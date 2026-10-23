package alabaster.crabbersdelight.client.renderer;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.LifePreserverBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.context.ContextKey;

/**
 * Draws a worn life preserver around the waist of any living entity.
 */
public class LifePreserverLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    // Filled by a render state modifier when the entity wears a life preserver
    public static final ContextKey<ItemStackRenderState> WORN_LIFE_PRESERVER = new ContextKey<>(CrabbersDelight.modPrefix("worn_life_preserver"));

    private static final float FLIP_ROTATION_DEGREES = 90f;
    private static final float RENDER_SCALE = 2.0f;
    private static final double RENDER_Y_OFFSET = 2.0 / 16.0;

    public LifePreserverLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        ItemStackRenderState item = state.getRenderData(WORN_LIFE_PRESERVER);
        if (item == null || item.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0, LifePreserverBlock.WAIST_HEIGHT - RENDER_Y_OFFSET, 0);
        poseStack.rotate(Axis.XP.rotationDegrees(FLIP_ROTATION_DEGREES));
        poseStack.scale(RENDER_SCALE, RENDER_SCALE, RENDER_SCALE);
        item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
