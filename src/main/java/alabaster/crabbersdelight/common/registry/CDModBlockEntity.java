package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CDModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrabbersDelight.MODID);

    public static final Supplier<BlockEntityType<CrabTrapBlockEntity>> CRAB_TRAP = BLOCK_ENTITY_TYPES.register("crab_trap",
            () -> new BlockEntityType<>(CrabTrapBlockEntity::new, CDModBlocks.CRAB_TRAP.get()));

    public static final Supplier<BlockEntityType<FishPlaqueBlockEntity>> FISH_PLAQUE = BLOCK_ENTITY_TYPES.register("fish_plaque",
            () -> new BlockEntityType<>(FishPlaqueBlockEntity::new, CDModBlocks.FISH_PLAQUE.get()));

    public static final Supplier<BlockEntityType<NoteBlockEntity>> NOTE = BLOCK_ENTITY_TYPES.register("note",
            () -> new BlockEntityType<>(NoteBlockEntity::new, CDModBlocks.NOTE_BLOCK.get()));

    public static final Supplier<BlockEntityType<TackleBoxBlockEntity>> TACKLE_BOX = BLOCK_ENTITY_TYPES.register("tackle_box",
            () -> new BlockEntityType<>(TackleBoxBlockEntity::new, CDModBlocks.TACKLE_BOX.get()));

    public static final Supplier<BlockEntityType<WormBinBlockEntity>> WORM_BIN = BLOCK_ENTITY_TYPES.register("worm_bin",
            () -> new BlockEntityType<>(WormBinBlockEntity::new, CDModBlocks.WORM_BIN.get()));
}
