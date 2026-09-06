package voltaic.compatibility.jei.screenhandlers.clickableingredients;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import voltaic.api.gas.GasStack;
import voltaic.compatibility.jei.utils.ingredients.VoltaicJeiTypes;

public class ClickableGasIngredient extends AbstractClickableIngredient<GasStack> {

    private final GasIngredientType typedIngredient;

    public ClickableGasIngredient(Rect2i rect, GasStack gasStack) {
	super(rect);
	typedIngredient = new GasIngredientType(gasStack);
    }

    @Override
    public IIngredientType<GasStack> getIngredientType() {
	return VoltaicJeiTypes.GAS_STACK;
    }

    @Override
    public GasStack getIngredient() {
	return typedIngredient.gasStack();
    }

    @Override
    @Deprecated
    public ITypedIngredient<GasStack> getTypedIngredient() {
	return typedIngredient;
    }

    private record GasIngredientType(GasStack gasStack) implements ITypedIngredient<GasStack> {

	@Override
	public IIngredientType<GasStack> getType() {
	    return VoltaicJeiTypes.GAS_STACK;
	}

	@Override
	public GasStack getIngredient() {
	    return gasStack;
	}

    }

}