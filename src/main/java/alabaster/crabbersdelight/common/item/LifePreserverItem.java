package alabaster.crabbersdelight.common.item;

import alabaster.crabbersdelight.common.registry.CDModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Placeable block that can also be worn in the legs slot (equippable component).
 */
public class LifePreserverItem extends BlockItem {
    public LifePreserverItem(Properties properties) {
        super(CDModBlocks.LIFE_PRESERVER.get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.crabbersdelight.life_preserver.desc").withStyle(ChatFormatting.GRAY));
    }
}
