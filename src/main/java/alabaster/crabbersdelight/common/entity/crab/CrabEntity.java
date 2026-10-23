package alabaster.crabbersdelight.common.entity.crab;

import alabaster.crabbersdelight.common.registry.CDModEntities;
import alabaster.crabbersdelight.common.registry.CDModItems;
import alabaster.crabbersdelight.common.tags.CDModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

public class CrabEntity extends Animal implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.BYTE);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public CrabEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.75, stack -> stack.is(CDModTags.CRAB_TEMPT_ITEM), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1);
    }

    @Override
    public int getMaxAirSupply() {
        return 100;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        // Crabs never drown
        if (!this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    @Override
    protected void doPush(Entity entity) {
        super.doPush(entity);
        if (entity instanceof CrabEntity) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            boolean nextToWall = false;
            BlockPos pos = this.blockPosition();
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (this.level().getBlockState(pos.relative(dir)).isSolid()) {
                    nextToWall = true;
                    break;
                }
            }
            this.setClimbing(nextToWall);
        }
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b = this.entityData.get(DATA_FLAGS_ID);
        b = climbing ? (byte) (b | 1) : (byte) (b & -2);
        this.entityData.set(DATA_FLAGS_ID, b);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        Holder<Biome> holder = level.getBiome(this.blockPosition());
        if (holder.is(Biomes.BEACH)) {
            this.setVariant(CrabVariant.RED);
        } else if (holder.is(Biomes.SWAMP)) {
            this.setVariant(CrabVariant.GREEN);
        } else if (holder.is(Biomes.STONY_SHORE)) {
            this.setVariant(CrabVariant.LIGHT_GRAY);
        } else if (holder.is(Biomes.SNOWY_BEACH)) {
            this.setVariant(CrabVariant.WHITE);
        } else {
            this.setVariant(CrabVariant.BLUE);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    public static boolean checkCrabSpawnRules(EntityType<CrabEntity> type, ServerLevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(CDModTags.CRAB_SPAWN_ON);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(FROM_BUCKET, false);
        builder.define(DATA_FLAGS_ID, (byte) 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public CrabVariant getVariant() {
        return CrabVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CrabVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getTypeVariant());
        output.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VARIANT, input.getIntOr("Variant", 0));
        this.entityData.set(FROM_BUCKET, input.getBooleanOr("FromBucket", false));
    }

    @Override
    public @Nullable CrabEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        CrabEntity baby = CDModEntities.CRAB.get().create(level, EntitySpawnReason.BREEDING);
        if (baby == null || !(otherParent instanceof CrabEntity otherCrab)) return baby;

        DyeColor mixed = DyeColor.getMixedColor(level, this.getVariant().getDyeColor(), otherCrab.getVariant().getDyeColor());
        baby.setVariant(CrabVariant.fromDyeColor(mixed));
        return baby;
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(CDModTags.CRAB_TEMPT_ITEM);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
            tag.putInt("Age", this.getAge());
            tag.putInt("Variant", this.getTypeVariant());
        });
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        tag.getInt("Age").ifPresent(this::setAge);
        tag.getInt("Variant").ifPresent(variant -> this.entityData.set(VARIANT, variant));
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(CDModItems.CRAB_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_AXOLOTL;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        var result = Bucketable.bucketMobPickup(player, hand, this);
        if (result.isPresent()) {
            return result.get();
        }

        ItemStack itemStack = player.getItemInHand(hand);
        DyeColor dyeColor = itemStack.get(DataComponents.DYE);
        if (dyeColor != null) {
            CrabVariant newVariant = CrabVariant.fromDyeColor(dyeColor);
            if (newVariant != this.getVariant()) {
                this.setVariant(newVariant);
                itemStack.consume(1, player);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 60;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }
}
