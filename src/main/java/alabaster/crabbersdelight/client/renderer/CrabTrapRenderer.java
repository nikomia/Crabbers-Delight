package alabaster.crabbersdelight.client.renderer;

import alabaster.crabbersdelight.common.block.CrabTrapBlock;
import alabaster.crabbersdelight.common.block.entity.CrabTrapBlockEntity;
import alabaster.crabbersdelight.common.block.entity.inventory.CrabTrapItemHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Shows the catch lying inside the trap.
 */
public class CrabTrapRenderer implements BlockEntityRenderer<CrabTrapBlockEntity, CrabTrapRenderer.State> {
    private static final int GRID_COLUMNS = 3;
    private static final double GRID_SPACING = 0.22;
    private static final double STAGGER_AMOUNT = 0.06;

    private final ItemModelResolver itemModelResolver;

    public CrabTrapRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        float yRot;
        final List<ItemStackRenderState> items = new ArrayList<>();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CrabTrapBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.yRot = blockEntity.getBlockState().getValue(CrabTrapBlock.FACING).toYRot();
        state.items.clear();
        CrabTrapItemHandler handler = blockEntity.getInventory();
        int seed = (int) blockEntity.getBlockPos().asLong();
        for (int slot = 1; slot < handler.getSlotCount(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            ItemStackRenderState itemState = new ItemStackRenderState();
            if (!stack.isEmpty()) {
                itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed + slot);
            }
            state.items.add(itemState);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (int index = 0; index < state.items.size(); index++) {
            ItemStackRenderState itemState = state.items.get(index);
            if (itemState.isEmpty()) {
                continue;
            }
            int column = index % GRID_COLUMNS;
            int row = index / GRID_COLUMNS;
            double offsetX = (column - 1) * GRID_SPACING;
            double offsetZ = (row - 1) * GRID_SPACING + staggerFor(index + 1);

            poseStack.pushPose();
            poseStack.translate(0.5, 0.3, 0.5);
            poseStack.rotate(Axis.YP.rotationDegrees(state.yRot));
            poseStack.translate(offsetX, 0, offsetZ);
            poseStack.scale(0.4f, 0.4f, 0.4f);
            itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    private static double staggerFor(int slot) {
        Random random = new Random(slot);
        double sign = random.nextBoolean() ? 1.0 : -1.0;
        return sign * STAGGER_AMOUNT * (0.4 + random.nextDouble() * 0.6);
    }
}
