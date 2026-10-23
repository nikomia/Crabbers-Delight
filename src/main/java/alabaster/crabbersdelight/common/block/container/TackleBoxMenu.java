package alabaster.crabbersdelight.common.block.container;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.inventory.TackleBoxItemHandler;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import alabaster.crabbersdelight.common.registry.CDModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TackleBoxMenu extends AbstractContainerMenu {
    public static final Identifier LURE_SLOT_ICON = CrabbersDelight.modPrefix("container/slot/lure");
    public static final Identifier LINE_SLOT_ICON = CrabbersDelight.modPrefix("container/slot/line");
    public static final Identifier BAIT_SLOT_ICON = CrabbersDelight.modPrefix("container/slot/bait");

    public final TackleBoxItemHandler inventory;
    private final ContainerLevelAccess access;

    public TackleBoxMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, new TackleBoxItemHandler(), ContainerLevelAccess.NULL);
    }

    public TackleBoxMenu(int id, Inventory playerInv, TackleBoxItemHandler inventory, ContainerLevelAccess access) {
        super(CDModMenus.TACKLE_BOX_MENU.get(), id);
        this.inventory = inventory;
        this.access = access;

        int startX = 8;
        int borderSlotSize = 18;
        int topY = 18;

        this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, TackleBoxItemHandler.LURE_SLOT, 19, 22).setBackground(LURE_SLOT_ICON));
        this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, TackleBoxItemHandler.LINE_SLOT, 47, 22).setBackground(LINE_SLOT_ICON));
        this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, TackleBoxItemHandler.BAIT_SLOT_1, 19, 50).setBackground(BAIT_SLOT_ICON));
        this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, TackleBoxItemHandler.BAIT_SLOT_2, 47, 50).setBackground(BAIT_SLOT_ICON));

        int generalStartX = startX + borderSlotSize * 2 + 4 + 30 + 2;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, row * 5 + column, generalStartX + column * borderSlotSize, topY + row * borderSlotSize));
            }
        }

        int startPlayerInvY = 84;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInv, column, startX + (column * borderSlotSize), 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int size = this.inventory.getSlotCount();
            if (index < size) {
                if (!this.moveItemStackTo(stack, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, CDModBlocks.TACKLE_BOX.get());
    }
}
