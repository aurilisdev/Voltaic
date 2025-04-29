package voltaic.common.recipe.recipeutils;

import java.util.Collections;
import java.util.List;

import voltaic.common.recipe.VoltaicRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class AbstractMaterialRecipe extends VoltaicRecipe {

	public AbstractMaterialRecipe(ResourceLocation recipeGroup, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts) {
		super(recipeGroup, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts);
	}
	
	@Override
	public ItemStack assemble(RecipeWrapper pContainer) {
		return getItemRecipeOutput();
	}
	
	@Override
	public ItemStack getResultItem() {
		return getItemRecipeOutput();
	}

	public FluidStack getFluidRecipeOutput() {
		return FluidStack.EMPTY;
	}

	public List<FluidIngredient> getFluidIngredients() {
		return Collections.emptyList();
	}

	public ItemStack getItemRecipeOutput() {
		return ItemStack.EMPTY;
	}

}
