package mekanism.client.jei.machine;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.client.gui.element.bar.GuiHorizontalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.machine.TileEntityChemicalDissolutionChamber;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class ChemicalDissolutionRecipeCategory extends BaseRecipeCategory<ChemicalDissolutionRecipe> {

    private final GuiGauge<?> inputGauge;
    private final GuiGauge<?> outputGauge;
    private final GuiSlot inputSlot;

    public ChemicalDissolutionRecipeCategory(IGuiHelper helper) {
        super(helper, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER, 3, 3, 170, 79);
        //Note: This previously had a lang key for a shorter string. Though ideally especially due to translations
        // we will eventually instead just make the text scale
        inputGauge = addElement(GuiGasGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 7, 4));
        outputGauge = addElement(GuiGasGauge.getDummy(GaugeType.STANDARD.with(DataType.OUTPUT), this, 131, 13));
        inputSlot = addSlot(SlotType.INPUT, 28, 36);
        addSlot(SlotType.EXTRA, 8, 65).with(SlotOverlay.MINUS);
        addSlot(SlotType.OUTPUT, 152, 55).with(SlotOverlay.PLUS);
        addSlot(SlotType.POWER, 152, 14).with(SlotOverlay.POWER);
        addSimpleProgress(ProgressType.LARGE_RIGHT, 64, 40);
        addElement(new GuiHorizontalPowerBar(this, FULL_BAR, 115, 75));
    }

    @Override
    public Class<? extends ChemicalDissolutionRecipe> getRecipeClass() {
        return ChemicalDissolutionRecipe.class;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, ChemicalDissolutionRecipe recipe, @Nonnull List<? extends IFocus<?>> focuses) {
        initItem(builder, 0, RecipeIngredientRole.INPUT, inputSlot, recipe.getItemInput().getRepresentations());
        List<@NonNull GasStack> gasInputs = recipe.getGasInput().getRepresentations();
        List<GasStack> scaledGases = gasInputs.stream().map(gas -> new GasStack(gas, gas.getAmount() * TileEntityChemicalDissolutionChamber.BASE_TICKS_REQUIRED))
              .toList();
        initChemical(builder, MekanismJEI.TYPE_GAS, 0, RecipeIngredientRole.INPUT, inputGauge, scaledGases);
        BoxedChemicalStack outputDefinition = recipe.getOutputDefinition();
        ChemicalType chemicalType = outputDefinition.getChemicalType();
        if (chemicalType == ChemicalType.GAS) {
            initChemicalOutput(builder, MekanismJEI.TYPE_GAS, (GasStack) outputDefinition.getChemicalStack());
        } else if (chemicalType == ChemicalType.INFUSION) {
            initChemicalOutput(builder, MekanismJEI.TYPE_INFUSION, (InfusionStack) outputDefinition.getChemicalStack());
        } else if (chemicalType == ChemicalType.PIGMENT) {
            initChemicalOutput(builder, MekanismJEI.TYPE_PIGMENT, (PigmentStack) outputDefinition.getChemicalStack());
        } else if (chemicalType == ChemicalType.SLURRY) {
            initChemicalOutput(builder, MekanismJEI.TYPE_SLURRY, (SlurryStack) outputDefinition.getChemicalStack());
        } else {
            throw new IllegalStateException("Unknown chemical type");
        }
    }

    private <STACK extends ChemicalStack<?>> void initChemicalOutput(IRecipeLayoutBuilder builder, IIngredientType<STACK> type, STACK stack) {
        initChemical(builder, type, 1, RecipeIngredientRole.OUTPUT, outputGauge, Collections.singletonList(stack));
    }
}