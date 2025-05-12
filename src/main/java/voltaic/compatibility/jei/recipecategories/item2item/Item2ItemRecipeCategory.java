package voltaic.compatibility.jei.recipecategories.item2item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import voltaic.common.recipe.categories.item2item.Item2ItemRecipe;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.RecipeType;
import voltaic.compatibility.jei.utils.gui.types.BackgroundObject;
import voltaic.prefab.utilities.CapabilityUtils;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public abstract class Item2ItemRecipeCategory<T extends Item2ItemRecipe> extends AbstractRecipeCategory<T> {

    /*
     * DOCUMENTATION NOTES:
     * 
     * > Output items supercede buckets in position > All biproducts will be included with the outputSlots field > All fluid
     * bucket output slots will be incled with the outputSlots field
     */

    public Item2ItemRecipeCategory(IGuiHelper guiHelper, ITextComponent title, ItemStack inputMachine, BackgroundObject bWrap, RecipeType<T> recipeType, int animTime) {
        super(guiHelper, title, inputMachine, bWrap, recipeType, animTime);
    }

    @Override
    public List<List<ItemStack>> getItemInputs(Item2ItemRecipe recipe) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        recipe.getCountedIngredients().forEach(h -> inputs.add(Arrays.asList(h.getItems())));
        return inputs;
    }

    @Override
    public List<ItemStack> getItemOutputs(Item2ItemRecipe recipe) {
        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(recipe.getItemRecipeOutput());

        if (recipe.hasItemBiproducts()) {
            outputs.addAll(Arrays.asList(recipe.getFullItemBiStacks()));
        }

        if (recipe.hasFluidBiproducts()) {
            for (ProbableFluid fluid : recipe.getFluidBiproducts()) {
                ItemStack canister = new ItemStack(fluid.getFullStack().getFluid().getBucket(), 1);
                IFluidHandlerItem handler = canister.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

                if (handler != CapabilityUtils.EMPTY_FLUID_ITEM) {

                    handler.fill(fluid.getFullStack(), FluidAction.EXECUTE);

                    canister = handler.getContainer();

                }
                outputs.add(canister);
            }
        }
        return outputs;
    }

}
