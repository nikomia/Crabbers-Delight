package alabaster.crabbersdelight.common.recipe;

import alabaster.crabbersdelight.common.item.SignedNoteItem;
import alabaster.crabbersdelight.common.item.component.BottledNoteReward;
import alabaster.crabbersdelight.common.item.component.SignedNoteContent;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import alabaster.crabbersdelight.common.registry.CDModItems;
import alabaster.crabbersdelight.common.registry.CDModRecipeSerializers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Signed note + glass bottle (+ optional reward items of one kind) = bottled note.
 */
public class BottledNoteRecipe extends CustomRecipe {

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int bottleCount = 0;
        ItemStack signedNote = ItemStack.EMPTY;
        ItemStack rewardStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof SignedNoteItem) {
                if (!signedNote.isEmpty()) {
                    return false;
                }
                signedNote = stack;
            } else if (stack.is(Items.GLASS_BOTTLE)) {
                bottleCount++;
            } else if (rewardStack.isEmpty()) {
                rewardStack = stack;
            } else if (!rewardStack.is(stack.getItem())) {
                return false;
            }
        }

        return !signedNote.isEmpty() && bottleCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack signedNote = ItemStack.EMPTY;
        int bottleCount = 0;
        ItemStack rewardStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof SignedNoteItem) {
                if (!signedNote.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                signedNote = stack;
            } else if (stack.is(Items.GLASS_BOTTLE)) {
                bottleCount++;
            } else if (rewardStack.isEmpty()) {
                rewardStack = stack.copyWithCount(1);
            } else if (rewardStack.is(stack.getItem())) {
                rewardStack.grow(1);
            } else {
                return ItemStack.EMPTY;
            }
        }

        SignedNoteContent content = signedNote.get(CDModDataComponents.SIGNED_NOTE_CONTENT.get());
        if (signedNote.isEmpty() || bottleCount != 1 || content == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(CDModItems.BOTTLED_NOTE.get());
        result.set(CDModDataComponents.SIGNED_NOTE_CONTENT.get(), content);
        if (!rewardStack.isEmpty()) {
            result.set(CDModDataComponents.BOTTLED_NOTE_REWARD.get(), new BottledNoteReward(rewardStack));
        }
        return result;
    }

    @Override
    public RecipeSerializer<BottledNoteRecipe> getSerializer() {
        return CDModRecipeSerializers.BOTTLED_NOTE.get();
    }
}
