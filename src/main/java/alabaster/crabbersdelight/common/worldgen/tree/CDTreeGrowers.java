package alabaster.crabbersdelight.common.worldgen.tree;

import alabaster.crabbersdelight.CrabbersDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.Feature;

public class CDTreeGrowers {
    public static final ResourceKey<Feature> PALM_KEY = ResourceKey.create(Registries.FEATURE, CrabbersDelight.modPrefix("palm"));

    public static final TreeGrower PALM = new TreeGrower(CrabbersDelight.MODID + ":palm",
            WeightedList.of(PALM_KEY), WeightedList.of(), WeightedList.of(), PALM_KEY);
}
