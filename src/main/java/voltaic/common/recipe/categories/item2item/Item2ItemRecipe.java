package voltaic.common.recipe.categories.item2item;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentProcessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public abstract class Item2ItemRecipe extends VoltaicRecipe {

    private List<CountableIngredient> inputItems;
    private ItemStack outputItem;

    public Item2ItemRecipe(String group, List<CountableIngredient> inputs, ItemStack output, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts, List<ProbableGas> gasBiproducts) {
        super(group, experience, ticks, usagePerTick, itemBiproducts, fluidBiproducts, gasBiproducts);
        inputItems = inputs;
        outputItem = output;
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
    public ItemStack assemble(VoltaicRecipe p_345149_, HolderLookup.Provider p_346030_) {
        return getItemRecipeOutput();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return getItemRecipeOutput();
    }

    public ItemStack getItemRecipeOutput() {
        return outputItem;
    }

    public List<CountableIngredient> getCountedIngredients() {
        return inputItems;
    }

    public interface Factory<T extends Item2ItemRecipe> {

        T create(String group, List<CountableIngredient> inputs, ItemStack output, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts, List<ProbableGas> gasBiproducts);

    }

}
