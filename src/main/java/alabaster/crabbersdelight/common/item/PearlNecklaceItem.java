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

public class PearlNecklaceItem extends Item {
    private static final int TICK_INTERVAL = 100; // 5 seconds
    private static final int EFFECT_DURATION = 10;

    public PearlNecklaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST || !(entity instanceof Player player)) return;

        if (player.isInWaterOrRain()) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, EFFECT_DURATION, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, EFFECT_DURATION, 0, false, false, true));

            if (level.getGameTime() % TICK_INTERVAL == 0) {
                stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
            }
        }
    }
}
