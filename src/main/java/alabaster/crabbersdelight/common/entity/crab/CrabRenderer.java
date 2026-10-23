package alabaster.crabbersdelight.common.entity.crab;

import alabaster.crabbersdelight.CrabbersDelight;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

public class CrabRenderer extends MobRenderer<CrabEntity, CrabRenderState, CrabModel> {
    private static final Map<CrabVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CrabVariant.class), map -> {
                map.put(CrabVariant.BLACK, CrabbersDelight.modPrefix("textures/entity/black_crab.png"));
                map.put(CrabVariant.BLUE, CrabbersDelight.modPrefix("textures/entity/blue_crab.png"));
                map.put(CrabVariant.BROWN, CrabbersDelight.modPrefix("textures/entity/brown_crab.png"));
                map.put(CrabVariant.CYAN, CrabbersDelight.modPrefix("textures/entity/cyan_crab.png"));
                map.put(CrabVariant.GRAY, CrabbersDelight.modPrefix("textures/entity/gray_crab.png"));
                map.put(CrabVariant.GREEN, CrabbersDelight.modPrefix("textures/entity/green_crab.png"));
                map.put(CrabVariant.LIGHT_BLUE, CrabbersDelight.modPrefix("textures/entity/light_blue_crab.png"));
                map.put(CrabVariant.LIGHT_GRAY, CrabbersDelight.modPrefix("textures/entity/light_gray_crab.png"));
                map.put(CrabVariant.LIME, CrabbersDelight.modPrefix("textures/entity/lime_crab.png"));
                map.put(CrabVariant.MAGENTA, CrabbersDelight.modPrefix("textures/entity/magenta_crab.png"));
                map.put(CrabVariant.ORANGE, CrabbersDelight.modPrefix("textures/entity/orange_crab.png"));
                map.put(CrabVariant.PINK, CrabbersDelight.modPrefix("textures/entity/pink_crab.png"));
                map.put(CrabVariant.PURPLE, CrabbersDelight.modPrefix("textures/entity/purple_crab.png"));
                map.put(CrabVariant.RED, CrabbersDelight.modPrefix("textures/entity/red_crab.png"));
                map.put(CrabVariant.WHITE, CrabbersDelight.modPrefix("textures/entity/white_crab.png"));
                map.put(CrabVariant.YELLOW, CrabbersDelight.modPrefix("textures/entity/yellow_crab.png"));
            });

    public CrabRenderer(EntityRendererProvider.Context context) {
        super(context, new CrabModel(context.bakeLayer(CrabModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public CrabRenderState createRenderState() {
        return new CrabRenderState();
    }

    @Override
    public void extractRenderState(CrabEntity entity, CrabRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.variant = entity.getVariant();
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }

    @Override
    public Identifier getTextureLocation(CrabRenderState state) {
        return LOCATION_BY_VARIANT.get(state.variant);
    }

    @Override
    protected void scale(CrabRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        if (state.isBaby) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
    }
}
