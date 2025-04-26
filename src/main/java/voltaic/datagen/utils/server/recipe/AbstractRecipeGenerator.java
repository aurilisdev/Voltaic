package voltaic.datagen.utils.server.recipe;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;

public abstract class AbstractRecipeGenerator {

	public abstract void addRecipes(Consumer<FinishedRecipe> consumer);

}
