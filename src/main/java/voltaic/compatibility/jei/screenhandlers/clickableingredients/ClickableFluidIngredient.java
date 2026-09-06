package voltaic.compatibility.jei.screenhandlers.clickableingredients;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.neoforge.fluids.FluidStack;

public class ClickableFluidIngredient extends AbstractClickableIngredient<FluidStack> {

    private final FluidTypeIngredient typedIngredient;

    public ClickableFluidIngredient(Rect2i rect, FluidStack fluidStack) {
	super(rect);
	typedIngredient = new FluidTypeIngredient(fluidStack);
    }

    @Override
    public IIngredientType<FluidStack> getIngredientType() {
	return NeoForgeTypes.FLUID_STACK;
    }

    @Override
    public FluidStack getIngredient() {
	return typedIngredient.fluidStack();
    }

    @Override
    @Deprecated
    public ITypedIngredient<FluidStack> getTypedIngredient() {
	return typedIngredient;
    }

    private record FluidTypeIngredient(FluidStack fluidStack) implements ITypedIngredient<FluidStack> {

	@Override
	public IIngredientType<FluidStack> getType() {
	    return NeoForgeTypes.FLUID_STACK;
	}

	@Override
	public FluidStack getIngredient() {
	    return fluidStack;
	}

    }

}