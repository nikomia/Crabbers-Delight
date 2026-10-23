package alabaster.crabbersdelight.client.gui;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.container.CrabTrapMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CrabTrapGUI extends AbstractContainerScreen<CrabTrapMenu> {
    private static final Identifier CRAB_TRAP_GUI = CrabbersDelight.modPrefix("textures/gui/crab_trap.png");

    public CrabTrapGUI(CrabTrapMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, 176, 147);
        this.inventoryLabelY = 54;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAB_TRAP_GUI, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
