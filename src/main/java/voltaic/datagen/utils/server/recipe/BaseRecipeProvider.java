package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;

public abstract class BaseRecipeProvider extends RecipeProvider {

	public final List<AbstractRecipeGenerator> generators = new ArrayList<>();

	public BaseRecipeProvider(DataGenerator generator) {
		super(generator);
		addRecipes();
	}

	public abstract void addRecipes();

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		for (AbstractRecipeGenerator generator : generators) {
			generator.addRecipes(consumer);
		}
	}
}	
