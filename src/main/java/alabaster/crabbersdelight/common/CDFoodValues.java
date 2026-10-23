package alabaster.crabbersdelight.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Food values. Since 1.21.2 effects and eating speed live in the consumable component, so every food
 * has a {@link FoodProperties} plus a {@link Consumable}.
 */
public class CDFoodValues {
    public static final int BRIEF_DURATION = 600;    // 30 seconds
    public static final int SHORT_DURATION = 1200;    // 1 minute
    public static final int MEDIUM_DURATION = 3600;    // 3 minutes
    public static final int LONG_DURATION = 6000;    // 5 minutes

    // Lazy reference: touching FD's ModEffects during mod construction registers all FD objects too early
    public static final DeferredHolder<MobEffect, MobEffect> NOURISHMENT =
            DeferredHolder.create(Registries.MOB_EFFECT, Identifier.fromNamespaceAndPath("farmersdelight", "nourishment"));

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    private static MobEffectInstance nourishment(int duration) {
        return new MobEffectInstance(NOURISHMENT, duration, 0, false, false);
    }

    private static Consumable fastFood() {
        return Consumables.defaultFood().consumeSeconds(0.8F).build();
    }

    private static Consumable foodWith(MobEffectInstance effect, float chance) {
        return Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(effect, chance)).build();
    }

    private static Consumable fastFoodWith(MobEffectInstance effect, float chance) {
        return Consumables.defaultFood().consumeSeconds(0.8F).onConsume(new ApplyStatusEffectsConsumeEffect(effect, chance)).build();
    }

    public static final Consumable DEFAULT_FOOD = Consumables.DEFAULT_FOOD;
    public static final Consumable FAST_FOOD = fastFood();

    public static final FoodProperties RAW_CRAB = food(2, 0.3f);
    public static final FoodProperties COOKED_CRAB = food(4, 0.8f);
    public static final FoodProperties RAW_CLAWSTER = food(3, 0.5f);
    public static final FoodProperties COOKED_CLAWSTER = food(7, 0.8f);

    public static final FoodProperties RAW_SHRIMP = food(1, 0.3f);
    public static final Consumable RAW_SHRIMP_CONSUMABLE = fastFoodWith(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F);
    public static final FoodProperties COOKED_SHRIMP = food(3, 0.6f);
    public static final FoodProperties RAW_CLAM_MEAT = food(2, 0.3f);
    public static final FoodProperties COOKED_CLAM_MEAT = food(4, 0.8f);
    public static final FoodProperties RAW_SQUID_TENTACLES = food(3, 0.5f);
    public static final FoodProperties COOKED_SQUID_TENTACLES = food(7, 0.8f);
    public static final FoodProperties RAW_FROG_LEG = food(2, 0.3f);
    public static final FoodProperties COOKED_FROG_LEG = food(4, 0.7f);
    public static final FoodProperties FRIED_FISH = food(5, 0.6f);
    public static final FoodProperties FISH_SANDWICH = food(10, 0.8f);

    // Drinks
    public static final FoodProperties KELP_SHAKE = new FoodProperties.Builder().alwaysEdible().build();
    public static final Consumable KELP_SHAKE_CONSUMABLE = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 300, 0), 1.0F)).build();
    public static final FoodProperties COCONUT_MILK = new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(0.5f).build();
    public static final Consumable COCONUT_MILK_CONSUMABLE = Consumables.DEFAULT_DRINK;
    public static final FoodProperties COCONUT_PUDDING = food(6, 0.5f);

    // Slices
    public static final FoodProperties COOKED_TROPICAL_FISH = food(3, 0.8f);
    public static final FoodProperties COOKED_TROPICAL_FISH_SLICE = food(3, 0.5f);
    public static final FoodProperties TROPICAL_FISH_SLICE = food(1, 0.1f);
    public static final FoodProperties COOKED_PUFFERFISH_SLICE = food(3, 0.5f);
    public static final FoodProperties PUFFERFISH_SLICE = food(1, 0.1f);
    public static final Consumable PUFFERFISH_SLICE_CONSUMABLE = fastFoodWith(new MobEffectInstance(MobEffects.POISON, 300, 0), 0.3F);
    public static final FoodProperties COCONUT_HALVE = food(2, 0.4f);

    // Meals
    public static final FoodProperties CRAB_LEGS = food(3, 0.6f);
    public static final FoodProperties SHRIMP_SKEWER = food(8, 0.8f);
    public static final FoodProperties SHRIMP_FRIED_RICE = food(7, 0.7f);
    public static final Consumable SHRIMP_FRIED_RICE_CONSUMABLE = foodWith(nourishment(BRIEF_DURATION), 1.0F);
    public static final FoodProperties SURF_AND_TURF = food(14, 0.8f);
    public static final Consumable SURF_AND_TURF_CONSUMABLE = foodWith(nourishment(LONG_DURATION), 1.0F);
    public static final FoodProperties CLAM_BAKE = food(13, 0.9f);
    public static final Consumable CLAM_BAKE_CONSUMABLE = foodWith(nourishment(LONG_DURATION), 1.0F);
    public static final FoodProperties CLAM_CHOWDER = food(7, 0.6f);
    public static final Consumable CLAM_CHOWDER_CONSUMABLE = foodWith(nourishment(MEDIUM_DURATION), 1.0F);
    public static final FoodProperties BISQUE = food(8, 0.75f);
    public static final Consumable BISQUE_CONSUMABLE = foodWith(nourishment(MEDIUM_DURATION), 1.0F);
    public static final FoodProperties SEAFOOD_GUMBO = food(12, 0.9f);
    public static final Consumable SEAFOOD_GUMBO_CONSUMABLE = foodWith(nourishment(MEDIUM_DURATION), 1.0F);
    public static final FoodProperties FISH_STICK = food(5, 0.6f);
    public static final FoodProperties CRAB_CAKES = food(10, 0.6f);
    public static final Consumable CRAB_CAKES_CONSUMABLE = foodWith(nourishment(SHORT_DURATION), 1.0F);
    public static final FoodProperties STUFFED_NAUTILUS_SHELL = food(8, 0.7f);
    public static final Consumable STUFFED_NAUTILUS_SHELL_CONSUMABLE = foodWith(nourishment(MEDIUM_DURATION), 1.0F);
    public static final FoodProperties JAR_OF_PICKLES = food(4, 0.4f);
    public static final FoodProperties SEA_PICKLE_JUICE = food(2, 0.2f);
    public static final Consumable SEA_PICKLE_JUICE_CONSUMABLE = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, BRIEF_DURATION, 0), 1.0F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, MEDIUM_DURATION, 0), 1.0F))
            .build();
    public static final FoodProperties SQUID_KEBAB = food(8, 0.8f);
    public static final FoodProperties FROG_LEG_KEBAB = food(9, 0.8f);
    public static final FoodProperties CORAL_CRUNCH = food(4, 0.2f);
    public static final Consumable CORAL_CRUNCH_CONSUMABLE = fastFoodWith(nourishment(SHORT_DURATION), 1.0F);
    public static final FoodProperties TURTLE_STEW = food(7, 0.6f);
    public static final Consumable TURTLE_STEW_CONSUMABLE = fastFoodWith(new MobEffectInstance(MobEffects.RESISTANCE, SHORT_DURATION, 0), 1.0F);
}
