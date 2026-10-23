package alabaster.crabbersdelight.common.block.entity.inventory;

import alabaster.crabbersdelight.common.tags.CDModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

import java.util.List;

public class CrabTrapItemHandler extends ItemStackHandler {
    public static final int SLOTS = 10;

    public CrabTrapItemHandler() {
        super(SLOTS);
    }

    public void addItemsAndShrinkBait(Level level, BlockPos pos, List<ItemStack> lootList, ItemStack baitItem, RandomSource random) {
        boolean insertedAny = false;

        for (ItemStack lootStack : lootList) {
            if (lootStack.isEmpty()) continue;

            for (int slot = 1; slot < getSlotCount(); slot++) {
                ItemStack remainder = insertItem(slot, lootStack, false);
                if (remainder.getCount() != lootStack.getCount()) {
                    insertedAny = true;
                }
                lootStack = remainder;
                if (lootStack.isEmpty()) break;
            }

            if (!lootStack.isEmpty()) {
                continue;
            }

            if (baitItem.is(CDModTags.CRAB_TRAP_BAIT) && !baitItem.is(CDModTags.CHUMS)) {
                baitItem.shrink(1);
            } else if (baitItem.is(CDModTags.CHUMS)) {
                int damage = baitItem.getDamageValue() + 1;
                baitItem.setDamageValue(damage);
                if (damage >= baitItem.getMaxDamage()) {
                    baitItem.shrink(1);
                    this.insertItem(0, new ItemStack(Items.BUCKET), false);
                }
            }
            onContentsChanged(0);
        }

        if (insertedAny) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.FISH_SWIM, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return slot != 0 ? 8 : stack.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(CDModTags.CRAB_TRAP_BAIT);
        }
        return true;
    }

    public void notifyChanged(int slot) {
        onContentsChanged(slot);
    }
}
