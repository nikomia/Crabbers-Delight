package alabaster.crabbersdelight.common.worldgen.tree;

import alabaster.crabbersdelight.common.block.CDLeavesBlock;
import alabaster.crabbersdelight.common.block.CoconutBlock;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import alabaster.crabbersdelight.common.registry.CDModFeatures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PalmFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PalmFoliagePlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance -> foliagePlacerParts(instance).apply(instance, PalmFoliagePlacer::new));

    public PalmFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return CDModFeatures.PALM_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter setter, RandomSource random, TreeFeature tree, int treeHeight,
                                 FoliageAttachment attachment, int foliageHeight, int leafRadius, int offset) {
        BlockPos center = attachment.pos();

        // Tuft cross above frond level
        placeLeafWithCoconut(level, setter, random, tree, center);
        placeLeafWithCoconut(level, setter, random, tree, center.north());
        placeLeafWithCoconut(level, setter, random, tree, center.south());
        placeLeafWithCoconut(level, setter, random, tree, center.east());
        placeLeafWithCoconut(level, setter, random, tree, center.west());

        // Cardinals
        createFrond(center, level, setter, random, tree, 1, 0);
        createFrond(center, level, setter, random, tree, -1, 0);
        createFrond(center, level, setter, random, tree, 0, 1);
        createFrond(center, level, setter, random, tree, 0, -1);

        // Diagonals
        createFrond(center, level, setter, random, tree, 1, 1);
        createFrond(center, level, setter, random, tree, 1, -1);
        createFrond(center, level, setter, random, tree, -1, 1);
        createFrond(center, level, setter, random, tree, -1, -1);
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false;
    }

    private static void createFrond(BlockPos start, WorldGenLevel level, FoliageSetter setter, RandomSource random, TreeFeature tree, int dx, int dz) {
        placeLeafWithCoconut(level, setter, random, tree, new BlockPos(start.getX() + dx, start.getY() - 1, start.getZ() + dz));
        for (int i = 2; i <= 3; i++) {
            placeLeafWithCoconut(level, setter, random, tree, new BlockPos(start.getX() + dx * i, start.getY() - 1, start.getZ() + dz * i));
        }
        placeLeafWithCoconut(level, setter, random, tree, new BlockPos(start.getX() + dx * 4, start.getY() - 2, start.getZ() + dz * 4));
    }

    private static void placeLeafWithCoconut(WorldGenLevel level, FoliageSetter setter, RandomSource random, TreeFeature tree, BlockPos pos) {
        BlockState leaf = tree.foliageProvider().value().getState(level, random, pos);
        if (leaf.hasProperty(CDLeavesBlock.GENERATED)) {
            leaf = leaf.setValue(CDLeavesBlock.GENERATED, true);
        }
        setter.set(pos, leaf);

        if (random.nextInt(5) == 0) {
            BlockPos below = pos.below();
            if (level.isStateAtPosition(below, BlockBehaviour.BlockStateBase::isAir)) {
                setter.set(below, CDModBlocks.COCONUT.get().defaultBlockState().setValue(CoconutBlock.HANGING, true));
            }
        }
    }
}
