package alabaster.crabbersdelight.common.worldgen.placement;

import alabaster.crabbersdelight.common.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;

/**
 * Applies the enable flag and rarity chance from the common config at generation time.
 */
public record CDConfigPlacementModifier(String configKey) implements PlacementModifier {
    public static final MapCodec<CDConfigPlacementModifier> CODEC =
            Codec.STRING.fieldOf("config_key").xmap(CDConfigPlacementModifier::new, CDConfigPlacementModifier::configKey);

    @Override
    public void modify(PlacementContext context, RandomSource random, BlockPos origin, Consumer<BlockPos> output) {
        boolean enabled;
        int chance;
        switch (configKey) {
            case "palm" -> {
                enabled = Config.GENERATE_PALM_TREES.get();
                chance = Config.CHANCE_PALM_TREES.get();
            }
            case "seashell" -> {
                enabled = Config.GENERATE_SEASHELLS.get();
                chance = Config.CHANCE_SEASHELLS.get();
            }
            case "seashell_underwater" -> {
                enabled = Config.GENERATE_SEASHELLS.get();
                chance = Config.CHANCE_SEASHELLS_UNDERWATER.get();
            }
            case "wormy_dirt" -> {
                enabled = Config.GENERATE_WORMY_DIRT.get();
                chance = Config.CHANCE_WORMY_DIRT.get();
            }
            default -> {
                output.accept(origin);
                return;
            }
        }
        if (enabled && chance > 0 && random.nextInt(chance) == 0) {
            output.accept(origin);
        }
    }

    @Override
    public MapCodec<CDConfigPlacementModifier> codec() {
        return CODEC;
    }
}
