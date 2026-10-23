package alabaster.crabbersdelight.common.mixin;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.event.FishSizeEvents;
import alabaster.crabbersdelight.common.item.component.FishSize;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Campfires cook through assemble(); keep the fish size on the cooked fish.
 */
@Mixin(SingleItemRecipe.class)
public abstract class SingleItemRecipeMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void crabbersdelight$scaleByFishSize(SingleRecipeInput input, CallbackInfoReturnable<ItemStack> cir) {
        if (!((Object) this instanceof CampfireCookingRecipe) || !Config.FISH_SIZE_ENABLED.get()) {
            return;
        }
        FishSize size = input.item().get(CDModDataComponents.FISH_SIZE.get());
        ItemStack output = cir.getReturnValue();
        if (size == null || !output.is(ItemTags.FISHES)) {
            return;
        }
        output.set(CDModDataComponents.FISH_SIZE.get(), size);
        FishSizeEvents.applyScaledFood(output, size);
    }
}
