package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public abstract class BaseRecipeProvider extends RecipeProvider {

	public final List<AbstractRecipeGenerator> generators = new ArrayList<>();

	public BaseRecipeProvider(PackOutput output) {
		super(output);
		addRecipes();
	}

	public abstract void addRecipes();

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		for (AbstractRecipeGenerator generator : generators) {
			generator.addRecipes(consumer);
		}
	}
}	
