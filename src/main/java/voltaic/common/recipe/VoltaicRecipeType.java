package voltaic.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;

public class VoltaicRecipeType<T extends IRecipe<?>> implements IRecipeType<T> {

    @Override
    public String toString() {
        return Registry.RECIPE_TYPE.getKey(this).toString();
    }

}
