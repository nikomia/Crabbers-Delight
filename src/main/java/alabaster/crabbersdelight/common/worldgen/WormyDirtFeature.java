package alabaster.crabbersdelight.common.worldgen;

import alabaster.crabbersdelight.common.registry.CDModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record WormyDirtFeature() implements Feature {
    public static final MapCodec<WormyDirtFeature> CODEC = MapCodec.unit(WormyDirtFeature::new);

    private static final int MIN_GROUP_SIZE = 1;
    private static final int MAX_GROUP_SIZE = 3;
    private static final int SEARCH_DEPTH_BELOW_ORIGIN = 8;

    @Override
    public MapCodec<WormyDirtFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        int minY = Math.max(level.getMinY(), origin.getY() - SEARCH_DEPTH_BELOW_ORIGIN);

        BlockPos chosen = null;
        int dirtSeen = 0;
        BlockPos.MutableBlockPos cursor = origin.mutable();
        for (int y = origin.getY(); y >= minY; y--) {
            cursor.setY(y);
            if (level.getBlockState(cursor).is(Blocks.DIRT)) {
                dirtSeen++;
                if (random.nextInt(dirtSeen) == 0) {
                    chosen = cursor.immutable();
                }
            }
        }

        if (chosen == null) {
            return false;
        }

        int groupSize = MIN_GROUP_SIZE + random.nextInt(MAX_GROUP_SIZE - MIN_GROUP_SIZE + 1);
        BlockPos.MutableBlockPos groupCursor = chosen.mutable();
        for (int i = 0; i < groupSize; i++) {
            if (level.getBlockState(groupCursor).is(Blocks.DIRT)) {
                setBlock(level, groupCursor, CDModBlocks.WORMY_DIRT.get().defaultBlockState());
            }
            groupCursor.move(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }
        return true;
    }
}
