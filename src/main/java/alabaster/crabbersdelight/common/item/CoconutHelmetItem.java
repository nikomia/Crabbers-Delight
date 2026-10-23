package alabaster.crabbersdelight.common.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Head slot item; equipping, armor and repair are handled by item components.
 */
public class CoconutHelmetItem extends Item {
    private static final int DAMAGE_INTERVAL_TICKS = 600; // 30 seconds
    private static final int EFFECT_DURATION = 10;

    public CoconutHelmetItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (slot != EquipmentSlot.HEAD || !(entity instanceof Player player)) return;

        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, EFFECT_DURATION, 0, false, false, true));
        if (level.getGameTime() % DAMAGE_INTERVAL_TICKS == 0) {
            stack.hurtAndBreak(1, player, EquipmentSlot.HEAD);
        }
    }
}
