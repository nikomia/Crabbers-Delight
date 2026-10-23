package alabaster.crabbersdelight.integration.jei;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.registry.CDModItems;
import alabaster.crabbersdelight.common.tags.CDModTags;
import alabaster.crabbersdelight.integration.jei.category.CrabTrapCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = CrabbersDelight.modPrefix("jei_plugin");
    public static final IRecipeType<CrabTrapRecipeWrapper> CRAB_TRAP_RECIPE =
            IRecipeType.create(CrabbersDelight.MODID, "crab_trap_loot", CrabTrapRecipeWrapper.class);

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrabTrapCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CRAB_TRAP_RECIPE, crabTrapRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(CRAB_TRAP_RECIPE, CDModItems.CRAB_TRAP.get());
    }

    // Catches shown per bait come from the jei_display_results/<namespace>/<bait> item tags
    private static List<CrabTrapRecipeWrapper> crabTrapRecipes() {
        List<CrabTrapRecipeWrapper> list = new ArrayList<>();
        for (Holder<Item> bait : BuiltInRegistries.ITEM.getTagOrEmpty(CDModTags.CRAB_TRAP_BAIT)) {
            Identifier baitId = BuiltInRegistries.ITEM.getKey(bait.value());
            TagKey<Item> outputTag = TagKey.create(Registries.ITEM,
                    CrabbersDelight.modPrefix("jei_display_results/" + baitId.getNamespace() + "/" + baitId.getPath()));
            List<ItemStack> outputs = new ArrayList<>();
            for (Holder<Item> output : BuiltInRegistries.ITEM.getTagOrEmpty(outputTag)) {
                outputs.add(new ItemStack(output));
            }
            if (!outputs.isEmpty()) {
                list.add(new CrabTrapRecipeWrapper(new ItemStack(bait), outputs));
            }
        }
        return list;
    }
}
