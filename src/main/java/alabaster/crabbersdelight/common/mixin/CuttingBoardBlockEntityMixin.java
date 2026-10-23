package alabaster.crabbersdelight.common.mixin;

import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import alabaster.crabbersdelight.common.utils.CuttingBoardSizeContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

/**
 * The cutting board recipe no longer receives its input, so the size of the item on the board is handed over here.
 */
@Mixin(value = CuttingBoardBlockEntity.class, remap = false)
public abstract class CuttingBoardBlockEntityMixin {
    @Shadow
    public abstract ItemStack getStoredItem();

    @Inject(method = "processStoredItemUsingTool", at = @At("HEAD"))
    private void crabbersdelight$captureSize(ItemStack toolStack, Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingBoardSizeContext.set(getStoredItem().get(CDModDataComponents.FISH_SIZE.get()));
    }

    @Inject(method = "processStoredItemUsingTool", at = @At("RETURN"))
    private void crabbersdelight$clearSize(ItemStack toolStack, Player player, CallbackInfoReturnable<Boolean> cir) {
        CuttingBoardSizeContext.set(null);
    }
}
