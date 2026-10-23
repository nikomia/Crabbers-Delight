package alabaster.crabbersdelight.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A bait and the catches it can attract in a crab trap.
 */
public record CrabTrapRecipeWrapper(ItemStack input, List<ItemStack> outputs) {
}
