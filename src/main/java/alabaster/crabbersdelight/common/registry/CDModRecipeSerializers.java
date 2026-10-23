package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CDModRecipeSerializers {
    // Recipes are registry values in 26.3 and must be distinct objects, so no shared singleton
    private static <T extends Recipe<?>> RecipeSerializer<T> unit(Supplier<T> factory) {
        return new RecipeSerializer<>(MapCodec.unit(factory), StreamCodec.of((buf, recipe) -> {}, buf -> factory.get()));
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CrabbersDelight.MODID);


}
