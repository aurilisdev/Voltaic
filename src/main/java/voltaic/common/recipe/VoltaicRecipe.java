package voltaic.common.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.tile.components.type.ComponentProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class VoltaicRecipe implements IRecipe<RecipeWrapper> {

    private final ResourceLocation group;

    private final double xp;
    private final int ticks;
    private final double usagePerTick;

    private final List<ProbableItem> itemBiproducts;
    private final List<ProbableFluid> fluidBiproducts;

    private final HashMap<Integer, List<Integer>> itemArrangements = new HashMap<>();
    @Nullable
    private List<Integer> fluidArrangement;
    @Nullable
    private List<Integer> gasArrangement;

    public VoltaicRecipe(ResourceLocation recipeGroup, double experience, int ticks, double usagePerTick, List<ProbableItem> itemBiproducts, List<ProbableFluid> fluidBiproducts) {
        group = recipeGroup;
        xp = experience;
        this.ticks = ticks;
        this.usagePerTick = usagePerTick;
        this.itemBiproducts = itemBiproducts;
        this.fluidBiproducts = fluidBiproducts;
    }

    /**
     * NEVER USE THIS METHOD!
     */
    @Override
    public boolean matches(RecipeWrapper pContainer, World pLevel) {
    	// TODO Auto-generated method stub
    	return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
    	return group;
    }

    public boolean hasItemBiproducts() {
        return itemBiproducts.size() != 0;
    }

    public boolean hasFluidBiproducts() {
        return fluidBiproducts.size() != 0;
    }

    @Nullable
    public List<ProbableItem> getItemBiproducts() {
        return itemBiproducts;
    }

    @Nullable
    public List<ProbableFluid> getFluidBiproducts() {
        return fluidBiproducts;
    }

    public ItemStack[] getFullItemBiStacks() {
        ItemStack[] items = new ItemStack[getItemBiproductCount()];
        for (int i = 0; i < getItemBiproductCount(); i++) {
            items[i] = itemBiproducts.get(i).getFullStack();
        }
        return items;
    }

    public FluidStack[] getFullFluidBiStacks() {
        FluidStack[] fluids = new FluidStack[getFluidBiproductCount()];
        for (int i = 0; i < getFluidBiproductCount(); i++) {
            fluids[i] = fluidBiproducts.get(i).getFullStack();
        }
        return fluids;
    }

    public int getItemBiproductCount() {
        return itemBiproducts.size();
    }

    public int getFluidBiproductCount() {
        return fluidBiproducts.size();
    }

    public double getXp() {
        return xp;
    }

    public int getTicks() {
        return ticks;
    }

    public double getUsagePerTick() {
        return usagePerTick;
    }

    public void setItemArrangement(Integer procNumber, List<Integer> arrangement) {
        itemArrangements.put(procNumber, arrangement);
    }

    public List<Integer> getItemArrangment(Integer procNumber) {
        return itemArrangements.get(procNumber);
    }

    public void setFluidArrangement(List<Integer> arrangement) {
        fluidArrangement = arrangement;
    }

    public List<Integer> getFluidArrangement() {
        return fluidArrangement;
    }

    public void setGasArrangement(List<Integer> arrangement) {
        gasArrangement = arrangement;
    }

    public List<Integer> getGasArrangement() {
        return gasArrangement;
    }

    public static List<VoltaicRecipe> findRecipesbyType(IRecipeType<? extends VoltaicRecipe> typeIn, World world) {
        return world != null ? world.getRecipeManager().getAllRecipesFor((IRecipeType<VoltaicRecipe>) typeIn) : Collections.emptyList();
    }

    @Nullable
    public static VoltaicRecipe getRecipe(ComponentProcessor pr, List<VoltaicRecipe> cachedRecipes, int index) {
        for (VoltaicRecipe recipe : cachedRecipes) {
            if (recipe.matchesRecipe(pr, index)) {
                return recipe;
            }
        }
        return null;
    }

    public static Pair<List<Integer>, Boolean> areItemsValid(List<CountableIngredient> ingredients, List<ItemStack> stacks) {
        Boolean valid = true;
        List<Integer> slotOreintation = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            CountableIngredient ing = ingredients.get(i);
            int slotNum = -1;
            for (int j = 0; j < stacks.size(); j++) {
                if (ing.test(stacks.get(j))) {
                    slotNum = j;
                    break;
                }
            }
            if (slotNum > -1 && !slotOreintation.contains(slotNum)) {
                slotOreintation.add(slotNum);
            }
        }
        if (slotOreintation.size() < ingredients.size()) {
            valid = false;
        }
        return Pair.of(slotOreintation, valid);
    }

    public static Pair<List<Integer>, Boolean> areFluidsValid(List<FluidIngredient> ingredients, FluidTank[] fluidTanks) {
        Boolean valid = true;
        List<Integer> tankOrientation = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            FluidIngredient ing = ingredients.get(i);
            int tankNum = -1;
            for (int j = 0; j < fluidTanks.length; j++) {
                if (ing.testFluid(fluidTanks[j].getFluid())) {
                    tankNum = j;
                    break;
                }
            }
            if (tankNum > -1 && !tankOrientation.contains(tankNum)) {
                tankOrientation.add(tankNum);
            }
        }
        if (tankOrientation.size() < ingredients.size()) {
            valid = false;
        }
        return Pair.of(tankOrientation, valid);
    }

    public abstract boolean matchesRecipe(ComponentProcessor pr, int index);

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}
