package voltaic.datagen.server.recipe;

import net.minecraft.data.PackOutput;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class VoltaicRecipeProvider extends BaseRecipeProvider {

    public VoltaicRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void addRecipes() {
        generators.add(new VoltaicCraftingTableRecipes());
    }
}
