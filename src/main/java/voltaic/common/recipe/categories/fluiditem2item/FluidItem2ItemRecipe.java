package voltaic.common.recipe.categories.fluiditem2item;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentFluidHandlerMulti;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentProcessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class FluidItem2ItemRecipe extends AbstractMaterialRecipe {

    private List<CountableIngredient> ingredients;
    private List<FluidIngredient> fluidIngredients;
    private ItemStack outputItemStack;

    public FluidItem2ItemRecipe(ResourceLocation group, List<CountableIngredient> itemInputs, List<FluidIngredient> fluidInputs, ItemStack itemOutput, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts, List<ProbableGas> gasBiproducts) {
        super(group, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts, gasBiproducts);
        ingredients = itemInputs;
        fluidIngredients = fluidInputs;
        outputItemStack = itemOutput;
    }

    @Override
    public boolean matchesRecipe(ComponentProcessor pr, int procNumber) {
        Pair<List<Integer>, Boolean> itemPair = areItemsValid(getCountedIngredients(), ((ComponentInventory) pr.getHolder().getComponent(IComponentType.Inventory)).getInputsForProcessor(procNumber));
        if (itemPair.getSecond()) {
            Pair<List<Integer>, Boolean> fluidPair = areFluidsValid(getFluidIngredients(), pr.getHolder().<ComponentFluidHandlerMulti>getComponent(IComponentType.FluidHandler).getInputTanks());
            if (fluidPair.getSecond()) {
                setItemArrangement(procNumber, itemPair.getFirst());
                setFluidArrangement(fluidPair.getFirst());
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getItemRecipeOutput() {
        return outputItemStack;
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    public List<CountableIngredient> getCountedIngredients() {
        return ingredients;
    }

    public interface Factory<T extends FluidItem2ItemRecipe> {

        T create(ResourceLocation group, List<CountableIngredient> itemInputs, List<FluidIngredient> fluidInputs, ItemStack itemOutput, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts, List<ProbableGas> gasBiproducts);

    }

}
