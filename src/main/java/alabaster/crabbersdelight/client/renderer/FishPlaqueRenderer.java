package alabaster.crabbersdelight.client.renderer;

import alabaster.crabbersdelight.common.block.FishPlaqueBlock;
import alabaster.crabbersdelight.common.block.entity.FishPlaqueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.axolotl.AdultAxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.AxolotlRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renders the mounted mob on a fish plaque.
 */
public class FishPlaqueRenderer implements BlockEntityRenderer<FishPlaqueBlockEntity, FishPlaqueRenderer.State> {

    private record MobOverride(float yaw, float roll, float pitch, float upDown, float leftRight, float frontBack) {
        static final MobOverride NONE = new MobOverride(0f, 0f, 0f, 0f, 0f, 0f);
    }

    private static final Map<Identifier, MobOverride> MOB_OVERRIDES = Map.ofEntries(
            // Vanilla
            Map.entry(id("minecraft", "cod"), new MobOverride(0f, 0f, 0f, 0.0625f, -0.0625f, -0.25f)),
            Map.entry(id("minecraft", "salmon"), new MobOverride(0f, 0f, 0f, 0.0625f, 0f, -0.1875f)),
            Map.entry(id("minecraft", "tropical_fish"), new MobOverride(0f, 0f, 0f, 0.0625f, -0.0625f, -0.25f)),
            Map.entry(id("minecraft", "squid"), new MobOverride(90f, -90f, 0f, 0.125f, -0.125f, -0.125f)),
            Map.entry(id("minecraft", "glow_squid"), new MobOverride(90f, -90f, 0f, 0.125f, -0.125f, -0.125f)),
            Map.entry(id("minecraft", "tadpole"), new MobOverride(0f, 180f, 0f, -0.125f, -0.0625f, -0.08f)),
            // Crabber's Delight
            Map.entry(id("crabbersdelight", "crab"), new MobOverride(-90f, 180f, -90f, -0.125f, -0.125f, -0.125f)),
            // Naturalist
            Map.entry(id("naturalist", "bass"), new MobOverride(0f, -90f, 0f, 0f, -0.125f, 0f)),
            Map.entry(id("naturalist", "catfish"), new MobOverride(0f, -90f, 0f, 0f, -0.0625f, 0.125f)),
            Map.entry(id("naturalist", "ray"), new MobOverride(90f, 90f, 90f, 0.0625f, 0.125f, -0.0625f)),
            Map.entry(id("naturalist", "starfish"), new MobOverride(90f, 90f, 90f, -0.0625f, 0.125f, -0.0625f)),
            Map.entry(id("naturalist", "giant_isopod"), new MobOverride(90f, 90f, 90f, -0.0625f, 0.125f, -0.0625f)),
            Map.entry(id("naturalist", "jellyfish"), new MobOverride(0f, -90f, 0f, -0.25f, -0.125f, 0f)),
            Map.entry(id("naturalist", "piranha"), new MobOverride(0f, -90f, 0f, 0f, -0.125f, 0f)),
            Map.entry(id("naturalist", "blobfish"), new MobOverride(0f, -90f, 0f, 0f, -0.125f, 0f)),
            Map.entry(id("naturalist", "anglerfish"), new MobOverride(0f, -90f, 0f, 0f, -0.125f, 0f)),
            // Upgrade Aquatic
            Map.entry(id("upgrade_aquatic", "perch"), new MobOverride(0f, 0f, 0f, 0f, -0.125f, -0.1875f)),
            Map.entry(id("upgrade_aquatic", "lionfish"), new MobOverride(0f, 0f, 0f, 0f, -0.125f, -0.1875f)),
            Map.entry(id("upgrade_aquatic", "pike"), new MobOverride(0f, 0f, 0f, 0f, 0f, -0.1875f)),
            Map.entry(id("upgrade_aquatic", "nautilus"), new MobOverride(0f, 0f, 0f, 0.125f, -0.125f, -0.125f))
    );

    private static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    enum Kind { NORMAL, PUFFERFISH, AXOLOTL_HEAD }

    public static class State extends BlockEntityRenderState {
        @Nullable EntityRenderState entity;
        @Nullable Identifier axolotlTexture;
        Kind kind = Kind.NORMAL;
        Direction facing = Direction.NORTH;
        MobOverride override = MobOverride.NONE;
        float scale;
    }

    private final EntityRenderDispatcher entityRenderer;
    // Own copy of the axolotl model: only its head is drawn and its pose never changes
    private final ModelPart axolotlHead;

    private final Map<Long, CachedFishEntity> entityCache = new LinkedHashMap<>(256, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, CachedFishEntity> eldest) {
            return size() > 256;
        }
    };

    public FishPlaqueRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
        this.axolotlHead = new AdultAxolotlModel(context.bakeLayer(ModelLayers.AXOLOTL)).root().getChild("body").getChild("head");
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FishPlaqueBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
        state.entity = null;
        state.axolotlTexture = null;
        if (!be.hasFish() || be.getLevel() == null) {
            entityCache.remove(be.getBlockPos().asLong());
            return;
        }

        Entity entity = getOrCreateEntity(be, be.getLevel());
        if (entity == null) {
            return;
        }

        state.facing = be.getBlockState().getValue(FishPlaqueBlock.FACING);
        entity.setYRot(0);
        entity.setXRot(0);
        entity.setYHeadRot(0);
        entity.setYBodyRot(0);
        if (entity instanceof Pufferfish puffer) {
            puffer.setPuffState(2);
            state.kind = Kind.PUFFERFISH;
        } else if (entity instanceof Axolotl) {
            state.kind = Kind.AXOLOTL_HEAD;
        } else {
            state.kind = Kind.NORMAL;
            state.override = MOB_OVERRIDES.getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), MobOverride.NONE);
            float scale = 0.53125f;
            float maxDim = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if (maxDim > 1.0f) {
                scale /= maxDim;
            }
            state.scale = scale;
        }

        EntityRenderState entityState = entityRenderer.extractEntity(entity, 0.0f);
        entityState.lightCoords = state.lightCoords;
        state.entity = entityState;
        if (state.kind == Kind.AXOLOTL_HEAD && entityState instanceof AxolotlRenderState axolotlState
                && entityRenderer.getRenderer(axolotlState) instanceof AxolotlRenderer axolotlRenderer) {
            state.axolotlTexture = axolotlRenderer.getTextureLocation(axolotlState);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.entity == null) {
            return;
        }
        Direction facing = state.facing;
        switch (state.kind) {
            case PUFFERFISH -> {
                float scale = 0.53125f * 1.5f;
                poseStack.pushPose();
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.translate(facing.getStepX() * -0.35f, 0.175, facing.getStepZ() * -0.35f);
                poseStack.rotate(Axis.YP.rotationDegrees(-facing.toYRot()));
                poseStack.translate(0, -0.02f, 0);
                poseStack.scale(scale, scale, scale);
                entityRenderer.submit(state.entity, camera, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
                poseStack.popPose();
            }
            case AXOLOTL_HEAD -> {
                if (state.axolotlTexture == null) {
                    return;
                }
                float scale = 0.53125f * 1.5f;
                float forwardOffset = -0.9f;
                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.translate(facing.getStepX() * forwardOffset, 0, facing.getStepZ() * forwardOffset);
                poseStack.rotate(Axis.YP.rotationDegrees(-facing.toYRot() + 180f));
                poseStack.translate(0, -0.02f, 0);
                poseStack.rotate(Axis.ZP.rotationDegrees(180f));
                poseStack.scale(scale, scale, scale);
                submitNodeCollector.submitModelPart(axolotlHead, poseStack, RenderTypes.entityCutout(state.axolotlTexture),
                        state.lightCoords, OverlayTexture.NO_OVERLAY, null);
                poseStack.popPose();
            }
            case NORMAL -> {
                MobOverride override = state.override;
                float scale = state.scale;
                float stepMultiplier = 0.25f;
                float yDegree = -facing.toYRot() + 90f + 180f + override.yaw();
                Direction right = facing.getClockWise();

                poseStack.pushPose();
                poseStack.translate(0.5, 0.0, 0.5);
                poseStack.translate(-facing.getStepX() * stepMultiplier, scale, -facing.getStepZ() * stepMultiplier);
                if (override.upDown() != 0f) {
                    poseStack.translate(0, override.upDown(), 0);
                }
                if (override.leftRight() != 0f) {
                    poseStack.translate(right.getStepX() * override.leftRight(), 0, right.getStepZ() * override.leftRight());
                }
                if (override.frontBack() != 0f) {
                    poseStack.translate(facing.getStepX() * override.frontBack(), 0, facing.getStepZ() * override.frontBack());
                }
                poseStack.rotate(Axis.YP.rotationDegrees(yDegree));
                poseStack.rotate(Axis.ZP.rotationDegrees(90f + override.roll()));
                if (override.pitch() != 0f) {
                    poseStack.rotate(Axis.XP.rotationDegrees(override.pitch()));
                }
                poseStack.translate(-0.125f, -0.16f, 0.125f);
                poseStack.scale(scale * 1.5f, scale * 1.5f, scale * 1.5f);
                entityRenderer.submit(state.entity, camera, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
                poseStack.popPose();
            }
        }
    }

    private @Nullable Entity getOrCreateEntity(FishPlaqueBlockEntity be, Level level) {
        long posKey = be.getBlockPos().asLong();
        CachedFishEntity cached = entityCache.get(posKey);
        if (cached != null && cached.version == be.getDataVersion() && cached.entityType == be.getEntityType()) {
            return cached.entity;
        }

        EntityType<?> type = be.getEntityType();
        if (type == null) {
            entityCache.remove(posKey);
            return null;
        }
        Entity entity = type.create(level, EntitySpawnReason.LOAD);
        if (entity == null) {
            return null;
        }

        CompoundTag data = be.getEntityData();
        if (!data.isEmpty()) {
            if (entity instanceof Bucketable bucketable) {
                bucketable.loadFromBucketTag(data);
            } else {
                entity.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
            }
        }
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }

        entityCache.put(posKey, new CachedFishEntity(be.getDataVersion(), type, entity));
        return entity;
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(FishPlaqueBlockEntity blockEntity) {
        return new net.minecraft.world.phys.AABB(blockEntity.getBlockPos()).inflate(1.0);
    }

    private record CachedFishEntity(int version, EntityType<?> entityType, Entity entity) {}
}
