package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.CDFoodValues;
import alabaster.crabbersdelight.common.item.*;
import alabaster.crabbersdelight.common.item.fishing.LureEffect;
import alabaster.crabbersdelight.common.tags.CDModTags;
import com.google.common.collect.Sets;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.item.component.consumable.RemoveRandomStatusEffectsConsumeEffect;
import vectorwing.farmersdelight.refabricated.FDRefabricatedTags;

import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.function.Supplier;

public class CDModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrabbersDelight.MODID);
    public static final LinkedHashSet<Supplier<? extends Item>> CREATIVE_TAB_ITEMS = Sets.newLinkedHashSet();

    private static <I extends Item> DeferredItem<I> registerWithTab(String name, Function<Item.Properties, I> factory, Supplier<Item.Properties> properties) {
        DeferredItem<I> item = ITEMS.registerItem(name, factory, properties);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    private static <I extends Item> DeferredItem<I> registerWithTab(String name, Function<Item.Properties, I> factory) {
        return registerWithTab(name, factory, Item.Properties::new);
    }

    private static DeferredItem<BlockItem> blockItem(String name, Supplier<? extends Block> block) {
        return blockItem(name, block, Item.Properties::new);
    }

    private static DeferredItem<BlockItem> blockItem(String name, Supplier<? extends Block> block, Supplier<Item.Properties> properties) {
        return registerWithTab(name, p -> new BlockItem(block.get(), p), () -> properties.get().useBlockDescriptionPrefix());
    }

    private static Supplier<Item.Properties> fuel(ResourceKey<ContextIntProvider> burnTime) {
        return () -> new Item.Properties().cookingFuel(burnTime);
    }

    // Helpers mirroring Farmer's Delight
    public static Item.Properties foodItem(FoodProperties food, @Nullable Consumable consumable) {
        return new Item.Properties().food(food, consumable != null ? consumable : Consumables.DEFAULT_FOOD);
    }

    public static Item.Properties foodItem(FoodProperties food) {
        return foodItem(food, null);
    }

    public static Item.Properties bowlFoodItem(FoodProperties food, @Nullable Consumable consumable) {
        return foodItem(food, consumable).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static Item.Properties drinkItem(FoodProperties food, Consumable consumable) {
        return new Item.Properties().food(food, consumable).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }

    private static DeferredItem<ConsumableItem> food(String name, FoodProperties food) {
        return registerWithTab(name, ConsumableItem::new, () -> foodItem(food));
    }

    private static DeferredItem<ConsumableItem> food(String name, FoodProperties food, Consumable consumable, boolean effectTooltip) {
        return registerWithTab(name, p -> new ConsumableItem(p, effectTooltip), () -> foodItem(food, consumable));
    }

    private static DeferredItem<ConsumableItem> bowlFood(String name, FoodProperties food, Consumable consumable) {
        return registerWithTab(name, p -> new ConsumableItem(p, true), () -> bowlFoodItem(food, consumable));
    }

    // Spawn Eggs
    public static final DeferredItem<SpawnEggItem> CRAB_SPAWN_EGG = registerWithTab("crab_spawn_egg", SpawnEggItem::new,
            () -> new Item.Properties().spawnEgg(CDModEntities.CRAB.get()));

    // Mob Buckets
    public static final DeferredItem<MobBucketItem> CRAB_BUCKET = registerWithTab("crab_bucket",
            p -> new MobBucketItem(CDModEntities.CRAB.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, p),
            () -> new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));

    // Materials
    public static final DeferredItem<Item> CLAM = registerWithTab("clam", Item::new);
    public static final DeferredItem<Item> PEARL = registerWithTab("pearl", Item::new,
            () -> new Item.Properties().trimMaterial(ResourceKey.create(Registries.TRIM_MATERIAL, CrabbersDelight.modPrefix("pearl"))));
    public static final DeferredItem<Item> CAN = registerWithTab("can", Item::new);
    public static final DeferredItem<Item> CORAL_FRAGMENTS = registerWithTab("coral_fragments", Item::new);
    public static final DeferredItem<Item> FISH_BONES = registerWithTab("fish_bones", Item::new);

    // Tools & Equipment
    public static final DeferredItem<ChumItem> BUCKET_OF_CRAB_CHUM = registerWithTab("bucket_of_crab_chum", ChumItem::new, ChumItem::properties);
    public static final DeferredItem<ChumItem> BUCKET_OF_CLAWSTER_CHUM = registerWithTab("bucket_of_clawster_chum", ChumItem::new, ChumItem::properties);
    public static final DeferredItem<ChumItem> BUCKET_OF_CLAM_CHUM = registerWithTab("bucket_of_clam_chum", ChumItem::new, ChumItem::properties);
    public static final DeferredItem<ChumItem> BUCKET_OF_SHRIMP_CHUM = registerWithTab("bucket_of_shrimp_chum", ChumItem::new, ChumItem::properties);
    public static final DeferredItem<CrabClawItem> CRAB_CLAW = registerWithTab("crab_claw", CrabClawItem::new, CrabClawItem::properties);
    public static final DeferredItem<PearlNecklaceItem> PEARL_NECKLACE = registerWithTab("pearl_necklace", PearlNecklaceItem::new,
            () -> new Item.Properties().humanoidArmor(CDArmorMaterials.PEARL_NECKLACE, ArmorType.CHESTPLATE).stacksTo(1));
    public static final DeferredItem<CoconutHelmetItem> COCONUT_HELMET = registerWithTab("coconut_helmet", CoconutHelmetItem::new,
            () -> new Item.Properties().stacksTo(1).durability(64)
                    .repairable(CDModTags.REPAIRS_COCONUT_HELMET)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).setEquipSound(SoundEvents.ARMOR_EQUIP_TURTLE).build())
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ARMOR, new AttributeModifier(CrabbersDelight.modPrefix("coconut_helmet_armor"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
                            .build()));
    public static final DeferredItem<LifePreserverItem> LIFE_PRESERVER = registerWithTab("life_preserver", LifePreserverItem::new,
            () -> new Item.Properties().useBlockDescriptionPrefix()
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.LEGS).setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC).build()));
    public static final DeferredItem<ConchItem> CONCH = registerWithTab("conch", ConchItem::new, () -> new Item.Properties().stacksTo(1));

    // Fishing & Crabbing
    public static final DeferredItem<CrabTrapItem> CRAB_TRAP = registerWithTab("crab_trap", CrabTrapItem::new, () -> new Item.Properties().useBlockDescriptionPrefix());
    public static final DeferredItem<BlockItem> FISH_PLAQUE = blockItem("fish_plaque", CDModBlocks.FISH_PLAQUE);
    public static final DeferredItem<TackleBoxItem> TACKLE_BOX = registerWithTab("tackle_box", TackleBoxItem::new, () -> new Item.Properties().stacksTo(1).useBlockDescriptionPrefix());
    public static final DeferredItem<LureItem> BARBED_LURE = registerWithTab("barbed_lure", p -> new LureItem(p, LureEffect.BARBED), LureItem::properties);
    public static final DeferredItem<LureItem> DOUBLE_LURE = registerWithTab("double_lure", p -> new LureItem(p, LureEffect.DOUBLE), LureItem::properties);
    public static final DeferredItem<LureItem> MAGNETIC_LURE = registerWithTab("magnetic_lure", p -> new LureItem(p, LureEffect.MAGNETIC), LureItem::properties);
    public static final DeferredItem<LureItem> AUTOMATIC_LURE = registerWithTab("automatic_lure", p -> new LureItem(p, LureEffect.AUTOMATIC), LureItem::properties);
    public static final DeferredItem<LureItem> STORM_LURE = registerWithTab("storm_lure", p -> new LureItem(p, LureEffect.STORM), LureItem::properties);
    public static final DeferredItem<FishingLineItem> FISHING_LINE = registerWithTab("fishing_line", FishingLineItem::new, () -> new Item.Properties().stacksTo(1).durability(64));
    public static final DeferredItem<WormItem> WORM = registerWithTab("worm", WormItem::new);
    public static final DeferredItem<BlockItem> WORMY_DIRT = blockItem("wormy_dirt", CDModBlocks.WORMY_DIRT);
    public static final DeferredItem<BlockItem> WORM_BIN = blockItem("worm_bin", CDModBlocks.WORM_BIN);

    // Palm
    public static final DeferredItem<BlockItem> PALM_LOG = blockItem("palm_log", CDModBlocks.PALM_LOG, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_WOOD = blockItem("palm_wood", CDModBlocks.PALM_WOOD, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> STRIPPED_PALM_LOG = blockItem("stripped_palm_log", CDModBlocks.STRIPPED_PALM_LOG, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> STRIPPED_PALM_WOOD = blockItem("stripped_palm_wood", CDModBlocks.STRIPPED_PALM_WOOD, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_PLANKS = blockItem("palm_planks", CDModBlocks.PALM_PLANKS, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_STAIRS = blockItem("palm_stairs", CDModBlocks.PALM_STAIRS, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_SLAB = blockItem("palm_slab", CDModBlocks.PALM_SLAB, fuel(ContextIntProviders.COOKING_TIME_WOOD_SLABS));
    public static final DeferredItem<BlockItem> PALM_FENCE = blockItem("palm_fence", CDModBlocks.PALM_FENCE, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_FENCE_GATE = blockItem("palm_fence_gate", CDModBlocks.PALM_FENCE_GATE, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<DoubleHighBlockItem> PALM_DOOR = registerWithTab("palm_door", p -> new DoubleHighBlockItem(CDModBlocks.PALM_DOOR.get(), p),
            () -> new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE).useBlockDescriptionPrefix());
    // Item id kept from the original mod even though the block is "palm_trapdoor"
    public static final DeferredItem<BlockItem> PALM_TRAPDOOR = blockItem("palm_trapdoor_bottom", CDModBlocks.PALM_TRAPDOOR, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_PRESSURE_PLATE = blockItem("palm_pressure_plate", CDModBlocks.PALM_PRESSURE_PLATE, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> PALM_BUTTON = blockItem("palm_button", CDModBlocks.PALM_BUTTON, fuel(ContextIntProviders.COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL));
    public static final DeferredItem<BlockItem> PALM_LEAVES = blockItem("palm_leaves", CDModBlocks.PALM_LEAVES,
            () -> new Item.Properties().compostable(ContextIntProviders.COMPOSTABLE_LOW));
    public static final DeferredItem<BlockItem> PALM_SAPLING = blockItem("palm_sapling", CDModBlocks.PALM_SAPLING,
            () -> new Item.Properties().compostable(ContextIntProviders.COMPOSTABLE_LOW).cookingFuel(ContextIntProviders.COOKING_TIME_DRY_PLANTS));
    public static final DeferredItem<StandingAndWallBlockItem> PALM_SIGN = registerWithTab("palm_sign",
            p -> new StandingAndWallBlockItem(CDModBlocks.PALM_SIGN.get(), CDModBlocks.PALM_WALL_SIGN.get(), Direction.DOWN, p),
            () -> new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE).stacksTo(16).signText().useBlockDescriptionPrefix());
    public static final DeferredItem<HangingSignItem> PALM_HANGING_SIGN = registerWithTab("palm_hanging_sign",
            p -> new HangingSignItem(CDModBlocks.PALM_HANGING_SIGN.get(), CDModBlocks.PALM_WALL_HANGING_SIGN.get(), p),
            () -> new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_HANGING_SIGNS).stacksTo(16).signText().useBlockDescriptionPrefix());
    public static final DeferredItem<BoatItem> PALM_BOAT = registerWithTab("palm_boat", p -> new BoatItem(CDModEntities.PALM_BOAT.get(), p),
            () -> new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_BOATS).stacksTo(1));
    public static final DeferredItem<BoatItem> PALM_CHEST_BOAT = registerWithTab("palm_chest_boat", p -> new BoatItem(CDModEntities.PALM_CHEST_BOAT.get(), p),
            () -> new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_BOATS).stacksTo(1));
    public static final DeferredItem<BlockItem> PALM_CABINET = blockItem("palm_cabinet", CDModBlocks.PALM_CABINET, fuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));

    // Coconut
    public static final DeferredItem<BlockItem> COCONUT = blockItem("coconut", CDModBlocks.COCONUT);
    public static final DeferredItem<ConsumableItem> COCONUT_HALVE = food("coconut_halve", CDFoodValues.COCONUT_HALVE);
    public static final DeferredItem<ConsumableItem> COCONUT_MILK = registerWithTab("coconut_milk", p -> new ConsumableItem(p, false, true),
            () -> drinkItem(CDFoodValues.COCONUT_MILK, Consumables.defaultDrink()
                    .onConsume(new RemoveRandomStatusEffectsConsumeEffect(FDRefabricatedTags.MobEffects.MILK_BOTTLE_IGNORED)).build()));
    public static final DeferredItem<ConsumableItem> COCONUT_PUDDING = registerWithTab("coconut_pudding", ConsumableItem::new,
            () -> foodItem(CDFoodValues.COCONUT_PUDDING).craftRemainder(Items.GLASS_BOTTLE));

    // Seashells
    public static final DeferredItem<BlockItem> SEASHELLS = blockItem("seashells", CDModBlocks.SEASHELLS);

    // Raw / Cooked Meats
    public static final DeferredItem<ConsumableItem> RAW_CRAB = food("crab", CDFoodValues.RAW_CRAB);
    public static final DeferredItem<ConsumableItem> COOKED_CRAB = food("cooked_crab", CDFoodValues.COOKED_CRAB);
    public static final DeferredItem<ConsumableItem> RAW_CLAWSTER = food("clawster", CDFoodValues.RAW_CLAWSTER);
    public static final DeferredItem<ConsumableItem> COOKED_CLAWSTER = food("cooked_clawster", CDFoodValues.COOKED_CLAWSTER);
    public static final DeferredItem<ConsumableItem> RAW_SHRIMP = food("shrimp", CDFoodValues.RAW_SHRIMP, CDFoodValues.RAW_SHRIMP_CONSUMABLE, true);
    public static final DeferredItem<ConsumableItem> COOKED_SHRIMP = food("cooked_shrimp", CDFoodValues.COOKED_SHRIMP, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> RAW_CLAM_MEAT = food("raw_clam_meat", CDFoodValues.RAW_CLAM_MEAT, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> COOKED_CLAM_MEAT = food("cooked_clam_meat", CDFoodValues.COOKED_CLAM_MEAT, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> RAW_SQUID_TENTACLES = food("raw_squid_tentacles", CDFoodValues.RAW_SQUID_TENTACLES);
    public static final DeferredItem<ConsumableItem> RAW_GLOW_SQUID_TENTACLES = food("raw_glow_squid_tentacles", CDFoodValues.RAW_SQUID_TENTACLES);
    public static final DeferredItem<ConsumableItem> COOKED_SQUID_TENTACLES = food("cooked_squid_tentacles", CDFoodValues.COOKED_SQUID_TENTACLES);
    public static final DeferredItem<ConsumableItem> COOKED_GLOW_SQUID_TENTACLES = food("cooked_glow_squid_tentacles", CDFoodValues.COOKED_SQUID_TENTACLES);
    public static final DeferredItem<ConsumableItem> RAW_FROG_LEG = food("raw_frog_leg", CDFoodValues.RAW_FROG_LEG);
    public static final DeferredItem<ConsumableItem> COOKED_FROG_LEG = food("cooked_frog_leg", CDFoodValues.COOKED_FROG_LEG);
    public static final DeferredItem<ConsumableItem> FRIED_FISH = food("fried_fish", CDFoodValues.FRIED_FISH);
    public static final DeferredItem<ConsumableItem> FISH_SANDWICH = food("fish_sandwich", CDFoodValues.FISH_SANDWICH);

    // Meals
    public static final DeferredItem<ConsumableItem> CRAB_CAKES = food("crab_cakes", CDFoodValues.CRAB_CAKES, CDFoodValues.CRAB_CAKES_CONSUMABLE, true);
    public static final DeferredItem<ConsumableItem> CRAB_LEGS = food("crab_legs", CDFoodValues.CRAB_LEGS);
    public static final DeferredItem<ConsumableItem> FISH_STICK = food("fish_stick", CDFoodValues.FISH_STICK);
    public static final DeferredItem<ConsumableItem> SURF_AND_TURF = food("surf_and_turf", CDFoodValues.SURF_AND_TURF, CDFoodValues.SURF_AND_TURF_CONSUMABLE, true);
    public static final DeferredItem<ConsumableItem> SHRIMP_SKEWER = food("shrimp_skewer", CDFoodValues.SHRIMP_SKEWER);
    public static final DeferredItem<ConsumableItem> CLAM_BAKE = food("clam_bake", CDFoodValues.CLAM_BAKE, CDFoodValues.CLAM_BAKE_CONSUMABLE, true);
    public static final DeferredItem<ConsumableItem> COOKED_TROPICAL_FISH = food("cooked_tropical_fish", CDFoodValues.COOKED_TROPICAL_FISH, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> PUFFERFISH_SLICE = food("pufferfish_slice", CDFoodValues.PUFFERFISH_SLICE, CDFoodValues.PUFFERFISH_SLICE_CONSUMABLE, true);
    public static final DeferredItem<ConsumableItem> COOKED_PUFFERFISH_SLICE = food("cooked_pufferfish_slice", CDFoodValues.COOKED_PUFFERFISH_SLICE, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> TROPICAL_FISH_SLICE = food("tropical_fish_slice", CDFoodValues.TROPICAL_FISH_SLICE, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> COOKED_TROPICAL_FISH_SLICE = food("cooked_tropical_fish_slice", CDFoodValues.COOKED_TROPICAL_FISH_SLICE, CDFoodValues.FAST_FOOD, true);
    public static final DeferredItem<ConsumableItem> STUFFED_NAUTILUS_SHELL = registerWithTab("stuffed_nautilus_shell", p -> new ConsumableItem(p, true),
            () -> foodItem(CDFoodValues.STUFFED_NAUTILUS_SHELL, CDFoodValues.STUFFED_NAUTILUS_SHELL_CONSUMABLE).craftRemainder(Items.NAUTILUS_SHELL));
    public static final DeferredItem<ConsumableItem> SQUID_KEBAB = food("squid_kebab", CDFoodValues.SQUID_KEBAB);
    public static final DeferredItem<ConsumableItem> FROG_LEG_KEBAB = food("frog_leg_kebab", CDFoodValues.FROG_LEG_KEBAB);
    public static final DeferredItem<ConsumableItem> JAR_OF_PICKLES = registerWithTab("jar_of_pickles", ConsumableItem::new,
            () -> foodItem(CDFoodValues.JAR_OF_PICKLES).craftRemainder(Items.GLASS_BOTTLE));

    // Drinks
    public static final DeferredItem<ConsumableItem> KELP_SHAKE = registerWithTab("kelp_shake", p -> new ConsumableItem(p, true),
            () -> drinkItem(CDFoodValues.KELP_SHAKE, CDFoodValues.KELP_SHAKE_CONSUMABLE));
    public static final DeferredItem<ConsumableItem> SEA_PICKLE_JUICE = registerWithTab("sea_pickle_juice", p -> new ConsumableItem(p, true),
            () -> drinkItem(CDFoodValues.SEA_PICKLE_JUICE, CDFoodValues.SEA_PICKLE_JUICE_CONSUMABLE));

    // Bowl Foods
    public static final DeferredItem<ConsumableItem> BISQUE = bowlFood("bisque", CDFoodValues.BISQUE, CDFoodValues.BISQUE_CONSUMABLE);
    public static final DeferredItem<ConsumableItem> SEAFOOD_GUMBO = bowlFood("seafood_gumbo", CDFoodValues.SEAFOOD_GUMBO, CDFoodValues.SEAFOOD_GUMBO_CONSUMABLE);
    public static final DeferredItem<ConsumableItem> CLAM_CHOWDER = bowlFood("clam_chowder", CDFoodValues.CLAM_CHOWDER, CDFoodValues.CLAM_CHOWDER_CONSUMABLE);
    public static final DeferredItem<ConsumableItem> SHRIMP_FRIED_RICE = bowlFood("shrimp_fried_rice", CDFoodValues.SHRIMP_FRIED_RICE, CDFoodValues.SHRIMP_FRIED_RICE_CONSUMABLE);
    public static final DeferredItem<ConsumableItem> CORAL_CRUNCH = bowlFood("coral_crunch", CDFoodValues.CORAL_CRUNCH, CDFoodValues.CORAL_CRUNCH_CONSUMABLE);
    public static final DeferredItem<ConsumableItem> TURTLE_STEW = registerWithTab("turtle_stew", p -> new ConsumableItem(p, true),
            () -> foodItem(CDFoodValues.TURTLE_STEW, CDFoodValues.TURTLE_STEW_CONSUMABLE).craftRemainder(Items.TURTLE_SCUTE));

    // Storage Blocks
    public static final DeferredItem<BlockItem> CRAB_BARREL = blockItem("crab_barrel", CDModBlocks.CRAB_BARREL);
    public static final DeferredItem<BlockItem> CLAM_BARREL = blockItem("clam_barrel", CDModBlocks.CLAM_BARREL);
    public static final DeferredItem<BlockItem> CLAWSTER_BARREL = blockItem("clawster_barrel", CDModBlocks.CLAWSTER_BARREL);
    public static final DeferredItem<BlockItem> SHRIMP_BARREL = blockItem("shrimp_barrel", CDModBlocks.SHRIMP_BARREL);
    public static final DeferredItem<BlockItem> COD_BARREL = blockItem("cod_barrel", CDModBlocks.COD_BARREL);
    public static final DeferredItem<BlockItem> SALMON_BARREL = blockItem("salmon_barrel", CDModBlocks.SALMON_BARREL);
    public static final DeferredItem<BlockItem> PUFFERFISH_BARREL = blockItem("pufferfish_barrel", CDModBlocks.PUFFERFISH_BARREL);
    public static final DeferredItem<BlockItem> TROPICAL_FISH_BARREL = blockItem("tropical_fish_barrel", CDModBlocks.TROPICAL_FISH_BARREL);
    public static final DeferredItem<BlockItem> SQUID_BARREL = blockItem("squid_barrel", CDModBlocks.SQUID_BARREL);
    public static final DeferredItem<BlockItem> GLOW_SQUID_BARREL = blockItem("glow_squid_barrel", CDModBlocks.GLOW_SQUID_BARREL);
    public static final DeferredItem<BlockItem> FROG_LEG_BARREL = blockItem("frog_leg_barrel", CDModBlocks.FROG_LEG_BARREL);
    public static final DeferredItem<BlockItem> COCONUT_CRATE = blockItem("coconut_crate", CDModBlocks.COCONUT_CRATE);
    public static final DeferredItem<BlockItem> NAUTILUS_SHELL_BLOCK = blockItem("nautilus_shell_block", CDModBlocks.NAUTILUS_SHELL_BLOCK);
    public static final DeferredItem<BlockItem> PEARL_BLOCK = blockItem("pearl_block", CDModBlocks.PEARL_BLOCK);
    public static final DeferredItem<BlockItem> SCUTE_BLOCK = blockItem("scute_block", CDModBlocks.SCUTE_BLOCK);
    public static final DeferredItem<BlockItem> SEA_PICKLE_CRATE = blockItem("sea_pickle_crate", CDModBlocks.SEA_PICKLE_CRATE);

    // Notes + Messages in a Bottle
    public static final DeferredItem<NoteItem> NOTE = registerWithTab("note", NoteItem::new, () -> new Item.Properties().stacksTo(16));
    public static final DeferredItem<SignedNoteItem> SIGNED_NOTE = registerWithTab("signed_note", SignedNoteItem::new, () -> new Item.Properties().stacksTo(16));
    public static final DeferredItem<MessageBottleItem> MESSAGE_BOTTLE = registerWithTab("message_bottle", MessageBottleItem::new);
    public static final DeferredItem<BottledNoteItem> BOTTLED_NOTE = registerWithTab("bottled_note", BottledNoteItem::new, () -> new Item.Properties().stacksTo(16));
}
