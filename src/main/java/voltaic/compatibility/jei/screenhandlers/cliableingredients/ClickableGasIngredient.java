package voltaic.compatibility.jei.screenhandlers.cliableingredients;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import voltaic.api.gas.GasStack;
import voltaic.compatibility.jei.utils.ingredients.VoltaicJeiTypes;

public class ClickableGasIngredient extends AbstractClickableIngredient<GasStack> {

    private final GasIngredientType typeIngredient;

    public ClickableGasIngredient(Rect2i rect, GasStack gasStack) {
        super(rect);
        typeIngredient = new GasIngredientType(gasStack);
    }

    @Override
    public ITypedIngredient<GasStack> getTypedIngredient() {
        return typeIngredient;
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
