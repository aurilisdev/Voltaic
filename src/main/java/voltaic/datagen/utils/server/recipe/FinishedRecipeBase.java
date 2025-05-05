package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import voltaic.common.recipe.VoltaicRecipeSerializer;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;

public abstract class FinishedRecipeBase implements IFinishedRecipe {

	private IRecipeSerializer<?> serializer;
	private ResourceLocation id;

	private List<ProbableItem> itemBiproducts = new ArrayList<>();
	private List<ProbableFluid> fluidBiproducts = new ArrayList<>();

	private List<ItemStack> itemIngredients = new ArrayList<>();
	private List<Pair<INamedTag<Item>, Integer>> tagItemIngredients = new ArrayList<>();
	private List<FluidStack> fluidIngredients = new ArrayList<>();
	private List<Pair<INamedTag<Fluid>, Integer>> tagFluidIngredients = new ArrayList<>();

	private double experience = 0.0;
	private int processTime = 0;
	private double usagePerTick = 0.0;

	protected FinishedRecipeBase(IRecipeSerializer<?> serializer, double experience, int processTime, double usagePerTick) {
		this.serializer = serializer;
		this.experience = experience;
		this.processTime = processTime;
		this.usagePerTick = usagePerTick;
	}

	public FinishedRecipeBase name(RecipeCategory category, String parent, String name) {
		id = new ResourceLocation(parent, category.category() + "/" + name);
		return this;
	}

	public FinishedRecipeBase addItemStackInput(ItemStack stack) {
		itemIngredients.add(stack);
		return this;
	}

	public FinishedRecipeBase addItemTagInput(INamedTag<Item> tag, int count) {
		tagItemIngredients.add(Pair.of(tag, count));
		return this;
	}

	public FinishedRecipeBase addFluidStackInput(FluidStack stack) {
		fluidIngredients.add(stack);
		return this;
	}

	public FinishedRecipeBase addFluidTagInput(INamedTag<Fluid> tag, int count) {
		tagFluidIngredients.add(Pair.of(tag, count));
		return this;
	}

	public FinishedRecipeBase addItemBiproduct(ProbableItem biproudct) {
		itemBiproducts.add(biproudct);
		return this;
	}

	public FinishedRecipeBase addFluidBiproduct(ProbableFluid biproduct) {
		fluidBiproducts.add(biproduct);
		return this;
	}
	
	public void complete(Consumer<IFinishedRecipe> consumer) {
		consumer.accept(this);
	}

	@Override
	public void serializeRecipeData(JsonObject recipeJson) {
		boolean inputsFlag = false;

		recipeJson.addProperty(VoltaicRecipeSerializer.TICKS, processTime);
		recipeJson.addProperty(VoltaicRecipeSerializer.USAGE_PER_TICK, usagePerTick);

		int itemInputsCount = itemIngredients.size() + tagItemIngredients.size();
		if (itemInputsCount > 0) {
			inputsFlag = true;
			JsonObject itemInputs = new JsonObject();
			itemInputs.addProperty(VoltaicRecipeSerializer.COUNT, itemInputsCount);
			JsonElement itemJson;
			CountableIngredient ing;
			int index = 0;
			for (ItemStack stack : itemIngredients) {
				ing = new CountableIngredient(stack);
				itemJson = CountableIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				itemInputs.add(index + "", itemJson);
				index++;
			}
			for (Pair<INamedTag<Item>, Integer> itemTags : tagItemIngredients) {
				ing = new CountableIngredient(itemTags.getFirst(), itemTags.getSecond());
				itemJson = CountableIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				itemInputs.add(index + "", itemJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.ITEM_INPUTS, itemInputs);
		}

		int fluidInputsCount = fluidIngredients.size() + tagFluidIngredients.size();
		if (fluidInputsCount > 0) {
			inputsFlag = true;
			JsonObject fluidInputs = new JsonObject();
			fluidInputs.addProperty(VoltaicRecipeSerializer.COUNT, fluidInputsCount);
			JsonElement fluidJson;
			FluidIngredient ing;
			int index = 0;
			for (FluidStack stack : fluidIngredients) {
				ing = new FluidIngredient(stack);
				fluidJson = FluidIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			for (Pair<INamedTag<Fluid>, Integer> fluidTags : tagFluidIngredients) {
				ing = new FluidIngredient(fluidTags.getFirst(), fluidTags.getSecond());
				fluidJson = FluidIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.FLUID_INPUTS, fluidInputs);
		}

		if (!inputsFlag) {
			throw new RuntimeException("You must specify at least one item, fluid, or gas input");
		}

		writeOutput(recipeJson);

		recipeJson.addProperty(VoltaicRecipeSerializer.EXPERIENCE, experience);

		if (itemBiproducts.size() > 0) {
			JsonObject itemBiproducts = new JsonObject();
			itemBiproducts.addProperty(VoltaicRecipeSerializer.COUNT, this.itemBiproducts.size());
			JsonElement itemJson;
			int index = 0;
			for (ProbableItem biproduct : this.itemBiproducts) {
				itemJson = ProbableItem.CODEC.encodeStart(JsonOps.INSTANCE, biproduct).result().get();
				itemBiproducts.add(index + "", itemJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.ITEM_BIPRODUCTS, itemBiproducts);
		}

		if (fluidBiproducts.size() > 0) {
			JsonObject fluidBiproducts = new JsonObject();
			fluidBiproducts.addProperty(VoltaicRecipeSerializer.COUNT, this.fluidBiproducts.size());
			JsonElement fluidJson;
			int index = 0;
			for (ProbableFluid biproduct : this.fluidBiproducts) {
				fluidJson = ProbableFluid.CODEC.encodeStart(JsonOps.INSTANCE, biproduct).result().get();
				fluidBiproducts.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.FLUID_BIPRODUCTS, fluidBiproducts);
		}

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
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Override
	@Nullable
	public ResourceLocation getAdvancementId() {
		return null;
	}

	public abstract void writeOutput(JsonObject recipeJson);

	public enum RecipeCategory {
		ITEM_2_ITEM,
		ITEM_2_FLUID,
		FLUID_ITEM_2_ITEM,
		FLUID_ITEM_2_FLUID,
		FLUID_2_ITEM,
		FLUID_2_FLUID,
		FLUID_2_GAS,
		FLUID_ITEM_2_GAS;

		public String category() {
			return toString().toLowerCase(Locale.ROOT).replaceAll("_", "");
		}
	}


}
