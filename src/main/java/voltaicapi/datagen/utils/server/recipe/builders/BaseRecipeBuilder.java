package voltaicapi.datagen.utils.server.recipe.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import voltaicapi.common.recipe.ModularElectricityRecipe;
import voltaicapi.common.recipe.recipeutils.ProbableFluid;
import voltaicapi.common.recipe.recipeutils.ProbableGas;
import voltaicapi.common.recipe.recipeutils.ProbableItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public abstract class BaseRecipeBuilder<T extends ModularElectricityRecipe, A extends BaseRecipeBuilder<?, ?>> implements RecipeBuilder {

    public final ResourceLocation id;

    public final String group;

    public final double experience;
    public final int processTime;
    public final double usagePerTick;

    @Nullable
    public ICondition[] conditions;

    public final List<ProbableItem> itemBiproducts = new ArrayList<>();
    public final List<ProbableFluid> fluidBiproducts = new ArrayList<>();
    public final List<ProbableGas> gasBiproducts = new ArrayList<>();

    public BaseRecipeBuilder(RecipeCategory category, String parent, String name, String group, double experience, int processTime, double usagePerTick) {
        this.id = ResourceLocation.fromNamespaceAndPath(parent, category.category() + "/" + name);
        this.group = group;
        this.experience = experience;
        this.processTime = processTime;
        this.usagePerTick = usagePerTick;
    }

    public A addItemBiproduct(ProbableItem biproudct) {
        itemBiproducts.add(biproudct);
        return (A) this;
    }

    public A addFluidBiproduct(ProbableFluid biproduct) {
        fluidBiproducts.add(biproduct);
        return (A) this;
    }

    public A addGasBiproduct(ProbableGas biproduct) {
        gasBiproducts.add(biproduct);
        return (A) this;
    }

    public A conditions(ICondition... conditions) {
        this.conditions = conditions;
        return (A) this;
    }

    public abstract T makeRecipe();

    @Override
    public void save(RecipeOutput output, ResourceLocation altName) {
        if (conditions != null) {
            output.withConditions(conditions).accept(id, makeRecipe(), null);
        } else {
            output.accept(id, makeRecipe(), null);
        }

    }

    @Override
    public void save(RecipeOutput output) {
        this.save(output, id);
    }

    @Override
    public void save(RecipeOutput output, String group) {
        this.save(output, id);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(String pGroupName) {
        return this;
    }

    public enum RecipeCategory {
        ITEM_2_ITEM,
        ITEM_2_FLUID,
        FLUID_ITEM_2_ITEM,
        FLUID_ITEM_2_FLUID,
        FLUID_2_ITEM,
        FLUID_2_FLUID,
        FLUID_2_GAS,
        FLUID_ITEM_2_GAS,
        CHEMICAL_REACTOR;

        public String category() {
            return toString().toLowerCase(Locale.ROOT).replaceAll("_", "");
        }
    }

    public static record GasIngWrapper(int amt, int temp, int pressure) {

    }

}
