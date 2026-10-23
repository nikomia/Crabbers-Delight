package alabaster.crabbersdelight.common.mixin;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.utils.CuttingBoardSizeContext;
import alabaster.crabbersdelight.common.item.component.FishSize;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

/**
 * Bigger fish yield more slices on the cutting board.
 */
@Mixin(value = CuttingBoardRecipe.class, remap = false)
public abstract class CuttingBoardRecipeMixin {

    @Inject(method = "rollResults", at = @At("RETURN"))
    private void crabbersdelight$scaleByFishSize(RandomSource random, int fortuneLevel, CallbackInfoReturnable<List<ItemStack>> cir) {
        FishSize size = CuttingBoardSizeContext.get();
        if (size == null || !Config.FISH_SIZE_ENABLED.get()) {
            return;
        }
        for (ItemStack stack : cir.getReturnValue()) {
            stack.setCount(Math.max(1, Math.round(stack.getCount() * size.getMultiplier())));
        }
    }
}
