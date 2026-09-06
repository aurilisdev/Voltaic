package voltaic.datagen.utils.server.recipe;

import javax.annotation.Nullable;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public class ShapelessCraftingRecipeBuilder implements RecipeBuilder {

    private final Item item;
    private final int count;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private ICondition[] recipeConditions = {};
    private String group = "";

    private @Nullable ResourceLocation id;

    private ShapelessCraftingRecipeBuilder(Item item, int count) {
	this.item = item;
	this.count = count;
    }

    public static ShapelessCraftingRecipeBuilder start(Item item, int count) {
	return new ShapelessCraftingRecipeBuilder(item, count);
    }

    public ShapelessCraftingRecipeBuilder addIngredient(Ingredient ing) {
	ingredients.add(ing);
	return this;
    }

    public ShapelessCraftingRecipeBuilder addIngredient(ICustomIngredient ing) {
	ingredients.add(new Ingredient(ing));
	return this;
    }

    public ShapelessCraftingRecipeBuilder addIngredient(String parent, String tag) {
	ingredients.add(Ingredient.of(itemTag(ResourceLocation.fromNamespaceAndPath(parent, tag))));
	return this;
    }

    public ShapelessCraftingRecipeBuilder addIngredient(TagKey<Item> tag) {
	ingredients.add(Ingredient.of(tag));
	return this;
    }

    public ShapelessCraftingRecipeBuilder addIngredient(Item item) {
	return addIngredient(new ItemStack(item));
    }

    public ShapelessCraftingRecipeBuilder addIngredient(ItemStack item) {
	ingredients.add(Ingredient.of(item));
	return this;
    }

    public ShapelessCraftingRecipeBuilder addConditions(ICondition... conditions) {
	recipeConditions = conditions;
	return this;
    }

    public ShapelessCraftingRecipeBuilder complete(String parent, String name, RecipeOutput output) {
	id = ResourceLocation.fromNamespaceAndPath(parent, name);
	save(output);
	return this;
    }

    private static TagKey<Item> itemTag(ResourceLocation tag) {
	return TagKey.create(Registries.ITEM, tag);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
	return this;
    }

    @Override
    public ShapelessCraftingRecipeBuilder group(@Nullable String group) {
	this.group = group == null ? "" : group;
	return this;
    }

    @Override
    public Item getResult() {
	return item;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
	if (recipeConditions.length > 0)
	    output = output.withConditions(recipeConditions);

	output.accept(id,
		new ShapelessRecipe(group, CraftingBookCategory.MISC, new ItemStack(item, count), ingredients), null);

    }

    @Override
    public void save(RecipeOutput output) {
	ResourceLocation recipeId = id;
	if (recipeId == null)
	    throw new IllegalStateException("Recipe ID has not been set");

	save(output, recipeId);
    }

    @Override
    public void save(RecipeOutput output, String name) {
	save(output);
    }

}
