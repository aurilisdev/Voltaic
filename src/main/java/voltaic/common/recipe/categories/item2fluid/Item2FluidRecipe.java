package voltaic.common.recipe.categories.item2fluid;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentProcessor;

public abstract class Item2FluidRecipe extends AbstractMaterialRecipe {

    private List<CountableIngredient> inputItems;
    private FluidStack outputFluid;

    public Item2FluidRecipe(ResourceLocation group, List<CountableIngredient> itemInputs, FluidStack fluidOutput, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts) {
        super(group, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts);
        inputItems = itemInputs;
        outputFluid = fluidOutput;
    }

    @Override
    public boolean matchesRecipe(ComponentProcessor pr, int procNumber) {
        Pair<List<Integer>, Boolean> pair = areItemsValid(getCountedIngredients(), ((ComponentInventory) pr.getHolder().getComponent(IComponentType.Inventory)).getInputsForProcessor(procNumber));
        if (pair.getSecond()) {
            setItemArrangement(procNumber, pair.getFirst());
            return true;
        }
        return false;
    }

    @Override
    public FluidStack getFluidRecipeOutput() {
        return outputFluid;
    }

    public List<CountableIngredient> getCountedIngredients() {
        List<CountableIngredient> list = new ArrayList<>();
        for (CountableIngredient ing : inputItems) {
            list.add(ing);
        }
        return list;
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        return new ArrayList<>();
    }

    public interface Factory<T extends Item2FluidRecipe> {

        T create(ResourceLocation group, List<CountableIngredient> itemInputs, FluidStack fluidOutput, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts);

    }

}
