package alabaster.crabbersdelight.client.renderer;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.inventory.TackleBoxItemHandler;
import alabaster.crabbersdelight.common.item.LureItem;
import alabaster.crabbersdelight.common.utils.TackleBoxProximity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;

/**
 * Vanilla bobber, but textured after the lure in the owner's tackle box.
 */
public class LureAwareFishingHookRenderer extends FishingHookRenderer {
    private static final RenderType DEFAULT_RENDER_TYPE = RenderTypes.entityCutoutCull(Identifier.withDefaultNamespace("textures/entity/fishing/fishing_hook.png"));
    private static final RenderType BARBED_RENDER_TYPE = lure("barbed");
    private static final RenderType DOUBLE_RENDER_TYPE = lure("double");
    private static final RenderType MAGNETIC_RENDER_TYPE = lure("magnetic");
    private static final RenderType AUTOMATIC_RENDER_TYPE = lure("automatic");
    private static final RenderType STORM_RENDER_TYPE = lure("storm");

    private static RenderType lure(String name) {
        return RenderTypes.entityCutoutCull(CrabbersDelight.modPrefix("textures/entity/fishing_hook_" + name + ".png"));
    }

    public static class State extends FishingHookRenderState {
        RenderType renderType = DEFAULT_RENDER_TYPE;
    }

    public LureAwareFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FishingHookRenderState createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FishingHook entity, FishingHookRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof State lureState) {
            Player owner = entity.getPlayerOwner();
            lureState.renderType = owner != null ? resolveRenderType(owner) : DEFAULT_RENDER_TYPE;
        }
    }

    private static RenderType resolveRenderType(Player player) {
        TackleBoxProximity.TackleBoxAccess tackleBox = TackleBoxProximity.find(player);
        if (tackleBox == null) {
            return DEFAULT_RENDER_TYPE;
        }
        ItemStack lure = tackleBox.getSlot(TackleBoxItemHandler.LURE_SLOT);
        if (!(lure.getItem() instanceof LureItem lureItem)) {
            return DEFAULT_RENDER_TYPE;
        }
        return switch (lureItem.getEffect()) {
            case BARBED -> BARBED_RENDER_TYPE;
            case DOUBLE -> DOUBLE_RENDER_TYPE;
            case MAGNETIC -> MAGNETIC_RENDER_TYPE;
            case AUTOMATIC -> AUTOMATIC_RENDER_TYPE;
            case STORM -> STORM_RENDER_TYPE;
        };
    }

    // Same geometry as the vanilla renderer, only the bobber render type differs
    @Override
    public void submit(FishingHookRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        RenderType renderType = state instanceof State lureState ? lureState.renderType : DEFAULT_RENDER_TYPE;
        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.rotate(camera.orientation);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();
        float xa = (float) state.lineOriginOffset.x;
        float ya = (float) state.lineOriginOffset.y;
        float za = (float) state.lineOriginOffset.z;
        float width = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
            for (int i = 0; i < 16; i++) {
                float a0 = i / 16.0F;
                float a1 = (i + 1) / 16.0F;
                stringVertex(xa, ya, za, buffer, pose, a0, a1, width);
                stringVertex(xa, ya, za, buffer, pose, a1, a0, width);
            }
        });
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, int lightCoords, float x, int y, int u, int v) {
        builder.addVertex(pose, x - 0.5F, y - 0.5F, 0.0F)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(float xa, float ya, float za, VertexConsumer buffer, PoseStack.Pose pose, float aa, float nexta, float width) {
        float x = xa * aa;
        float y = ya * (aa * aa + aa) * 0.5F + 0.25F;
        float z = za * aa;
        float nx = xa * nexta - x;
        float ny = ya * (nexta * nexta + nexta) * 0.5F + 0.25F - y;
        float nz = za * nexta - z;
        float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        buffer.addVertex(pose, x, y, z).setColor(-16777216).setNormal(pose, nx / length, ny / length, nz / length).setLineWidth(width);
    }
}
