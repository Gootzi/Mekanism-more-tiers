package mekanism.client.jei.machine;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.recipes.FluidSlurryToSlurryRecipe;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.gauge.GuiSlurryGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.component.config.DataType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class FluidSlurryToSlurryRecipeCategory extends BaseRecipeCategory<FluidSlurryToSlurryRecipe> {

    private final GuiGauge<?> fluidInput;
    private final GuiGauge<?> slurryInput;
    private final GuiGauge<?> output;

    public FluidSlurryToSlurryRecipeCategory(IGuiHelper helper) {
        super(helper, MekanismBlocks.CHEMICAL_WASHER, 7, 13, 162, 60);
        fluidInput = addElement(GuiFluidGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 7, 13));
        slurryInput = addElement(GuiSlurryGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 28, 13));
        output = addElement(GuiSlurryGauge.getDummy(GaugeType.STANDARD.with(DataType.OUTPUT), this, 131, 13));
        addSlot(SlotType.POWER, 152, 14).with(SlotOverlay.POWER);
        addSlot(SlotType.OUTPUT, 152, 56).with(SlotOverlay.MINUS);
        addConstantProgress(ProgressType.LARGE_RIGHT, 64, 39);
    }

    @Override
    public Class<? extends FluidSlurryToSlurryRecipe> getRecipeClass() {
        return FluidSlurryToSlurryRecipe.class;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, FluidSlurryToSlurryRecipe recipe, @Nonnull List<? extends IFocus<?>> focuses) {
        initFluid(builder, 0, RecipeIngredientRole.INPUT, fluidInput, recipe.getFluidInput().getRepresentations());
        initChemical(builder, MekanismJEI.TYPE_SLURRY, 0, RecipeIngredientRole.INPUT, slurryInput, recipe.getChemicalInput().getRepresentations());
        initChemical(builder, MekanismJEI.TYPE_SLURRY, 1, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
    }
}