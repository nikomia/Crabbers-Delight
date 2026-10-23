package alabaster.crabbersdelight;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.registry.*;
import alabaster.crabbersdelight.common.worldgen.CDBiomeModifiers;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrabbersDelight.MODID)
public class CrabbersDelight {
    public static final String MODID = "crabbersdelight";
    public static final Logger LOGGER = LogManager.getLogger();

    public CrabbersDelight(IEventBus bus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.LOCAL, Config.COMMON_CONFIG);

        CDModBlocks.BLOCKS.register(bus);
        CDModItems.ITEMS.register(bus);
        CDModBlockEntity.BLOCK_ENTITY_TYPES.register(bus);
        CDModDataComponents.DATA_COMPONENTS.register(bus);
        CDModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        CDModMenus.MENU.register(bus);
        CDModCreativeTabs.CREATIVE_TAB.register(bus);
        CDModPotions.POTIONS.register(bus);
        CDModFluids.FLUID_TYPES.register(bus);
        CDModFluids.FLUIDS.register(bus);
        CDModEntities.ENTITY_TYPES.register(bus);
        CDModFeatures.FEATURES.register(bus);
        CDModFeatures.PLACEMENT_MODIFIERS.register(bus);
        CDModFeatures.STATE_PROVIDERS.register(bus);
        CDModFeatures.TRUNK_PLACERS.register(bus);
        CDModFeatures.FOLIAGE_PLACERS.register(bus);
        CDBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(bus);
    }

    public static Identifier modPrefix(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
