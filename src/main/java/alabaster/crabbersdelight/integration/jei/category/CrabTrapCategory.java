package alabaster.crabbersdelight.integration.jei.category;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.CrabTrapBlockEntity;
import alabaster.crabbersdelight.common.registry.CDModItems;
import alabaster.crabbersdelight.integration.jei.CrabTrapRecipeWrapper;
import alabaster.crabbersdelight.integration.jei.JEIPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CrabTrapCategory implements IRecipeCategory<CrabTrapRecipeWrapper> {
    private static final Identifier CRAB_TRAP_LOCATION = CrabbersDelight.modPrefix("textures/gui/jei_crab_trap.png");
    private static final int WIDTH = 79;
    private static final int HEIGHT = 39;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public CrabTrapCategory(IGuiHelper helper) {
        this.title = Component.translatable("block.crabbersdelight.crab_trap");
        this.background = helper.createDrawable(CRAB_TRAP_LOCATION, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemLike(CDModItems.CRAB_TRAP.get());
    }

    @Override
    public IRecipeType<CrabTrapRecipeWrapper> getRecipeType() {
        return JEIPlugin.CRAB_TRAP_RECIPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CrabTrapRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    private static boolean iconPosition(double mouseX, double mouseY) {
        return 32 <= mouseX && mouseX < 32 + 15 && 2 <= mouseY && mouseY < 2 + 10;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CrabTrapRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!iconPosition(mouseX, mouseY)) {
            return;
        }
        var minMax = CrabTrapBlockEntity.getMinMax();
        if (minMax.getSecond() > minMax.getFirst()) {
            tooltip.add(Component.literal("Collects every " + minMax.getFirst() + "-" + minMax.getSecond() + " ticks"));
        } else {
            tooltip.add(Component.literal("Error: Minimum value is higher than maximum value!").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrabTrapRecipeWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 16).add(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 16).addItemStacks(recipe.outputs());
    }
}
