package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public abstract class BaseRecipeProvider extends RecipeProvider {

    public final List<AbstractRecipeGenerator> generators = new ArrayList<>();
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;


    public BaseRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
        this.lookupProvider = lookupProvider;
        addRecipes();
    }

    public abstract void addRecipes();

    @Override
    protected void buildRecipes(RecipeOutput output) {

        for (AbstractRecipeGenerator generator : generators) {
            generator.addRecipes(output);
        }
    }

}
