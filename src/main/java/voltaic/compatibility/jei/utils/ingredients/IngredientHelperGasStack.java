package voltaic.compatibility.jei.utils.ingredients;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import voltaic.api.gas.GasStack;
import voltaic.registers.VoltaicGases;

public class IngredientHelperGasStack implements IIngredientHelper<GasStack> {

    @Override
    public IIngredientType<GasStack> getIngredientType() {
	return VoltaicJeiTypes.GAS_STACK;
    }

    @Override
    public String getDisplayName(GasStack ingredient) {
	return ingredient.getGas().getDescription().getString();
    }

    @Override
    @Deprecated
    public String getUniqueId(GasStack ingredient, UidContext context) {
	return getGasId(ingredient).toString();
    }

    @Override
    public Object getUid(GasStack ingredient, UidContext context) {
	return getGasId(ingredient);
    }

    @Override
    public ResourceLocation getResourceLocation(GasStack ingredient) {
	return getGasId(ingredient);
    }

    @Override
    public GasStack copyIngredient(GasStack ingredient) {
	return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable GasStack ingredient) {
	return ingredient == null ? "null" : ingredient.toString();
    }

    private static ResourceLocation getGasId(GasStack ingredient) {
	ResourceLocation id = VoltaicGases.GAS_REGISTRY.getKey(ingredient.getGas());
	if (id == null)
	    throw new IllegalArgumentException("Gas is not registered: " + ingredient.getGas());
	return id;
    }

}