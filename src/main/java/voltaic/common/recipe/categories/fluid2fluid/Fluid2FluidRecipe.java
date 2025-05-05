package voltaic.common.recipe.categories.fluid2fluid;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentFluidHandlerMulti;
import voltaic.prefab.tile.components.type.ComponentProcessor;

public abstract class Fluid2FluidRecipe extends AbstractMaterialRecipe {

    private List<FluidIngredient> inputFluidIngredients;
    private FluidStack outputFluidStack;

    public Fluid2FluidRecipe(ResourceLocation recipeGroup, List<FluidIngredient> inputFluids, FluidStack outputFluid, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts) {
        super(recipeGroup, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts);
        inputFluidIngredients = inputFluids;
        outputFluidStack = outputFluid;
    }

    @Override
    public boolean matchesRecipe(ComponentProcessor pr, int procNumber) {
        Pair<List<Integer>, Boolean> pair = areFluidsValid(getFluidIngredients(), pr.getHolder().<ComponentFluidHandlerMulti>getComponent(IComponentType.FluidHandler).getInputTanks());
        if (pair.getSecond()) {
            setFluidArrangement(pair.getFirst());
            return true;
        }
        return false;
    }

    @Override
    public FluidStack getFluidRecipeOutput() {
        return outputFluidStack;
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        return inputFluidIngredients;
    }

    public interface Factory<T extends Fluid2FluidRecipe> {

        T create(ResourceLocation recipeGroup, List<FluidIngredient> inputFluids, FluidStack outputFluid, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts);

    }

}
