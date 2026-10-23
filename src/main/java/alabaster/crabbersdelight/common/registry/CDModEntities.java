package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.entity.LifePreserverSeat;
import alabaster.crabbersdelight.common.entity.crab.CrabEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class CDModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, CrabbersDelight.MODID);

    private static <T extends net.minecraft.world.entity.Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(
            String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, CrabbersDelight.modPrefix(name));
        return ENTITY_TYPES.register(name, () -> builder.apply(EntityType.Builder.of(factory, category)).build(key));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<CrabEntity>> CRAB = register("crab", CrabEntity::new, MobCategory.CREATURE,
            b -> b.sized(0.7f, 0.7f));

    // Entity ids kept from the original mod
    public static final DeferredHolder<EntityType<?>, EntityType<Boat>> PALM_BOAT = register("mod_boat",
            (type, level) -> new Boat(type, level, () -> CDModItems.PALM_BOAT.get()), MobCategory.MISC,
            b -> b.noLootTable().sized(1.375f, 0.5625f).eyeHeight(0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<ChestBoat>> PALM_CHEST_BOAT = register("mod_chest_boat",
            (type, level) -> new ChestBoat(type, level, () -> CDModItems.PALM_CHEST_BOAT.get()), MobCategory.MISC,
            b -> b.noLootTable().sized(1.375f, 0.5625f).eyeHeight(0.5625F).clientTrackingRange(10));



    public static final DeferredHolder<EntityType<?>, EntityType<LifePreserverSeat>> LIFE_PRESERVER_SEAT = register("life_preserver_seat",
            LifePreserverSeat::new, MobCategory.MISC, b -> b.sized(0.1f, 0.1f).noSave().clientTrackingRange(6).updateInterval(40));
}
