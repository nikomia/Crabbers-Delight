package alabaster.crabbersdelight.common.entity.crab;

import alabaster.crabbersdelight.CrabbersDelight;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CrabModel extends EntityModel<CrabRenderState> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(CrabbersDelight.modPrefix("crab"), "main");

	// The model is authored facing sideways; crabs walk that way
	private static final float SIDEWAYS_ROTATION = (float) Math.PI / 2F;

	private final ModelPart crab;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation idleAnimation;

	public CrabModel(ModelPart root) {
		super(root);
		this.crab = root.getChild("crab");
		this.walkAnimation = CrabAnimations.walk.bake(root);
		this.idleAnimation = CrabAnimations.idle.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition crab = partdefinition.addOrReplaceChild("crab", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, 0.5F));

		PartDefinition fullbody = crab.addOrReplaceChild("fullbody", CubeListBuilder.create(), PartPose.offset(-3.0F, -1.5F, -3.5F));

		PartDefinition eye = fullbody.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(5, 4).addBox(5.5F, -1.5F, 0.0F, 1.0F, 1.0F, 0.01F, new CubeDeformation(0.0F))
				.texOffs(5, 5).addBox(-0.5F, -1.5F, 0.0F, 1.0F, 1.0F, 0.01F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bigclaw = fullbody.addOrReplaceChild("bigclaw", CubeListBuilder.create(), PartPose.offset(8.5165F, 0.0121F, 4.7325F));

		PartDefinition bigclawbottom = bigclaw.addOrReplaceChild("bigclawbottom", CubeListBuilder.create(), PartPose.offset(0.2405F, -0.0456F, -0.4621F));

		PartDefinition bigclawbottom_r1 = bigclawbottom.addOrReplaceChild("bigclawbottom_r1", CubeListBuilder.create().texOffs(16, 15).addBox(-1.9052F, 0.1072F, -6.8775F, 4.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1835F, -0.5658F, 0.0179F));

		PartDefinition bigclawtop = bigclaw.addOrReplaceChild("bigclawtop", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bigclawtop_r1 = bigclawtop.addOrReplaceChild("bigclawtop_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-1.9506F, -2.8472F, -7.3965F, 4.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1835F, -0.5658F, 0.0179F));

		PartDefinition smallclaw = fullbody.addOrReplaceChild("smallclaw", CubeListBuilder.create(), PartPose.offset(-2.5396F, 1.2184F, 4.2473F));

		PartDefinition smallclaw_r1 = smallclaw.addOrReplaceChild("smallclaw_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-1.2717F, -1.1118F, -4.1902F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1835F, 0.5658F, -0.0179F));

		PartDefinition antenna = fullbody.addOrReplaceChild("antenna", CubeListBuilder.create(), PartPose.offset(4.0F, -0.0231F, -0.1503F));

		PartDefinition rantenna = antenna.addOrReplaceChild("rantenna", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition rantenna_r1 = rantenna.addOrReplaceChild("rantenna_r1", CubeListBuilder.create().texOffs(4, 2).addBox(-0.5F, -1.0F, -0.2F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0231F, 0.1503F, -0.3054F, 0.0F, 0.0F));

		PartDefinition rantenna2 = antenna.addOrReplaceChild("rantenna2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rantenna2_r1 = rantenna2.addOrReplaceChild("rantenna2_r1", CubeListBuilder.create().texOffs(4, 2).addBox(-0.5F, -1.0F, -0.2F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0231F, 0.1503F, -0.3054F, 0.0F, 0.0F));

		PartDefinition bodybase = fullbody.addOrReplaceChild("bodybase", CubeListBuilder.create().texOffs(0, 1).mirror().addBox(-4.5F, -2.0F, -3.5F, 9.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.0F, 1.5F, 3.5F));

		PartDefinition legs = crab.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(-4.9759F, 2.5095F, 0.0F));

		PartDefinition rlegs = legs.addOrReplaceChild("rlegs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rleg1 = rlegs.addOrReplaceChild("rleg1", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, -2.0F));

		PartDefinition rleg1_r1 = rleg1.addOrReplaceChild("rleg1_r1", CubeListBuilder.create().texOffs(-1, 0).addBox(-2.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.829F));

		PartDefinition rleg2 = rlegs.addOrReplaceChild("rleg2", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, 0.0F));

		PartDefinition rleg2_r1 = rleg2.addOrReplaceChild("rleg2_r1", CubeListBuilder.create().texOffs(-1, 1).addBox(-2.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.829F));

		PartDefinition rleg3 = rlegs.addOrReplaceChild("rleg3", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, 2.0F));

		PartDefinition rleg3_r1 = rleg3.addOrReplaceChild("rleg3_r1", CubeListBuilder.create().texOffs(-1, 0).addBox(-2.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.829F));

		PartDefinition llegs = legs.addOrReplaceChild("llegs", CubeListBuilder.create(), PartPose.offset(9.9518F, 0.0F, 0.0F));

		PartDefinition lleg1 = llegs.addOrReplaceChild("lleg1", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, -2.0F));

		PartDefinition lleg1_r1 = lleg1.addOrReplaceChild("lleg1_r1", CubeListBuilder.create().texOffs(-1, 0).addBox(0.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.829F));

		PartDefinition lleg2 = llegs.addOrReplaceChild("lleg2", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, 0.0F));

		PartDefinition lleg2_r1 = lleg2.addOrReplaceChild("lleg2_r1", CubeListBuilder.create().texOffs(-1, 1).addBox(0.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.829F));

		PartDefinition lleg3 = llegs.addOrReplaceChild("lleg3", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, 2.0F));

		PartDefinition lleg3_r1 = lleg3.addOrReplaceChild("lleg3_r1", CubeListBuilder.create().texOffs(-1, 0).addBox(0.4129F, -0.0617F, -0.5F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.829F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(CrabRenderState state) {
		super.setupAnim(state);
		this.root().yRot = SIDEWAYS_ROTATION;
		float headYaw = Mth.clamp(state.yRot, -30f, 30f);
		float headPitch = Mth.clamp(state.xRot, -25f, 45f);
		this.crab.yRot = headYaw * Mth.DEG_TO_RAD;
		this.crab.xRot = headPitch * Mth.DEG_TO_RAD;
		this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 5f);
		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
	}
}
