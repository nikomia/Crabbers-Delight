package alabaster.crabbersdelight.common.block.container;

import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.refabricated.inventory.ItemHandlerSlot;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

import java.util.function.IntConsumer;

/**
 * Slot editing in place (quick move, splitting) bypasses the handler, so the owner is notified explicitly.
 */
public class NotifyingSlot extends ItemHandlerSlot {
    private final IntConsumer onChanged;

    public NotifyingSlot(ItemStackHandler handler, IntConsumer onChanged, int index, int x, int y) {
        super(handler, index, x, y);
        this.onChanged = onChanged;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        onChanged.accept(getContainerSlot());
    }

    @Override
    public void onTake(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        super.onTake(player, stack);
        onChanged.accept(getContainerSlot());
    }
}
