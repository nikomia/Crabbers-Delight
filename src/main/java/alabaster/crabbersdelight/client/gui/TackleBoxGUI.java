package alabaster.crabbersdelight.client.gui;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.container.NotifyingSlot;
import alabaster.crabbersdelight.common.block.container.TackleBoxMenu;
import alabaster.crabbersdelight.common.block.entity.inventory.TackleBoxItemHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

public class TackleBoxGUI extends AbstractContainerScreen<TackleBoxMenu> {
    private static final Identifier TACKLE_BOX_GUI = CrabbersDelight.modPrefix("textures/gui/tackle_box.png");

    public TackleBoxGUI(TackleBoxMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, 176, 177);
        this.inventoryLabelY = 74;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TACKLE_BOX_GUI, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        // Explain the special slots while they are empty
        if (this.hoveredSlot instanceof NotifyingSlot && !this.hoveredSlot.hasItem()) {
            Component label = slotLabel(this.hoveredSlot.getContainerSlot());
            if (label != null) {
                graphics.setTooltipForNextFrame(label, mouseX, mouseY);
            }
        }
    }

    private static @Nullable Component slotLabel(int slotIndex) {
        if (slotIndex == TackleBoxItemHandler.LURE_SLOT) {
            return Component.translatable("gui.crabbersdelight.tackle_box.lure_slot");
        }
        if (slotIndex == TackleBoxItemHandler.LINE_SLOT) {
            return Component.translatable("gui.crabbersdelight.tackle_box.line_slot");
        }
        if (slotIndex == TackleBoxItemHandler.BAIT_SLOT_1 || slotIndex == TackleBoxItemHandler.BAIT_SLOT_2) {
            return Component.translatable("gui.crabbersdelight.tackle_box.bait_slot");
        }
        return null;
    }
}
