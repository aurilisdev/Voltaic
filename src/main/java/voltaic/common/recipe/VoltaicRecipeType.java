package voltaic.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class VoltaicRecipeType<T extends Recipe<?>> implements RecipeType<T> {
    private final ResourceLocation id;

    public VoltaicRecipeType(ResourceLocation id) {
	this.id = id;
    }

    @Override
    public String toString() {
	return id.toString();
    }
}