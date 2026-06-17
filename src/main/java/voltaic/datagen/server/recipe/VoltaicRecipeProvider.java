package voltaic.datagen.server.recipe;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class VoltaicRecipeProvider extends BaseRecipeProvider {

    public VoltaicRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
	super(output, lookupProvider);
    }

    @Override
    public void addRecipes() {
	generators.add(new VoltaicCraftingTableRecipes());
    }
}
