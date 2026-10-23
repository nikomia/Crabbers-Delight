package alabaster.crabbersdelight.common.worldgen.tree;

import alabaster.crabbersdelight.common.block.CoconutBlock;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import alabaster.crabbersdelight.common.registry.CDModFeatures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class PalmTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<PalmTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance)
                    .and(IntProviders.CODEC.fieldOf("bend_length").forGetter(p -> p.bendLength))
                    .apply(instance, PalmTrunkPlacer::new));

    private static final int COCONUT_CHANCE = 20;
    private static final int COCONUT_RADIUS = 3;
    private static final int MAX_COCONUTS = 3;
    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_DESCEND = 6;

    private final IntProvider bendLength;

    public PalmTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, IntProvider bendLength) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.bendLength = bendLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return CDModFeatures.PALM_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter,
                                                            RandomSource random, int height, BlockPos pos, TreeFeature tree) {
        int dx = random.nextBoolean() ? 1 : -1;
        int dz = random.nextBoolean() ? 1 : -1;
        int bend = bendLength.sample(random);

        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int y = 0; y < height; y++) {
            if (y > height / 3 && bend > 0 && random.nextInt(3) == 0) {
                cursor.move(dx, 0, dz);
                bend--;
            }
            placeLog(level, trunkSetter, random, cursor, tree);
            cursor.move(0, 1, 0);
        }

        // Rare ground coconuts around the trunk base
        if (random.nextInt(COCONUT_CHANCE) == 0) {
            int coconutCount = 1 + random.nextInt(MAX_COCONUTS);
            for (int i = 0; i < coconutCount; i++) {
                placeGroundCoconut(level, trunkSetter, random, pos);
            }
        }

        return List.of(new FoliagePlacer.FoliageAttachment(cursor.immutable(), 0, false));
    }

    private static void placeGroundCoconut(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos pos) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int rx = pos.getX() + random.nextInt(COCONUT_RADIUS * 2 + 1) - COCONUT_RADIUS;
            int rz = pos.getZ() + random.nextInt(COCONUT_RADIUS * 2 + 1) - COCONUT_RADIUS;
            BlockPos candidate = new BlockPos(rx, pos.getY(), rz);
            for (int dy = 0; dy >= -MAX_DESCEND; dy--) {
                BlockPos target = candidate.offset(0, dy, 0);
                if (level.isStateAtPosition(target, BlockBehaviour.BlockStateBase::isAir)
                        && !level.isStateAtPosition(target.below(), BlockBehaviour.BlockStateBase::isAir)) {
                    trunkSetter.accept(target, CDModBlocks.COCONUT.get().defaultBlockState().setValue(CoconutBlock.HANGING, false));
                    return;
                }
            }
        }
    }
}
