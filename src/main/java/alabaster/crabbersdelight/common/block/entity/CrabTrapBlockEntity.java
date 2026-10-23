package alabaster.crabbersdelight.common.block.entity;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.block.container.CrabTrapMenu;
import alabaster.crabbersdelight.common.block.entity.inventory.CrabTrapItemHandler;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import alabaster.crabbersdelight.common.tags.CDModTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class CrabTrapBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public static final Component CRAB_TRAP_NAME = Component.translatable("block.crabbersdelight.crab_trap");

    private final CrabTrapItemHandler handler = new CrabTrapItemHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    private int tickCounter = 0;

    public CrabTrapBlockEntity(BlockPos pos, BlockState state) {
        super(CDModBlockEntity.CRAB_TRAP.get(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        handler.serialize(output.child("handler"));
        output.putInt("tickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        handler.deserialize(input.childOrEmpty("handler"));
        tickCounter = input.getIntOr("tickCounter", 0);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public static Pair<Integer, Integer> getMinMax() {
        return Pair.of(Config.MIN_TICKS.get(), Config.MAX_TICKS.get());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrabTrapBlockEntity blockEntity) {
        RandomSource random = level.getRandom();
        if (getMinMax().getSecond() <= getMinMax().getFirst()) {
            CrabbersDelight.LOGGER.error("Error: Minimum value is higher than maximum value!");
            return;
        }
        if (blockEntity.tickCounter >= random.nextIntBetweenInclusive(getMinMax().getFirst(), getMinMax().getSecond())) {
            blockEntity.tickCounter = 0;
            if (isSurroundedByWater(level, pos) && isValidFishingLocation(level, pos) && level instanceof ServerLevel serverLevel) {
                ItemStack bait = blockEntity.handler.getStackInSlot(0);
                if (bait.is(CDModTags.CRAB_TRAP_BAIT)) {
                    LootParams params = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .create(LootContextParamSets.FISHING);
                    Identifier itemId = BuiltInRegistries.ITEM.getKey(bait.getItem());
                    Identifier tableId = CrabbersDelight.modPrefix("gameplay/crab_trap_loot/" + itemId.getPath());
                    LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, tableId));
                    List<ItemStack> loot = table.getRandomItems(params);
                    blockEntity.handler.addItemsAndShrinkBait(level, pos, loot, bait, random);
                }
            }
        } else {
            if (isWaterBiome(level, pos)) {
                blockEntity.tickCounter++;
            }
            blockEntity.tickCounter++;
        }
    }

    private static boolean isValidFishingLocation(Level level, BlockPos pos) {
        if (!level.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSurroundedByWater(Level level, BlockPos pos) {
        if (Config.REQUIRE_SURROUNDING_WATER.get()) {
            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
                if (!level.getFluidState(nearbyPos).is(FluidTags.WATER)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isWaterBiome(Level level, BlockPos pos) {
        return level.getBiome(pos).is(Tags.Biomes.IS_AQUATIC);
    }

    public CrabTrapItemHandler getInventory() {
        return this.handler;
    }

    @Override
    public Component getName() {
        return CRAB_TRAP_NAME;
    }

    @Override
    public Component getDisplayName() {
        return CRAB_TRAP_NAME;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CrabTrapMenu(id, playerInv, this.handler, ContainerLevelAccess.create(level, worldPosition));
    }
}
