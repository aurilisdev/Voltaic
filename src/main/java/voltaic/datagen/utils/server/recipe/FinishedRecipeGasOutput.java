package voltaic.datagen.utils.server.recipe;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.VoltaicRecipeSerializer;
import voltaic.registers.VoltaicRegistries;

public class FinishedRecipeGasOutput extends FinishedRecipeBase {

	private GasStack output;

	private FinishedRecipeGasOutput(RecipeSerializer<?> serializer, GasStack output, double experience, int processTime, double usagePerTick) {
		super(serializer, experience, processTime, usagePerTick);
		this.output = output;
	}

	@Override
	public void writeOutput(JsonObject recipeJson) {
		JsonObject output = new JsonObject();
		output.addProperty("gas", VoltaicRegistries.gasRegistry().getKey(this.output.getGas()).toString());
		output.addProperty("amount", this.output.getAmount());
		output.addProperty("temp", this.output.getTemperature());
		output.addProperty("pressure", this.output.getPressure());
		recipeJson.add(VoltaicRecipeSerializer.OUTPUT, output);
	}

	@Override
	public FinishedRecipeGasOutput name(RecipeCategory category, String parent, String name) {
		return (FinishedRecipeGasOutput) super.name(category, parent, name);
	}

	public static FinishedRecipeGasOutput of(RecipeSerializer<?> serializer, GasStack output, double experience, int processTime, double usage) {
		return new FinishedRecipeGasOutput(serializer, output, experience, processTime, usage);
	}

}
