package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CustomShapedCraftingRecipe extends ShapedRecipeBuilder.Result {

	@Nullable
	private ICondition[] recipeConditions;

	private CustomShapedCraftingRecipe(ResourceLocation recipeId, Item result, int count, List<String> pattern, Map<Character, Ingredient> keys, ICondition[] recipeConditions) {
		super(recipeId, result, count, "", CraftingBookCategory.MISC, pattern, keys, null, null, false);
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
		private List<String> patterns = new ArrayList<>();
		private Map<Character, Ingredient> keys = new HashMap<>();
		@Nullable
		private ICondition[] recipeConditions;

		private Builder(Item item, int count) {
			this.item = item;
			this.count = count;
		}

		public Builder addPattern(String pattern) {
			if (pattern.length() > 3) {
				throw new UnsupportedOperationException("The pattern " + pattern + " is more than 3 characters long and is not valid!");
			}
			if (patterns.size() > 3) {
				throw new UnsupportedOperationException("Already 3 patterns present");
			}
			patterns.add(pattern);
			return this;
		}

		public Builder addKey(Character key, Ingredient ing) {
			keys.put(key, ing);
			return this;
		}

		public Builder addKey(Character key, TagKey<Item> ing) {
			keys.put(key, Ingredient.of(ing));
			return this;
		}

		public Builder addKey(Character key, String parent, String tag) {
			keys.put(key, Ingredient.of(itemTag(new ResourceLocation(parent, tag))));
			return this;
		}

		public Builder addKey(Character key, Item item) {
			return addKey(key, new ItemStack(item));
		}

		public Builder addKey(Character key, ItemStack item) {
			keys.put(key, Ingredient.of(item));
			return this;
		}

		public Builder addConditions(ICondition... conditions) {
			recipeConditions = conditions;
			return this;
		}

		public void complete(String parent, String name, Consumer<FinishedRecipe> consumer) {
			for (Character character : keys.keySet()) {
				if (isKeyNotUsed(character)) {
					throw new UnsupportedOperationException("The key " + character + " is defined by never used!");
				}
			}
			consumer.accept(new CustomShapedCraftingRecipe(new ResourceLocation(parent, name), item, count, patterns, keys, recipeConditions));
		}

		private boolean isKeyNotUsed(char character) {
			for (String str : patterns) {
				for (char ch : str.toCharArray()) {
					if (ch == character) {
						return false;
					}
				}
			}
			return true;

		}

		private TagKey<Item> itemTag(ResourceLocation tag) {
			return TagKey.create(Registries.ITEM, tag);
		}

	}

}
