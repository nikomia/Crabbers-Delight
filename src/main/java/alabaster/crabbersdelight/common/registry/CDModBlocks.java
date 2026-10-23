package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.*;
import alabaster.crabbersdelight.common.worldgen.tree.CDTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.CabinetBlock;

public class CDModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrabbersDelight.MODID);

    private static BlockBehaviour.Properties woodenStorage() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD);
    }

    private static BlockBehaviour.Properties shellStorage() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE).strength(2.0F, 3.0F).sound(SoundType.CORAL_BLOCK);
    }

    // Crab Trap
    public static final DeferredBlock<Block> CRAB_TRAP = BLOCKS.registerBlock("crab_trap", CrabTrapBlock::new,
            CDModBlocks::woodenStorage);




    // Palm Tree
    public static final DeferredBlock<Block> PALM_LOG = BLOCKS.registerBlock("palm_log", CDLogBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<Block> PALM_WOOD = BLOCKS.registerBlock("palm_wood", CDLogBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));
    public static final DeferredBlock<Block> STRIPPED_PALM_LOG = BLOCKS.registerBlock("stripped_palm_log", CDLogBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG));
    public static final DeferredBlock<Block> STRIPPED_PALM_WOOD = BLOCKS.registerBlock("stripped_palm_wood", CDLogBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD));

    public static final DeferredBlock<Block> PALM_PLANKS = BLOCKS.registerBlock("palm_planks", p -> new Block(p) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

    public static final DeferredBlock<Block> PALM_LEAVES = BLOCKS.registerBlock("palm_leaves", p -> new CDLeavesBlock(p) {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));

    public static final DeferredBlock<Block> PALM_SAPLING = BLOCKS.registerBlock("palm_sapling",
            p -> new CDSaplingBlock(CDTreeGrowers.PALM, p, () -> Blocks.SAND),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));

    public static final DeferredBlock<SlabBlock> PALM_SLAB = BLOCKS.registerBlock("palm_slab", SlabBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));
    public static final DeferredBlock<StairBlock> PALM_STAIRS = BLOCKS.registerBlock("palm_stairs",
            p -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), p),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));

    public static final DeferredBlock<DoorBlock> PALM_DOOR = BLOCKS.registerBlock("palm_door",
            p -> new DoorBlock(BlockSetType.OAK, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR));
    public static final DeferredBlock<TrapDoorBlock> PALM_TRAPDOOR = BLOCKS.registerBlock("palm_trapdoor",
            p -> new TrapDoorBlock(BlockSetType.OAK, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR));

    public static final DeferredBlock<FenceBlock> PALM_FENCE = BLOCKS.registerBlock("palm_fence", FenceBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));
    public static final DeferredBlock<FenceGateBlock> PALM_FENCE_GATE = BLOCKS.registerBlock("palm_fence_gate",
            p -> new FenceGateBlock(CDWoodTypes.PALM, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<PressurePlateBlock> PALM_PRESSURE_PLATE = BLOCKS.registerBlock("palm_pressure_plate",
            p -> new PressurePlateBlock(BlockSetType.OAK, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE));
    public static final DeferredBlock<ButtonBlock> PALM_BUTTON = BLOCKS.registerBlock("palm_button",
            p -> new ButtonBlock(BlockSetType.OAK, 30, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON));

    // Wall variants copy the standing sign properties: vanilla wall signs carry a fixed oak loot table and name
    public static final DeferredBlock<StandingSignBlock> PALM_SIGN = BLOCKS.registerBlock("palm_sign",
            p -> new StandingSignBlock(CDWoodTypes.PALM, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN));
    public static final DeferredBlock<WallSignBlock> PALM_WALL_SIGN = BLOCKS.registerBlock("palm_wall_sign",
            p -> new WallSignBlock(CDWoodTypes.PALM, p),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).overrideDescription("block.crabbersdelight.palm_sign"));

    public static final DeferredBlock<CeilingHangingSignBlock> PALM_HANGING_SIGN = BLOCKS.registerBlock("palm_hanging_sign",
            p -> new CeilingHangingSignBlock(CDWoodTypes.PALM, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN));
    public static final DeferredBlock<WallHangingSignBlock> PALM_WALL_HANGING_SIGN = BLOCKS.registerBlock("palm_wall_hanging_sign",
            p -> new WallHangingSignBlock(CDWoodTypes.PALM, p),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).overrideDescription("block.crabbersdelight.palm_hanging_sign"));

    public static final DeferredBlock<Block> PALM_CABINET = BLOCKS.registerBlock("palm_cabinet", CabinetBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));

    public static final DeferredBlock<Block> COCONUT = BLOCKS.registerBlock("coconut", CoconutBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA));

    public static final DeferredBlock<Block> SEASHELLS = BLOCKS.registerBlock("seashells", SeashellBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.HORN_CORAL).offsetType(BlockBehaviour.OffsetType.XZ));

    // Storage
    public static final DeferredBlock<Block> CRAB_BARREL = BLOCKS.registerBlock("crab_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> CLAM_BARREL = BLOCKS.registerBlock("clam_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> CLAWSTER_BARREL = BLOCKS.registerBlock("clawster_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> SHRIMP_BARREL = BLOCKS.registerBlock("shrimp_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> COD_BARREL = BLOCKS.registerBlock("cod_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> SALMON_BARREL = BLOCKS.registerBlock("salmon_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> PUFFERFISH_BARREL = BLOCKS.registerBlock("pufferfish_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> TROPICAL_FISH_BARREL = BLOCKS.registerBlock("tropical_fish_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> SQUID_BARREL = BLOCKS.registerBlock("squid_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> GLOW_SQUID_BARREL = BLOCKS.registerBlock("glow_squid_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> FROG_LEG_BARREL = BLOCKS.registerBlock("frog_leg_barrel", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> COCONUT_CRATE = BLOCKS.registerBlock("coconut_crate", Block::new, CDModBlocks::woodenStorage);
    public static final DeferredBlock<Block> NAUTILUS_SHELL_BLOCK = BLOCKS.registerBlock("nautilus_shell_block", NautilusShellBlock::new, CDModBlocks::shellStorage);
    public static final DeferredBlock<Block> PEARL_BLOCK = BLOCKS.registerBlock("pearl_block", Block::new, CDModBlocks::shellStorage);
    public static final DeferredBlock<Block> SCUTE_BLOCK = BLOCKS.registerBlock("scute_block", Block::new, CDModBlocks::shellStorage);
    public static final DeferredBlock<Block> SEA_PICKLE_CRATE = BLOCKS.registerBlock("sea_pickle_crate", SlabBlock::new, CDModBlocks::woodenStorage);


    // Life Preserver
    public static final DeferredBlock<Block> LIFE_PRESERVER = BLOCKS.registerBlock("life_preserver", LifePreserverBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WOOL.white()).noOcclusion());
}
