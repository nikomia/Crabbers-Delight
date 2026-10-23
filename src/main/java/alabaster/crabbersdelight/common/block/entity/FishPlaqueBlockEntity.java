package alabaster.crabbersdelight.common.block.entity;

import alabaster.crabbersdelight.common.block.FishPlaqueBlock;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class FishPlaqueBlockEntity extends BlockEntity {

    private @Nullable EntityType<?> entityType = null;
    private CompoundTag entityData = new CompoundTag();
    private ItemStack sourceBucket = ItemStack.EMPTY;
    private int dataVersion = 0;

    public FishPlaqueBlockEntity(BlockPos pos, BlockState state) {
        super(CDModBlockEntity.FISH_PLAQUE.get(), pos, state);
    }

    public boolean hasFish() {
        return entityType != null;
    }

    public @Nullable EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundTag getEntityData() {
        return entityData;
    }

    public ItemStack getSourceBucket() {
        return sourceBucket;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setFishData(EntityType<?> type, CompoundTag data, ItemStack sourceBucket) {
        this.entityType = type;
        this.entityData = data != null ? data : new CompoundTag();
        this.sourceBucket = sourceBucket != null ? sourceBucket.copy() : ItemStack.EMPTY;
        this.dataVersion++;
        setChanged();
    }

    public void clearFish() {
        this.entityType = null;
        this.entityData = new CompoundTag();
        this.sourceBucket = ItemStack.EMPTY;
        this.dataVersion++;
        setChanged();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && hasFish()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), FishPlaqueBlock.buildReturnBucket(this));
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (entityType != null) {
            output.putString("EntityType", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
            if (!entityData.isEmpty()) {
                output.store("EntityData", CompoundTag.CODEC, entityData.copy());
            }
            if (!sourceBucket.isEmpty()) {
                output.store("SourceBucket", ItemStack.CODEC, sourceBucket);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.dataVersion++;

        Identifier typeId = input.getString("EntityType").map(Identifier::tryParse).orElse(null);
        if (typeId != null) {
            this.entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(typeId).orElse(null);
            this.entityData = input.read("EntityData", CompoundTag.CODEC).map(CompoundTag::copy).orElseGet(CompoundTag::new);
            this.sourceBucket = input.read("SourceBucket", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        } else {
            this.entityType = null;
            this.entityData = new CompoundTag();
            this.sourceBucket = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
