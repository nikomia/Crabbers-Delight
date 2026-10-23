package alabaster.crabbersdelight.common.block.entity;

import alabaster.crabbersdelight.common.block.entity.inventory.WormBinItemHandler;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import alabaster.crabbersdelight.common.registry.CDModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class WormBinBlockEntity extends BlockEntity {
    public static final int DEGRADE_TIME = 1200;
    public static final int MAX_WORMS = 16;
    private static final float PARTICLE_CHANCE = 0.1f;
    // Each roll adds a worm with the item's composting chance, keeping the old "1 + 2 * chance" average yield
    private static final int COMPOST_ROLLS = 2;

    private final WormBinItemHandler handler = new WormBinItemHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int progress = 0;

    public WormBinBlockEntity(BlockPos pos, BlockState state) {
        super(CDModBlockEntity.WORM_BIN.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WormBinBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack fuel = be.handler.getStackInSlot(WormBinItemHandler.FUEL_SLOT);

        if (fuel.isEmpty()) {
            be.progress = 0;
        } else {
            be.progress++;
            if (be.progress >= DEGRADE_TIME) {
                be.progress = 0;

                int yield = 1 + rollCompostLayers(serverLevel, pos, state, fuel);
                ItemStack currentWorms = be.handler.getStackInSlot(WormBinItemHandler.WORM_SLOT);
                int newCount = Math.min(MAX_WORMS, currentWorms.getCount() + yield);
                be.handler.setStackInSlot(WormBinItemHandler.WORM_SLOT, new ItemStack(CDModItems.WORM.get(), newCount));
                be.handler.setStackInSlot(WormBinItemHandler.FUEL_SLOT, ItemStack.EMPTY);

                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        10, 0.25, 0.15, 0.25, 0.0);
            }
        }

        if (!be.handler.getStackInSlot(WormBinItemHandler.FUEL_SLOT).isEmpty() && level.getRandom().nextFloat() < PARTICLE_CHANCE) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PODZOL.defaultBlockState()),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    2, 0.2, 0.05, 0.2, 0.0);
        }
    }

    private static int rollCompostLayers(ServerLevel level, BlockPos pos, BlockState state, ItemStack fuel) {
        Compostable compostable = fuel.get(DataComponents.COMPOSTABLE);
        if (compostable == null) {
            return 0;
        }
        LootContext context = new LootContext.Builder(new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.BLOCK_INTERACT))
                .create(Optional.empty());
        int total = 0;
        for (int i = 0; i < COMPOST_ROLLS; i++) {
            total += Math.max(0, compostable.layers().get(context, 0));
        }
        return total;
    }

    public static boolean isValidFuel(ItemStack stack) {
        return stack.has(DataComponents.COMPOSTABLE);
    }

    public boolean insertFuel(ItemStack stack) {
        if (!isValidFuel(stack) || !handler.getStackInSlot(WormBinItemHandler.FUEL_SLOT).isEmpty()) {
            return false;
        }
        handler.setStackInSlot(WormBinItemHandler.FUEL_SLOT, stack.copyWithCount(1));
        progress = 0;
        stack.shrink(1);
        setChanged();
        return true;
    }

    public ItemStack ejectFuel() {
        ItemStack fuel = handler.getStackInSlot(WormBinItemHandler.FUEL_SLOT);
        if (fuel.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack ejected = fuel.copy();
        handler.setStackInSlot(WormBinItemHandler.FUEL_SLOT, ItemStack.EMPTY);
        progress = 0;
        setChanged();
        return ejected;
    }

    public ItemStack collectWorms() {
        ItemStack worms = handler.getStackInSlot(WormBinItemHandler.WORM_SLOT);
        if (worms.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack collected = worms.copy();
        handler.setStackInSlot(WormBinItemHandler.WORM_SLOT, ItemStack.EMPTY);
        setChanged();
        return collected;
    }

    public WormBinItemHandler getHandler() {
        return handler;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            for (int slot = 0; slot < handler.getSlotCount(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        handler.serialize(output.child("Inventory"));
        output.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        handler.deserialize(input.childOrEmpty("Inventory"));
        progress = input.getIntOr("Progress", 0);
    }
}
