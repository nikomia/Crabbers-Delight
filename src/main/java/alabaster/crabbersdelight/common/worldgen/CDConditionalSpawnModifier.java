package alabaster.crabbersdelight.common.worldgen;

import alabaster.crabbersdelight.common.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * Adds a mob spawn whose weight and group size come from the common config.
 */
public record CDConditionalSpawnModifier(
        HolderSet<Biome> biomes,
        MobCategory category,
        EntityType<?> entityType,
        String configKey
) implements BiomeModifier {

    public static final MapCodec<CDConditionalSpawnModifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(CDConditionalSpawnModifier::biomes),
            MobCategory.CODEC.fieldOf("category").forGetter(CDConditionalSpawnModifier::category),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(CDConditionalSpawnModifier::entityType),
            Codec.STRING.fieldOf("config_key").forGetter(CDConditionalSpawnModifier::configKey)
    ).apply(i, CDConditionalSpawnModifier::new));

    @Override
    public void modify(RegistryAccess registries, Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !biomes.contains(biome)) return;

        if ("crab_spawns".equals(configKey)) {
            if (!Config.GENERATE_CRAB_SPAWNS.get()) return;
            int min = Config.CRAB_SPAWN_MIN.get();
            int max = Math.max(min, Config.CRAB_SPAWN_MAX.get());
            builder.getMobSpawnSettings().addSpawn(entityType, category, Config.CRAB_SPAWN_WEIGHT.get(), UniformInt.of(min, max));
        } else {
            builder.getMobSpawnSettings().addSpawn(entityType, category, 10, UniformInt.of(1, 3));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
