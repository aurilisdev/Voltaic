package voltaic.datagen.utils.server.recipe;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomCookingRecipe implements IFinishedRecipe {

	private final ResourceLocation id;
	private final Ingredient ingredient;
	private final Item result;
	private final float experience;
	private final int cookingTime;
	private final IRecipeSerializer<? extends AbstractCookingRecipe> serializer;

	private CustomCookingRecipe(ResourceLocation id, Ingredient input, Item result, float experience, int cookingTime, IRecipeSerializer<? extends AbstractCookingRecipe> serializer) {
		this.id = id;
		ingredient = input;
		this.result = result;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.serializer = serializer;
	}

	@Override
	@Nullable
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Override
	public void serializeRecipeData(JsonObject pJson) {
		pJson.add("ingredient", this.ingredient.toJson());
		pJson.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
		pJson.addProperty("experience", this.experience);
		pJson.addProperty("cookingtime", this.cookingTime);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getType() {
		return serializer;
	}

	@Override
	@Nullable
	public ResourceLocation getAdvancementId() {
		return null;
	}

	public static SmeltingBuilder smeltingRecipe(Item result, float experience, int smeltTime) {
		return new SmeltingBuilder(result, experience, smeltTime);
	}

	public static SmokingBuilder smokingRecipe(Item result, float experience, int smeltTime) {
		return new SmokingBuilder(result, experience, smeltTime);
	}

	public static BlastingBuilder blastingRecipe(Item result, float experience, int smeltTime) {
		return new BlastingBuilder(result, experience, smeltTime);
	}

	public static class Builder {

		private final Item result;
		private final float experience;
		private final int smeltTime;
		private final IRecipeSerializer<? extends AbstractCookingRecipe> serializer;
		private Ingredient input;

		private Builder(Item result, float experience, int smeltTime, IRecipeSerializer<? extends AbstractCookingRecipe> serializer) {
			this.result = result;
			this.experience = experience;
			this.smeltTime = smeltTime;
			this.serializer = serializer;
		}

		public Builder input(Item item) {
			return input(new ItemStack(item));
		}

		public Builder input(ItemStack item) {
			return input(Ingredient.of(item));
		}

		public Builder input(INamedTag<Item> tag) {
			return input(Ingredient.of(tag));
		}

		public Builder input(Ingredient item) {
			input = item;
			return this;
		}

		public void complete(String parent, String name, Consumer<IFinishedRecipe> consumer) {
			consumer.accept(new CustomCookingRecipe(new ResourceLocation(parent, name), input, result, experience, smeltTime, serializer));
		}

	}

	public static class SmeltingBuilder extends Builder {

		private SmeltingBuilder(Item result, float experience, int smeltTime) {
			super(result, experience, smeltTime, IRecipeSerializer.SMELTING_RECIPE);
		}

	}

	public static class SmokingBuilder extends Builder {

		private SmokingBuilder(Item result, float experience, int smeltTime) {
			super(result, experience, smeltTime, IRecipeSerializer.SMOKING_RECIPE);
		}

	}

	public static class BlastingBuilder extends Builder {

		private BlastingBuilder(Item result, float experience, int smeltTime) {
			super(result, experience, smeltTime, IRecipeSerializer.BLASTING_RECIPE);
		}

	}

}
