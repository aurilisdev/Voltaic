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

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.VoltaicRecipeSerializer;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;

public abstract class FinishedRecipeBase implements FinishedRecipe {

	private RecipeSerializer<?> serializer;
	private ResourceLocation id;

	private List<ProbableItem> itemBiproducts = new ArrayList<>();
	private List<ProbableFluid> fluidBiproducts = new ArrayList<>();
	private List<ProbableGas> gasBiproducts = new ArrayList<>();

	private List<ItemStack> itemIngredients = new ArrayList<>();
	private List<Pair<TagKey<Item>, Integer>> tagItemIngredients = new ArrayList<>();
	private List<FluidStack> fluidIngredients = new ArrayList<>();
	private List<Pair<TagKey<Fluid>, Integer>> tagFluidIngredients = new ArrayList<>();
	private List<GasStack> gasIngredients = new ArrayList<>();
	private List<Pair<TagKey<Gas>, GasIngWrapper>> tagGasIngredients = new ArrayList<>();

	private double experience = 0.0;
	private int processTime = 0;
	private double usagePerTick = 0.0;

	protected FinishedRecipeBase(RecipeSerializer<?> serializer, double experience, int processTime, double usagePerTick) {
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

	public FinishedRecipeBase addItemTagInput(TagKey<Item> tag, int count) {
		tagItemIngredients.add(Pair.of(tag, count));
		return this;
	}

	public FinishedRecipeBase addFluidStackInput(FluidStack stack) {
		fluidIngredients.add(stack);
		return this;
	}

	public FinishedRecipeBase addFluidTagInput(TagKey<Fluid> tag, int count) {
		tagFluidIngredients.add(Pair.of(tag, count));
		return this;
	}

	public FinishedRecipeBase addGasStackInput(GasStack stack) {
		gasIngredients.add(stack);
		return this;
	}

	public FinishedRecipeBase addGasTagInput(TagKey<Gas> tag, int amt, int temp, int pressure) {
		tagGasIngredients.add(Pair.of(tag, new GasIngWrapper(amt, temp, pressure)));
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

	public FinishedRecipeBase addGasBiproduct(ProbableGas biproduct) {
		gasBiproducts.add(biproduct);
		return this;
	}

	public void complete(Consumer<FinishedRecipe> consumer) {
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
			for (Pair<TagKey<Item>, Integer> itemTags : tagItemIngredients) {
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
			for (Pair<TagKey<Fluid>, Integer> fluidTags : tagFluidIngredients) {
				ing = new FluidIngredient(fluidTags.getFirst(), fluidTags.getSecond());
				fluidJson = FluidIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.FLUID_INPUTS, fluidInputs);
		}

		int gasInputsCount = gasIngredients.size() + tagGasIngredients.size();
		if (gasInputsCount > 0) {
			inputsFlag = true;
			JsonObject gasInputs = new JsonObject();
			gasInputs.addProperty(VoltaicRecipeSerializer.COUNT, gasInputsCount);
			JsonElement gasJson;
			GasIngredient ing;
			int index = 0;
			for (GasStack stack : gasIngredients) {
				ing = new GasIngredient(stack);
				gasJson = GasIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				gasInputs.add(index + "", gasJson);
				index++;
			}
			for (Pair<TagKey<Gas>, GasIngWrapper> gasTags : tagGasIngredients) {
				GasIngWrapper wrapper = gasTags.getSecond();
				ing = new GasIngredient(gasTags.getFirst(), wrapper.amt, wrapper.pressure, wrapper.temp);
				gasJson = GasIngredient.CODEC.encodeStart(JsonOps.INSTANCE, ing).result().get();
				gasInputs.add(index + "", gasJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.GAS_INPUTS, gasInputs);
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

		if (gasBiproducts.size() > 0) {
			JsonObject gasBiproducts = new JsonObject();
			gasBiproducts.addProperty(VoltaicRecipeSerializer.COUNT, this.gasBiproducts.size());
			JsonElement gasJson;
			int index = 0;
			for (ProbableGas biproduct : this.gasBiproducts) {
				gasJson = ProbableGas.CODEC.encodeStart(JsonOps.INSTANCE, biproduct).result().get();
				gasBiproducts.add(index + "", gasJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.GAS_BIPRODUCTS, gasBiproducts);
		}

	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getType() {
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

	public static record GasIngWrapper(int amt, int temp, int pressure) {

	}

}
