package voltaic.common.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class VoltaicRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    @Override
    public String toString() {
	return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
    }

}
