package mekanism.common.recipe.impl;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@NothingNullByDefault
public class PurifyingIRecipe extends ItemStackGasToItemStackRecipe {

    public PurifyingIRecipe(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output) {
        super(itemInput, gasInput, output);
    }

    @Override
    public RecipeType<ItemStackGasToItemStackRecipe> getType() {
        return MekanismRecipeType.PURIFYING.get();
    }

    @Override
    public RecipeSerializer<ItemStackGasToItemStackRecipe> getSerializer() {
        return MekanismRecipeSerializers.PURIFYING.get();
    }

    @Override
    public String getGroup() {
        return MekanismBlocks.PURIFICATION_CHAMBER.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MekanismBlocks.PURIFICATION_CHAMBER.getItemStack();
    }
}