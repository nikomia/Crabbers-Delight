package alabaster.crabbersdelight.common.block.container;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.inventory.CrabTrapItemHandler;
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

public class CrabTrapMenu extends AbstractContainerMenu {
    public static final Identifier BAIT_SLOT = CrabbersDelight.modPrefix("container/slot/bait");

    public final CrabTrapItemHandler inventory;
    private final ContainerLevelAccess access;

    public CrabTrapMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(id, playerInventory, new CrabTrapItemHandler(), ContainerLevelAccess.NULL);
    }

    public CrabTrapMenu(int id, Inventory playerInv, CrabTrapItemHandler inventory, ContainerLevelAccess access) {
        super(CDModMenus.CRAB_TRAP_MENU.get(), id);
        this.inventory = inventory;
        this.access = access;

        int startX = 8;
        int borderSlotSize = 18;

        // Bait Slot
        this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, 0, 80, 13).setBackground(BAIT_SLOT));

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new NotifyingSlot(inventory, inventory::notifyChanged, column + 1, 8 + column * 18, 34));
        }

        int startPlayerInvY = 65;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInv, column, startX + (column * borderSlotSize), 123));
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
        return stillValid(this.access, player, CDModBlocks.CRAB_TRAP.get());
    }
}
