package alabaster.crabbersdelight.common.worldgen;

import alabaster.crabbersdelight.common.Config;
import alabaster.crabbersdelight.common.block.SeashellBlock;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record RandomSeashellStateProvider(boolean waterlogged) implements BlockStateProvider {
    public static final MapCodec<RandomSeashellStateProvider> CODEC =
            Codec.BOOL.optionalFieldOf("waterlogged", false).xmap(RandomSeashellStateProvider::new, RandomSeashellStateProvider::waterlogged);

    @Override
    public MapCodec<RandomSeashellStateProvider> codec() {
        return CODEC;
    }

    @Override
    public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
        int variant = random.nextInt(Config.SEASHELL_VARIANT_COUNT.get());
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        return CDModBlocks.SEASHELLS.get().defaultBlockState()
                .setValue(SeashellBlock.VARIANT, variant)
                .setValue(SeashellBlock.FACING, facing)
                .setValue(SeashellBlock.WATERLOGGED, waterlogged);
    }
}
