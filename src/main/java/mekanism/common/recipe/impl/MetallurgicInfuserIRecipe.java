package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.MetallurgicInfuserRecipe;
import mekanism.api.recipes.inputs.InfusionIngredient;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.MekanismBlock;
import mekanism.common.recipe.MekanismRecipeSerializers;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

public class MetallurgicInfuserIRecipe extends MetallurgicInfuserRecipe {

    public MetallurgicInfuserIRecipe(ResourceLocation id, ItemStackIngredient itemInput, InfusionIngredient infusionInput, ItemStack output) {
        super(id, itemInput, infusionInput, output);
    }

    @Nonnull
    @Override
    public IRecipeType<MetallurgicInfuserRecipe> getType() {
        return MekanismRecipeType.METALLURGIC_INFUSING;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<MetallurgicInfuserRecipe> getSerializer() {
        return MekanismRecipeSerializers.METALLURGIC_INFUSING;
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlock.METALLURGIC_INFUSER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return MekanismBlock.METALLURGIC_INFUSER.getItemStack();
    }
}