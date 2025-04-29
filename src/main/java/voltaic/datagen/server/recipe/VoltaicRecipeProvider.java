package voltaic.datagen.server.recipe;

import net.minecraft.data.DataGenerator;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class VoltaicRecipeProvider extends BaseRecipeProvider {

    public VoltaicRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void addRecipes() {
        generators.add(new VoltaicCraftingTableRecipes());
    }
}
