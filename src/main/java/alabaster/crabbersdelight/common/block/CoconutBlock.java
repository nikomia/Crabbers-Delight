package alabaster.crabbersdelight.common.block;

import alabaster.crabbersdelight.common.registry.CDDamageSources;
import alabaster.crabbersdelight.common.registry.CDModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoconutBlock extends FallingBlock {
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);
    private static final VoxelShape HANGING_SHAPE = Block.box(3.0D, 5.0D, 3.0D, 13.0D, 15.0D, 13.0D);

    public CoconutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HANGING, false));
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter level, BlockPos pos) {
        return 0x5C3A1E;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_SHAPE : SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                     BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            boolean hanging = computeHanging(level, pos);
            if (!hanging) {
                ticks.scheduleTick(pos, this, 1);
            }
            return state.setValue(HANGING, hanging);
        }
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HANGING, computeHanging(context.getLevel(), context.getClickedPos()));
    }

    private boolean computeHanging(BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        BlockPos belowPos = pos.below();
        return !level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(HANGING)) {
            super.tick(state, level, pos, random);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // No falling dust particles
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceState, FallingBlockEntity fallingEntity) {
        super.onLand(level, pos, state, replaceState, fallingEntity);

        if (level instanceof ServerLevel serverLevel && fallingEntity != null) {
            boolean hitPlayer = false;

            for (Entity entity : level.getEntities(null, SHAPE.bounds().move(pos))) {
                if (entity instanceof LivingEntity living) {
                    living.hurtServer(serverLevel, CDDamageSources.getSimpleDamageSource(level, CDDamageSources.FALLING_COCONUT), 2.0F);

                    if (entity instanceof Player player) {
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GOAT_HORN_BREAK, SoundSource.PLAYERS, 0.7F, 0.5F);

                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 2));

                        if (player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(CDModItems.COCONUT_HELMET.get()));
                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            level.destroyBlock(pos, false);
                        }

                        hitPlayer = true;
                    }
                }
            }

            if (hitPlayer) {
                level.destroyBlock(pos, true);
            }
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);

        if (!level.isClientSide() && state.getValue(HANGING)) {
            BlockPos pos = hit.getBlockPos();
            level.setBlock(pos, state.setValue(HANGING, false), Block.UPDATE_ALL);
            level.scheduleTick(pos, this, 1);
        }
    }
}
