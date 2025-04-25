package voltaic.compatibility.jei.recipecategories.fluid2fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.categories.fluid2fluid.Fluid2FluidRecipe;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.gui.types.BackgroundObject;
import voltaic.prefab.utilities.CapabilityUtils;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public abstract class Fluid2FluidRecipeCategory<T extends VoltaicRecipe> extends AbstractRecipeCategory<T> {

    public Fluid2FluidRecipeCategory(IGuiHelper guiHelper, Component title, ItemStack inputMachine, BackgroundObject wrapper, RecipeType<T> recipeType, int animationTime) {
        super(guiHelper, title, inputMachine, wrapper, recipeType, animationTime);
    }

    @Override
    public List<List<FluidStack>> getFluidInputs(VoltaicRecipe electro) {
        Fluid2FluidRecipe recipe = (Fluid2FluidRecipe) electro;
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
    public List<List<ItemStack>> getItemInputs(VoltaicRecipe electro) {
        Fluid2FluidRecipe recipe = (Fluid2FluidRecipe) electro;
        List<List<ItemStack>> ingredients = new ArrayList<>();

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
    public List<ItemStack> getItemOutputs(VoltaicRecipe electro) {
        Fluid2FluidRecipe recipe = (Fluid2FluidRecipe) electro;
        List<ItemStack> outputItems = new ArrayList<>();

        ItemStack bucket = new ItemStack(recipe.getFluidRecipeOutput().getFluid().getBucket(), 1);

        IFluidHandlerItem handler = bucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

        if (handler != CapabilityUtils.EMPTY_FLUID_ITEM) {

            handler.fill(recipe.getFluidRecipeOutput(), FluidAction.EXECUTE);

            bucket = handler.getContainer();

        }
        outputItems.add(bucket);

        if (recipe.hasFluidBiproducts()) {
            for (ProbableFluid stack : recipe.getFluidBiproducts()) {
                ItemStack temp = new ItemStack(stack.getFullStack().getFluid().getBucket(), 1);

                handler = temp.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

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
    public List<FluidStack> getFluidOutputs(VoltaicRecipe electro) {
        Fluid2FluidRecipe recipe = (Fluid2FluidRecipe) electro;
        List<FluidStack> outputFluids = new ArrayList<>();
        outputFluids.add(recipe.getFluidRecipeOutput());
        if (recipe.hasFluidBiproducts()) {
            outputFluids.addAll(Arrays.asList(recipe.getFullFluidBiStacks()));
        }
        return outputFluids;
    }

}
