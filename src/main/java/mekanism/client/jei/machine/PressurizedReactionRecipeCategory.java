package mekanism.client.jei.machine;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
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
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public class PressurizedReactionRecipeCategory extends BaseRecipeCategory<PressurizedReactionRecipe> {

    private final GuiGauge<?> inputGas;
    private final GuiGauge<?> inputFluid;
    private final GuiSlot inputItem;
    private final GuiSlot outputItem;
    private final GuiGauge<?> outputGas;

    public PressurizedReactionRecipeCategory(IGuiHelper helper) {
        super(helper, MekanismBlocks.PRESSURIZED_REACTION_CHAMBER, 3, 10, 170, 60);
        //Note: This previously had a lang key for a shorter string. Though ideally especially due to translations
        // we will eventually instead just make the text scale
        inputItem = addSlot(SlotType.INPUT, 54, 35);
        outputItem = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 141, 17).with(SlotOverlay.POWER);
        inputFluid = addElement(GuiFluidGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 5, 10));
        inputGas = addElement(GuiGasGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 28, 10));
        outputGas = addElement(GuiGasGauge.getDummy(GaugeType.SMALL.with(DataType.OUTPUT), this, 140, 40));
        addElement(new GuiVerticalPowerBar(this, FULL_BAR, 164, 15));
        addSimpleProgress(ProgressType.RIGHT, 77, 38);
    }

    @Override
    public Class<? extends PressurizedReactionRecipe> getRecipeClass() {
        return PressurizedReactionRecipe.class;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, PressurizedReactionRecipe recipe, @Nonnull List<? extends IFocus<?>> focuses) {
        initItem(builder, 0, RecipeIngredientRole.INPUT, inputItem, recipe.getInputSolid().getRepresentations());
        Pair<List<@NonNull ItemStack>, @NonNull GasStack> outputDefinition = recipe.getOutputDefinition();
        initItem(builder, 1, RecipeIngredientRole.OUTPUT, outputItem, outputDefinition.getLeft());
        initFluid(builder, 0, RecipeIngredientRole.INPUT, inputFluid, recipe.getInputFluid().getRepresentations());
        initChemical(builder, MekanismJEI.TYPE_GAS, 0, RecipeIngredientRole.INPUT, inputGas, recipe.getInputGas().getRepresentations());
        initChemical(builder, MekanismJEI.TYPE_GAS, 1, RecipeIngredientRole.OUTPUT, outputGas, Collections.singletonList(outputDefinition.getRight()));
    }
}