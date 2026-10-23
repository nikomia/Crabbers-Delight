package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.worldgen.RandomSeashellStateProvider;
import alabaster.crabbersdelight.common.worldgen.WormyDirtFeature;
import alabaster.crabbersdelight.common.worldgen.placement.CDConfigPlacementModifier;
import alabaster.crabbersdelight.common.worldgen.tree.PalmFoliagePlacer;
import alabaster.crabbersdelight.common.worldgen.tree.PalmTrunkPlacer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Worldgen types. In 26.3 features, placement modifiers and state providers are registered as codecs.
 */
public class CDModFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES =
            DeferredRegister.create(Registries.FEATURE_TYPE, CrabbersDelight.MODID);
    public static final DeferredRegister<MapCodec<? extends PlacementModifier>> PLACEMENT_MODIFIERS =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, CrabbersDelight.MODID);
    public static final DeferredRegister<MapCodec<? extends BlockStateProvider>> STATE_PROVIDERS =
            DeferredRegister.create(Registries.BLOCK_STATE_PROVIDER_TYPE, CrabbersDelight.MODID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, CrabbersDelight.MODID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, CrabbersDelight.MODID);

    public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<WormyDirtFeature>> WORMY_DIRT_FEATURE =
            FEATURES.register("wormy_dirt", () -> WormyDirtFeature.CODEC);

    public static final DeferredHolder<MapCodec<? extends PlacementModifier>, MapCodec<CDConfigPlacementModifier>> CONFIG_FILTER =
            PLACEMENT_MODIFIERS.register("config_filter", () -> CDConfigPlacementModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends BlockStateProvider>, MapCodec<RandomSeashellStateProvider>> RANDOM_SEASHELL =
            STATE_PROVIDERS.register("random_seashell", () -> RandomSeashellStateProvider.CODEC);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<PalmTrunkPlacer>> PALM_TRUNK_PLACER =
            TRUNK_PLACERS.register("palm", () -> new TrunkPlacerType<>(PalmTrunkPlacer.CODEC));

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<PalmFoliagePlacer>> PALM_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("palm_foliage_placer", () -> new FoliagePlacerType<>(PalmFoliagePlacer.CODEC));
}
