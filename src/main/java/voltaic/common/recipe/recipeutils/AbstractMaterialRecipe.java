package voltaic.common.recipe.recipeutils;

import java.util.Collections;
import java.util.List;

import voltaic.api.gas.GasStack;
import voltaic.common.recipe.VoltaicRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class AbstractMaterialRecipe extends VoltaicRecipe {

	public AbstractMaterialRecipe(ResourceLocation recipeGroup, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts, List<ProbableGas> gasBiproducts) {
		super(recipeGroup, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts, gasBiproducts);
	}
	
	@Override
	public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
		return getItemRecipeOutput();
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return getItemRecipeOutput();
	}

	public FluidStack getFluidRecipeOutput() {
		return FluidStack.EMPTY;
	}

	public GasStack getGasRecipeOutput() {
		return GasStack.EMPTY;
	}

	public List<FluidIngredient> getFluidIngredients() {
		return Collections.emptyList();
	}

	public List<GasIngredient> getGasIngredients() {
		return Collections.emptyList();
	}

	public ItemStack getItemRecipeOutput() {
		return ItemStack.EMPTY;
	}

}
