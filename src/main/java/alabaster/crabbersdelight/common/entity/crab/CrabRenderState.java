package alabaster.crabbersdelight.common.entity.crab;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class CrabRenderState extends LivingEntityRenderState {
    public CrabVariant variant = CrabVariant.BLUE;
    public final AnimationState idleAnimationState = new AnimationState();
}
