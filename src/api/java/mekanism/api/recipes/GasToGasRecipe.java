package mekanism.api.recipes;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.chemical.ChemicalToChemicalRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;

/**
 * Input: Gas
 * <br>
 * Output: GasStack
 *
 * @apiNote There are currently two types of Gas to Gas recipe types:
 * <ul>
 *     <li>Activating: Can be processed in a Solar Neutron Activator.</li>
 *     <li>Centrifuging: Can be processed in an Isotopic Centrifuge.</li>
 * </ul>
 */
@ParametersAreNotNullByDefault
public abstract class GasToGasRecipe extends ChemicalToChemicalRecipe<Gas, GasStack, GasStackIngredient> {

    /**
     * @param input  Input.
     * @param output Output.
     */
    public GasToGasRecipe(GasStackIngredient input, GasStack output) {
        super(input, output);
    }
}