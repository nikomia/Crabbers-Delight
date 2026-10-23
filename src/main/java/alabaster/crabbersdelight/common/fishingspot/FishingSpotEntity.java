package alabaster.crabbersdelight.common.fishingspot;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FishingSpotEntity extends Entity {
    private static final EntityDataAccessor<Integer> RADIUS =
            SynchedEntityData.defineId(FishingSpotEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFETIME_TICKS =
            SynchedEntityData.defineId(FishingSpotEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DISTURBED =
            SynchedEntityData.defineId(FishingSpotEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int EXPIRE_FADE_TICKS = 20;
    public static final int FADE_IN_TICKS = 20;

    private int age = 0;

    public FishingSpotEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        if (random.nextFloat() < 0.16f) {
            spawnParticles(ParticleTypes.BUBBLE_POP, 3 + random.nextInt(3));
        }
        if (random.nextFloat() < 0.05f) {
            spawnParticles(ParticleTypes.FISHING, 1 + random.nextInt(2));
        }

        if (age >= getLifetimeTicks()) {
            discard();
        }
    }

    private void spawnParticles(net.minecraft.core.particles.SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = Math.sqrt(random.nextDouble()) * getRadius();
            double x = getX() + Math.cos(angle) * dist;
            double z = getZ() + Math.sin(angle) * dist;
            level().addParticle(type, x, getY() + 0.1, z, 0, 0.05, 0);
        }
    }

    public boolean contains(double x, double y, double z) {
        double dx = x - getX();
        double dz = z - getZ();
        return dx * dx + dz * dz <= (double) getRadius() * getRadius() && Math.abs(y - getY()) <= 2;
    }

    public void expireNow() {
        setLifetimeTicks(Math.min(getLifetimeTicks(), age + EXPIRE_FADE_TICKS));
    }

    public void disturb() {
        this.entityData.set(DISTURBED, true);
        expireNow();
    }

    public boolean isDisturbed() {
        return this.entityData.get(DISTURBED);
    }

    public int getRadius() {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(int radius) {
        this.entityData.set(RADIUS, radius);
    }

    public int getLifetimeTicks() {
        return this.entityData.get(LIFETIME_TICKS);
    }

    public void setLifetimeTicks(int ticks) {
        this.entityData.set(LIFETIME_TICKS, ticks);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RADIUS, 4);
        builder.define(LIFETIME_TICKS, 2400);
        builder.define(DISTURBED, false);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.age = input.getIntOr("Age", 0);
        setRadius(input.getIntOr("Radius", 4));
        setLifetimeTicks(input.getIntOr("LifetimeTicks", 2400));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Age", age);
        output.putInt("Radius", getRadius());
        output.putInt("LifetimeTicks", getLifetimeTicks());
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
