package mekanism.client.jei.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.api.recipes.inputs.chemical.InfusionStackIngredient;
import mekanism.api.recipes.inputs.chemical.PigmentStackIngredient;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.machine.GuiChemicalCrystallizer;
import mekanism.client.gui.machine.GuiChemicalCrystallizer.IOreInfo;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.component.config.DataType;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ChemicalCrystallizerRecipeCategory extends BaseRecipeCategory<ChemicalCrystallizerRecipe> {

    private static final String CHEMICAL_INPUT = "chemicalInput";
    private static final String DISPLAYED_ITEM = "displayedItem";

    private final OreInfo oreInfo = new OreInfo();
    private final GuiGauge<?> gauge;
    private final GuiSlot output;
    private final GuiSlot slurryOreSlot;

    public ChemicalCrystallizerRecipeCategory(IGuiHelper helper) {
        super(helper, MekanismBlocks.CHEMICAL_CRYSTALLIZER, 5, 3, 147, 79);
        gauge = addElement(GuiGasGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 7, 4));
        addSlot(SlotType.INPUT, 8, 65).with(SlotOverlay.PLUS);
        output = addSlot(SlotType.OUTPUT, 129, 57);
        addSimpleProgress(ProgressType.LARGE_RIGHT, 53, 61);
        addElement(new GuiInnerScreen(this, 31, 13, 115, 42, () -> GuiChemicalCrystallizer.getScreenRenderStrings(this.oreInfo)));
        slurryOreSlot = addElement(new GuiSlot(SlotType.ORE, this, 128, 13).setRenderAboveSlots());
    }

    @Override
    public Class<? extends ChemicalCrystallizerRecipe> getRecipeClass() {
        return ChemicalCrystallizerRecipe.class;
    }

    @Override
    public void draw(ChemicalCrystallizerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        //Set what the "current" recipe is for our ore info
        oreInfo.currentRecipe = recipe;
        oreInfo.ingredient = (ChemicalStack<?>) recipeSlotsView.findSlotByName(CHEMICAL_INPUT)
              .flatMap(IRecipeSlotView::getDisplayedIngredient)
              .map(ITypedIngredient::getIngredient)
              .filter(ingredient -> ingredient instanceof ChemicalStack)
              .orElse(null);
        oreInfo.itemIngredient = getDisplayedStack(recipeSlotsView, DISPLAYED_ITEM, VanillaTypes.ITEM, ItemStack.EMPTY);
        super.draw(recipe, recipeSlotsView, matrixStack, mouseX, mouseY);
        oreInfo.currentRecipe = null;
        oreInfo.ingredient = null;
        oreInfo.itemIngredient = ItemStack.EMPTY;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, ChemicalCrystallizerRecipe recipe, @Nonnull List<? extends IFocus<?>> focuses) {
        initItem(builder, 0, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
        ChemicalStackIngredient<?, ?> input = recipe.getInput();
        if (input instanceof GasStackIngredient ingredient) {
            initChemical(builder, MekanismJEI.TYPE_GAS, ingredient);
        } else if (input instanceof InfusionStackIngredient ingredient) {
            initChemical(builder, MekanismJEI.TYPE_INFUSION, ingredient);
        } else if (input instanceof PigmentStackIngredient ingredient) {
            initChemical(builder, MekanismJEI.TYPE_PIGMENT, ingredient);
        } else if (input instanceof SlurryStackIngredient ingredient) {
            initChemical(builder, MekanismJEI.TYPE_SLURRY, ingredient);
            Set<Tag<Item>> tags = new HashSet<>();
            for (SlurryStack slurryStack : ingredient.getRepresentations()) {
                Slurry slurry = slurryStack.getType();
                if (!slurry.isIn(MekanismTags.Slurries.DIRTY)) {
                    Tag<Item> oreTag = slurry.getOreTag();
                    if (oreTag != null) {
                        tags.add(oreTag);
                    }
                }
            }
            if (tags.size() == 1) {
                //TODO: Eventually come up with a better way to do this to allow for if there outputs based on the input and multiple input types
                tags.stream().findFirst().ifPresent(tag -> initItem(builder, IGNORED_INDEX, RecipeIngredientRole.RENDER_ONLY, slurryOreSlot,
                      tag.getValues().stream().map(ItemStack::new).toList()).setSlotName(DISPLAYED_ITEM));
            }
        }
    }

    private <STACK extends ChemicalStack<?>> void initChemical(IRecipeLayoutBuilder builder, IIngredientType<STACK> type, ChemicalStackIngredient<?, STACK> ingredient) {
        initChemical(builder, type, 0, RecipeIngredientRole.INPUT, gauge, ingredient.getRepresentations())
              .setSlotName(CHEMICAL_INPUT);
    }

    private static class OreInfo implements IOreInfo {

        @Nullable
        private ChemicalCrystallizerRecipe currentRecipe;
        @Nullable
        private ChemicalStack<?> ingredient;
        private ItemStack itemIngredient = ItemStack.EMPTY;

        @Nonnull
        @Override
        public BoxedChemicalStack getInputChemical() {
            if (ingredient == null || ingredient.isEmpty()) {
                return BoxedChemicalStack.EMPTY;
            }
            return BoxedChemicalStack.box(ingredient);
        }

        @Nullable
        @Override
        public ChemicalCrystallizerRecipe getRecipe() {
            return currentRecipe;
        }

        @Nonnull
        @Override
        public ItemStack getRenderStack() {
            return itemIngredient;
        }
    }
}