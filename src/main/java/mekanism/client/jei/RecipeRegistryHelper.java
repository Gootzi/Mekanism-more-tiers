package mekanism.client.jei;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlock;
import mekanism.common.integration.crafttweaker.handlers.EnergizedSmelter;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mezz.jei.api.registration.IRecipeRegistration;

public class RecipeRegistryHelper {

    public static void registerCondensentrator(IRecipeRegistration registry) {
        //TODO
        /*MekanismBlock mekanismBlock = MekanismBlock.ROTARY_CONDENSENTRATOR;
        if (!mekanismBlock.isEnabled()) {
            return;
        }
        List<RotaryCondensentratorRecipeWrapper> condensentratorRecipes = new ArrayList<>();
        List<RotaryCondensentratorRecipeWrapper> decondensentratorRecipes = new ArrayList<>();
        for (Gas gas : GasRegistry.getRegisteredGasses()) {
            if (gas.hasFluid()) {
                condensentratorRecipes.add(new RotaryCondensentratorRecipeWrapper(gas.getFluid(), gas, true));
                decondensentratorRecipes.add(new RotaryCondensentratorRecipeWrapper(gas.getFluid(), gas, false));
            }
        }
        ResourceLocation condensentrating = new ResourceLocation(Mekanism.MODID, "rotary_condensentrator_condensentrating");
        ResourceLocation decondensentrating = new ResourceLocation(Mekanism.MODID, "rotary_condensentrator_decondensentrating");
        registry.addRecipes(condensentratorRecipes, condensentrating);
        registry.addRecipes(decondensentratorRecipes, decondensentrating);*/
    }

    public static void registerSmelter(IRecipeRegistration registry) {
        MekanismBlock mekanismBlock = MekanismBlock.ENERGIZED_SMELTER;
        if (mekanismBlock.isEnabled()) {
            //TODO: Add all smelting recipes
            //registry.addRecipes(Collections.singleton(SmeltingRecipe.class), mekanismBlock.getJEICategory());
            if (Mekanism.hooks.CraftTweakerLoaded && EnergizedSmelter.hasRemovedRecipe()) {// Removed / Removed + Added
                registry.addRecipes(Recipe.ENERGIZED_SMELTER.get(), mekanismBlock.getRegistryName());
            } else if (Mekanism.hooks.CraftTweakerLoaded && EnergizedSmelter.hasAddedRecipe()) {// Added but not removed
                //TODO: Fix this
                // Only add added recipes
                /*Map<ItemStackInput, SmeltingRecipe> smeltingRecipes = Recipe.ENERGIZED_SMELTER.get();
                List<MachineRecipeWrapper> smeltingWrapper = new ArrayList<>();
                for (Entry<ItemStackInput, SmeltingRecipe> entry : smeltingRecipes.entrySet()) {
                    if (!FurnaceRecipes.instance().getSmeltingList().containsKey(entry.getKey().ingredient)) {
                        smeltingWrapper.add(new MachineRecipeWrapper<>(entry.getValue()));
                    }
                }
                registry.addRecipes(smeltingWrapper, mekanismBlock.getJEICategory());*/
            }
        }
    }

    public static <RECIPE extends MekanismRecipe> void register(IRecipeRegistration registry, MekanismBlock mekanismBlock, Recipe<RECIPE> type) {
        if (mekanismBlock.isEnabled()) {
            registry.addRecipes(type.get(), mekanismBlock.getRegistryName());
        }
    }
}