package voltaic.datagen.utils.server.recipe;

import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;

public abstract class AbstractRecipeGenerator {

	public abstract void addRecipes(Consumer<IFinishedRecipe> consumer);

}
