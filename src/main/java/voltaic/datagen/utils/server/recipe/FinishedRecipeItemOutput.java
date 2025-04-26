package voltaic.datagen.utils.server.recipe;

import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.common.recipe.VoltaicRecipeSerializer;

public class FinishedRecipeItemOutput extends FinishedRecipeBase {

	private ItemStack output;

	private FinishedRecipeItemOutput(RecipeSerializer<?> serializer, ItemStack stack, double experience, int processTime, double usage) {
		super(serializer, experience, processTime, usage);
		output = stack;
	}

	@Override
	public void writeOutput(JsonObject recipeJson) {
		JsonObject output = new JsonObject();
		output.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.getItem()).toString());
		output.addProperty(VoltaicRecipeSerializer.COUNT, this.output.getCount());
		recipeJson.add(VoltaicRecipeSerializer.OUTPUT, output);
	}

	@Override
	public FinishedRecipeItemOutput name(RecipeCategory category, String parent, String name) {
		return (FinishedRecipeItemOutput) super.name(category, parent, name);
	}

	public static FinishedRecipeItemOutput of(RecipeSerializer<?> serializer, ItemStack output, double experience, int processTime, double usage) {
		return new FinishedRecipeItemOutput(serializer, output, experience, processTime, usage);
	}

}
