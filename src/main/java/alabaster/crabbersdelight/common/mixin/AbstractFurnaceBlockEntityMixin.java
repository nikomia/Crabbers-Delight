package alabaster.crabbersdelight.common.mixin;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.event.FishSizeEvents;
import alabaster.crabbersdelight.common.item.component.FishSize;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Furnaces, smokers and blast furnaces keep the fish size of the smelted fish.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Unique
    private static final int crabbersdelight$OUTPUT_SLOT = 2;

    @Unique
    private static FishSize crabbersdelight$capturedSize;

    @Unique
    private static final String crabbersdelight$BURN = "burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V";

    // Youer (CraftBukkit/Paper) replaces burn with a boolean variant taking the recipe, level and position.
    @Unique
    private static final String crabbersdelight$BURN_YOUER = "burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z";

    @Inject(method = crabbersdelight$BURN, at = @At("HEAD"), require = 0)
    private static void crabbersdelight$captureInputSize(NonNullList<ItemStack> items, ItemStack input, ItemStack result, CallbackInfo ci) {
        crabbersdelight$capturedSize = input.get(CDModDataComponents.FISH_SIZE.get());
    }

    @Inject(method = crabbersdelight$BURN, at = @At("RETURN"), require = 0)
    private static void crabbersdelight$applyOutputSize(NonNullList<ItemStack> items, ItemStack input, ItemStack result, CallbackInfo ci) {
        crabbersdelight$applySize(items);
    }

    @Inject(method = crabbersdelight$BURN_YOUER, at = @At("HEAD"), require = 0)
    private static void crabbersdelight$captureInputSizeYouer(NonNullList<ItemStack> items, ItemStack input, ItemStack result, RecipeHolder<? extends AbstractCookingRecipe> recipe, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        crabbersdelight$capturedSize = input.get(CDModDataComponents.FISH_SIZE.get());
    }

    @Inject(method = crabbersdelight$BURN_YOUER, at = @At("RETURN"), require = 0)
    private static void crabbersdelight$applyOutputSizeYouer(NonNullList<ItemStack> items, ItemStack input, ItemStack result, RecipeHolder<? extends AbstractCookingRecipe> recipe, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            crabbersdelight$applySize(items);
        } else {
            crabbersdelight$capturedSize = null;
        }
    }

    @Unique
    private static void crabbersdelight$applySize(NonNullList<ItemStack> items) {
        FishSize size = crabbersdelight$capturedSize;
        crabbersdelight$capturedSize = null;
        if (size == null || !Config.FISH_SIZE_ENABLED.get()) {
            return;
        }
        ItemStack output = items.get(crabbersdelight$OUTPUT_SLOT);
        if (!output.is(ItemTags.FISHES)) {
            return;
        }
        output.set(CDModDataComponents.FISH_SIZE.get(), size);
        FishSizeEvents.applyScaledFood(output, size);
    }
}
