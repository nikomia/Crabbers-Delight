package alabaster.crabbersdelight.common.block;

import alabaster.crabbersdelight.client.gui.NoteClientHandler;
import alabaster.crabbersdelight.common.block.entity.NoteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class NoteBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty SIGNED = BooleanProperty.create("signed");
    public static final BooleanProperty FLAT = BooleanProperty.create("flat");

    private static final VoxelShape SHAPE_NORTH = Block.box(3, 2, 15, 13, 14, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(3, 2, 0, 13, 14, 1);
    private static final VoxelShape SHAPE_WEST = Block.box(15, 2, 3, 16, 14, 13);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 2, 3, 1, 14, 13);
    private static final VoxelShape SHAPE_FLAT_NORTH_SOUTH = Block.box(3, 0, 2, 13, 1, 14);
    private static final VoxelShape SHAPE_FLAT_EAST_WEST = Block.box(2, 0, 3, 14, 1, 13);

    public NoteBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIGNED, false)
                .setValue(FLAT, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(FLAT)) {
            return switch (state.getValue(FACING)) {
                case WEST, EAST -> SHAPE_FLAT_EAST_WEST;
                default -> SHAPE_FLAT_NORTH_SOUTH;
            };
        }
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> Shapes.block();
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIGNED, FLAT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NoteBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean flat = ctx.getClickedFace() == Direction.UP;
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(FLAT, flat);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(FLAT)) {
            BlockPos below = pos.below();
            return level.getBlockState(below).isCollisionShapeFullBlock(level, below);
        }
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return level.getBlockState(supportPos).isCollisionShapeFullBlock(level, supportPos);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                     BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(FLAT)) {
            if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof NoteBlockEntity note)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            NoteClientHandler.openBlockScreen(pos, note.isSigned(), note.getTitle(), note.getText(), note.getAuthor());
        }
        return InteractionResult.SUCCESS;
    }
}
