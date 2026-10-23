package alabaster.crabbersdelight.common.worldgen;

import alabaster.crabbersdelight.CrabbersDelight;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CDBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, CrabbersDelight.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<CDConditionalSpawnModifier>> CONDITIONAL_SPAWN =
            BIOME_MODIFIER_SERIALIZERS.register("conditional_spawn", () -> CDConditionalSpawnModifier.CODEC);
}
