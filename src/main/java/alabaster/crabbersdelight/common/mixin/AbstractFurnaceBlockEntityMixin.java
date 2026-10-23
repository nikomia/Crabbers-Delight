package alabaster.crabbersdelight.common.mixin;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.event.FishSizeEvents;
import alabaster.crabbersdelight.common.item.component.FishSize;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Furnaces, smokers and blast furnaces keep the fish size of the smelted fish.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Unique
    private static final int crabbersdelight$OUTPUT_SLOT = 2;

    @Unique
    private static FishSize crabbersdelight$capturedSize;

    @Inject(method = "burn", at = @At("HEAD"))
    private static void crabbersdelight$captureInputSize(NonNullList<ItemStack> items, ItemStack input, ItemStack result, CallbackInfo ci) {
        crabbersdelight$capturedSize = input.get(CDModDataComponents.FISH_SIZE.get());
    }

    @Inject(method = "burn", at = @At("RETURN"))
    private static void crabbersdelight$applyOutputSize(NonNullList<ItemStack> items, ItemStack input, ItemStack result, CallbackInfo ci) {
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
