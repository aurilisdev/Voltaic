package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CustomShapelessCraftingRecipe extends ShapelessRecipeBuilder.Result {

	@Nullable
	private ICondition[] recipeConditions;

	private CustomShapelessCraftingRecipe(ResourceLocation recipeId, Item result, int count, List<Ingredient> ingredients, ICondition[] recipeConditions) {
		super(recipeId, result, count, "", ingredients, null, null);
		this.recipeConditions = recipeConditions;
	}

	public static Builder start(Item item, int count) {
		return new Builder(item, count);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		super.serializeRecipeData(json);

		if (recipeConditions == null || recipeConditions.length == 0) {
			return;
		}

		JsonArray conditions = new JsonArray();

		for (ICondition condition : recipeConditions) {
			conditions.add(CraftingHelper.serialize(condition));
		}

		json.add("conditions", conditions);

	}

	@Override
	public JsonObject serializeAdvancement() {
		return null;
	}

	public static class Builder {

		private Item item;
		private int count;
		private List<Ingredient> ingredients = new ArrayList<>();
		@Nullable
		private ICondition[] recipeConditions;

		private Builder(Item item, int count) {
			this.item = item;
			this.count = count;
		}

		public Builder addIngredient(Ingredient ing) {
			ingredients.add(ing);
			return this;
		}

		public Builder addIngredient(String parent, String tag) {
			ingredients.add(Ingredient.of(itemTag(new ResourceLocation(parent, tag))));
			return this;
		}

		public Builder addIngredient(INamedTag<Item> tag) {
			ingredients.add(Ingredient.of(tag));
			return this;
		}

		public Builder addIngredient(Item item) {
			return addIngredient(new ItemStack(item));
		}

		public Builder addIngredient(ItemStack item) {
			ingredients.add(Ingredient.of(item));
			return this;
		}

		public Builder addConditions(ICondition... conditions) {
			recipeConditions = conditions;
			return this;
		}

		public void complete(String parent, String name, Consumer<IFinishedRecipe> consumer) {
			consumer.accept(new CustomShapelessCraftingRecipe(new ResourceLocation(parent, name), item, count, ingredients, recipeConditions));
		}

		private INamedTag<Item> itemTag(ResourceLocation tag) {
			return ItemTags.createOptional(tag);
		}

	}

}
