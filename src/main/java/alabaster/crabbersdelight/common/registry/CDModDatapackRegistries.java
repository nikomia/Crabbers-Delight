package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.common.world.BottleMessageEntry;
import alabaster.crabbersdelight.CrabbersDelight;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;

@EventBusSubscriber(modid = CrabbersDelight.MODID)
public class CDModDatapackRegistries {
    public static final ResourceKey<Registry<BottleMessageEntry>> BOTTLE_MESSAGE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(CrabbersDelight.modPrefix("bottle_message"));

    @SubscribeEvent
    public static void registerDatapackRegistries(NewDatapackRegistryEvent event) {
        event.worldRegistry(BOTTLE_MESSAGE_REGISTRY_KEY, BottleMessageEntry.CODEC, BottleMessageEntry.CODEC);
    }
}
