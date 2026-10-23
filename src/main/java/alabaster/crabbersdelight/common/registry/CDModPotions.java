package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CDModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, CrabbersDelight.MODID);

    public static final DeferredHolder<Potion, Potion> INKY_POTION = POTIONS.register("inky_potion",
            () -> new Potion("inky_potion", new MobEffectInstance(MobEffects.BLINDNESS, 300, 0)));
}
