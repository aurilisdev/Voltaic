package voltaic.compatibility.jei.recipecategories.fluiditem2gas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.categories.fluiditem2gas.FluidItem2GasRecipe;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.gui.types.BackgroundObject;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public abstract class FluidItem2GasRecipeCategory<T extends FluidItem2GasRecipe> extends AbstractRecipeCategory<T> {

    public FluidItem2GasRecipeCategory(IGuiHelper guiHelper, Component title, ItemStack inputMachine, BackgroundObject wrapper, RecipeType<T> recipeType, int animationTime) {
        super(guiHelper, title, inputMachine, wrapper, recipeType, animationTime);
    }

    @Override
    public List<List<ItemStack>> getItemInputs(T recipe) {
        List<List<ItemStack>> ingredients = new ArrayList<>();

        for (CountableIngredient ing : recipe.getCountedIngredients()) {
            ingredients.add(Arrays.asList(ing.getItemsArray()));
        }

        for (FluidIngredient ing : recipe.getFluidIngredients()) {
            List<ItemStack> buckets = new ArrayList<>();
            for (FluidStack stack : ing.getMatchingFluids()) {
                ItemStack bucket = new ItemStack(stack.getFluid().getBucket(), 1);
                IFluidHandlerItem handler = bucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

                if (handler != CapabilityUtils.EMPTY_FLUID_ITEM) {

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
    public List<ItemStack> getItemOutputs(T recipe) {
        List<ItemStack> outputItems = new ArrayList<>();

        if (recipe.hasItemBiproducts()) {
            outputItems.addAll(Arrays.asList(recipe.getFullItemBiStacks()));
        }

        ItemStack bucket = new ItemStack(recipe.getGasRecipeOutput().getGas().getContainer(), 1);

        IGasHandlerItem outputHandler = bucket.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM).orElse(CapabilityUtils.EMPTY_GAS_ITEM);

        if (outputHandler != CapabilityUtils.EMPTY_GAS_ITEM) {

            outputHandler.fill(recipe.getGasRecipeOutput(), GasAction.EXECUTE);

            bucket = outputHandler.getContainer();

            outputItems.add(bucket);
        }

        if (recipe.hasFluidBiproducts()) {
            for (ProbableFluid stack : recipe.getFluidBiproducts()) {
                ItemStack temp = new ItemStack(stack.getFullStack().getFluid().getBucket(), 1);
                IFluidHandlerItem handler = temp.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

                if (handler != CapabilityUtils.EMPTY_FLUID_ITEM) {

                    handler.fill(stack.getFullStack(), FluidAction.EXECUTE);

                    temp = handler.getContainer();

                }
                outputItems.add(temp);
            }
        }
        return outputItems;
    }

    @Override
    public List<List<FluidStack>> getFluidInputs(T recipe) {
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
    public List<GasStack> getGasOutputs(T recipe) {
        List<GasStack> outputFluids = new ArrayList<>();
        outputFluids.add(recipe.getGasRecipeOutput());
        if (recipe.hasGasBiproducts()) {
            outputFluids.addAll(Arrays.asList(recipe.getFullGasBiStacks()));
        }
        return outputFluids;
    }

}
