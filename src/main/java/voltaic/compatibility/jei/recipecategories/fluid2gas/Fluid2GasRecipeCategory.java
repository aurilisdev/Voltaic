package voltaic.compatibility.jei.recipecategories.fluid2gas;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.common.recipe.categories.fluid2gas.Fluid2GasRecipe;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.gui.types.BackgroundObject;
import voltaic.registers.VoltaicCapabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class Fluid2GasRecipeCategory<T extends Fluid2GasRecipe> extends AbstractRecipeCategory<T> {

    public Fluid2GasRecipeCategory(IGuiHelper guiHelper, Component title, ItemStack inputMachine, BackgroundObject wrapper, RecipeType<T> recipeType, int animationTime) {
        super(guiHelper, title, inputMachine, wrapper, recipeType, animationTime);
    }

    @Override
    public List<List<FluidStack>> getFluidInputs(Fluid2GasRecipe recipe) {
        List<List<FluidStack>> ingredients = new ArrayList<>();
        for (FluidIngredient ing : recipe.getFluidIngredients()) {
            List<FluidStack> fluids = new ArrayList<>();
            for (FluidStack stack : ing.getMatchingFluids()) {
                if (!BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString().toLowerCase(Locale.ROOT).contains("flow")) {
                    fluids.add(stack);
                }
            }
            ingredients.add(fluids);
        }
        return ingredients;
    }

    @Override
    public List<List<ItemStack>> getItemInputs(Fluid2GasRecipe recipe) {
        List<List<ItemStack>> ingredients = new ArrayList<>();

        for (FluidIngredient ing : recipe.getFluidIngredients()) {
            List<ItemStack> buckets = new ArrayList<>();
            for (FluidStack stack : ing.getMatchingFluids()) {
                ItemStack bucket = new ItemStack(stack.getFluid().getBucket(), 1);

                IFluidHandlerItem handler = bucket.getCapability(Capabilities.FluidHandler.ITEM);

                if (handler != null) {

                    handler.fill(stack, FluidAction.EXECUTE);

                    bucket = handler.getContainer();

                }
                buckets.add(bucket);
            }
            ingredients.add(buckets);
        }

        return ingredients;
    }

    @Override
    public List<ItemStack> getItemOutputs(Fluid2GasRecipe recipe) {
        List<ItemStack> outputItems = new ArrayList<>();

        ItemStack bucket = new ItemStack(recipe.getGasRecipeOutput().getGas().getContainer(), 1);

        IGasHandlerItem outputHandler = bucket.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM);

        outputHandler.fill(recipe.getGasRecipeOutput(), GasAction.EXECUTE);

        if (outputHandler != null) {

            outputHandler.fill(recipe.getGasRecipeOutput(), GasAction.EXECUTE);

            bucket = outputHandler.getContainer();

            outputItems.add(bucket);
        }

        outputItems.add(bucket);

        if (recipe.hasGasBiproducts()) {
            for (ProbableGas gas : recipe.getGasBiproducts()) {
                ItemStack temp = new ItemStack(gas.getFullStack().getGas().getContainer());

                IGasHandlerItem handler = temp.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM);

                if (handler != null) {

                    handler.fill(gas.getFullStack(), GasAction.EXECUTE);

                    temp = handler.getContainer();

                    outputItems.add(temp);

                }
            }
        }

        return outputItems;
    }

    @Override
    public List<GasStack> getGasOutputs(Fluid2GasRecipe recipe) {
        List<GasStack> outputs = new ArrayList<>();
        outputs.add(recipe.getGasRecipeOutput());
        if (recipe.hasGasBiproducts()) {
            outputs.addAll(Arrays.asList(recipe.getFullGasBiStacks()));
        }
        return outputs;
    }

}
