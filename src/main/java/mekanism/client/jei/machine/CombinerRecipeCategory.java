package mekanism.client.jei.machine;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.CombinerRecipe;
import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class CombinerRecipeCategory extends BaseRecipeCategory<CombinerRecipe> {

    private final GuiSlot input;
    private final GuiSlot extra;
    private final GuiSlot output;

    public CombinerRecipeCategory(IGuiHelper helper, IBlockProvider mekanismBlock) {
        super(helper, mekanismBlock, 28, 16, 144, 54);
        addElement(new GuiUpArrow(this, 68, 38));
        input = addSlot(SlotType.INPUT, 64, 17);
        extra = addSlot(SlotType.EXTRA, 64, 53);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 39, 35).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, FULL_BAR, 164, 15));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public Class<? extends CombinerRecipe> getRecipeClass() {
        return CombinerRecipe.class;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, CombinerRecipe recipe, @Nonnull List<? extends IFocus<?>> focuses) {
        initItem(builder, 0, RecipeIngredientRole.INPUT, input, recipe.getMainInput().getRepresentations());
        initItem(builder, 1, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
        initItem(builder, 2, RecipeIngredientRole.INPUT, extra, recipe.getExtraInput().getRepresentations());
    }
}