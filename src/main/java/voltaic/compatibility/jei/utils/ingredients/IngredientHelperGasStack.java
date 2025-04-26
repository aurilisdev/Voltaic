package voltaic.compatibility.jei.utils.ingredients;

import org.jetbrains.annotations.Nullable;

import voltaic.api.gas.GasStack;
import voltaic.registers.VoltaicRegistries;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;

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
	public String getUniqueId(GasStack ingredient, UidContext context) {
		return ingredient.getGas().getDescription().getString();
	}

	@Override
	public ResourceLocation getResourceLocation(GasStack ingredient) {
		return VoltaicRegistries.gasRegistry().getKey(ingredient.getGas());
	}

	@Override
	public GasStack copyIngredient(GasStack ingredient) {
		return ingredient.copy();
	}

	@Override
	public String getErrorInfo(@Nullable GasStack ingredient) {
		return ingredient == null ? "null" : ingredient.toString();
	}

}
