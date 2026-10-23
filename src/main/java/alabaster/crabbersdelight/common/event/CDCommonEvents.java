package alabaster.crabbersdelight.common.event;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.inventory.CrabTrapItemHandler;
import alabaster.crabbersdelight.common.entity.crab.CrabEntity;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import alabaster.crabbersdelight.common.registry.CDModEntities;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import vectorwing.farmersdelight.refabricated.inventory.ItemHandlerResourceHandler;

@EventBusSubscriber(modid = CrabbersDelight.MODID)
public class CDCommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CDModEntities.CRAB.get(), CrabEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(CDModEntities.CRAB.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CrabEntity::checkCrabSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void addBlocksToBlockEntityTypes(BlockEntityTypeAddBlocksEvent event) {
        // Palm signs use the vanilla sign block entities, the cabinet uses Farmer's Delight's
        event.modify(BlockEntityTypes.SIGN, CDModBlocks.PALM_SIGN.get(), CDModBlocks.PALM_WALL_SIGN.get());
        event.modify(BlockEntityTypes.HANGING_SIGN, CDModBlocks.PALM_HANGING_SIGN.get(), CDModBlocks.PALM_WALL_HANGING_SIGN.get());
        event.modify(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath("farmersdelight", "cabinet")),
                CDModBlocks.PALM_CABINET.get());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Crab trap: bait goes in from the top, catches come out from the sides and bottom
        event.registerBlockEntity(Capabilities.Item.BLOCK, CDModBlockEntity.CRAB_TRAP.get(), (be, side) -> {
            ItemHandlerResourceHandler handler = new ItemHandlerResourceHandler(be.getInventory());
            return side == Direction.UP
                    ? RangedResourceHandler.ofSingleIndex(handler, 0)
                    : RangedResourceHandler.of(handler, 1, CrabTrapItemHandler.SLOTS);
        });
    }
}
