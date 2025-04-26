package voltaic.datagen.utils.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.VoltaicRecipeSerializer;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.registers.VoltaicRegistries;

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

	public FinishedRecipeBase addGasTagInput(TagKey<Gas> tag, double amt, double temp, double pressure) {
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
			JsonObject itemJson;
			int index = 0;
			for (ItemStack stack : itemIngredients) {
				itemJson = new JsonObject();
				itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
				itemJson.addProperty(VoltaicRecipeSerializer.COUNT, stack.getCount());
				itemInputs.add(index + "", itemJson);
				index++;
			}
			for (Pair<TagKey<Item>, Integer> itemTags : tagItemIngredients) {
				itemJson = new JsonObject();
				itemJson.addProperty("tag", itemTags.getFirst().location().toString());
				itemJson.addProperty(VoltaicRecipeSerializer.COUNT, itemTags.getSecond());
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
			JsonObject fluidJson;
			int index = 0;
			for (FluidStack stack : fluidIngredients) {
				fluidJson = new JsonObject();
				fluidJson.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
				fluidJson.addProperty("amount", stack.getAmount());
				fluidInputs.add(index + "", fluidJson);
				index++;
			}
			for (Pair<TagKey<Fluid>, Integer> fluidTags : tagFluidIngredients) {
				fluidJson = new JsonObject();
				fluidJson.addProperty("tag", fluidTags.getFirst().location().toString());
				fluidJson.addProperty("amount", fluidTags.getSecond());
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
			JsonObject gasJson;
			int index = 0;
			for (GasStack stack : gasIngredients) {
				gasJson = new JsonObject();
				gasJson.addProperty("gas", VoltaicRegistries.gasRegistry().getKey(stack.getGas()).toString());
				gasJson.addProperty("amount", stack.getAmount());
				gasJson.addProperty("temp", stack.getTemperature());
				gasJson.addProperty("pressure", stack.getPressure());
				gasInputs.add(index + "", gasJson);
				index++;
			}
			for (Pair<TagKey<Gas>, GasIngWrapper> gasTags : tagGasIngredients) {
				gasJson = new JsonObject();
				gasJson.addProperty("tag", gasTags.getFirst().location().toString());
				gasJson.addProperty("amount", gasTags.getSecond().amt());
				gasJson.addProperty("temp", gasTags.getSecond().temp());
				gasJson.addProperty("pressure", gasTags.getSecond().pressure());
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
			JsonObject itemJson;
			ItemStack stack;
			int index = 0;
			for (ProbableItem biproduct : this.itemBiproducts) {
				itemJson = new JsonObject();
				stack = biproduct.getFullStack();
				itemJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
				itemJson.addProperty(VoltaicRecipeSerializer.COUNT, stack.getCount());
				itemJson.addProperty("chance", biproduct.getChance());
				itemBiproducts.add(index + "", itemJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.ITEM_BIPRODUCTS, itemBiproducts);
		}

		if (fluidBiproducts.size() > 0) {
			JsonObject fluidBiproducts = new JsonObject();
			fluidBiproducts.addProperty(VoltaicRecipeSerializer.COUNT, this.fluidBiproducts.size());
			JsonObject fluidJson;
			FluidStack stack;
			int index = 0;
			for (ProbableFluid biproduct : this.fluidBiproducts) {
				fluidJson = new JsonObject();
				stack = biproduct.getFullStack();
				fluidJson.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
				fluidJson.addProperty("amount", stack.getAmount());
				fluidJson.addProperty("chance", biproduct.getChance());
				fluidBiproducts.add(index + "", fluidJson);
				index++;
			}
			recipeJson.add(VoltaicRecipeSerializer.FLUID_BIPRODUCTS, fluidBiproducts);
		}

		if (gasBiproducts.size() > 0) {
			JsonObject gasBiproducts = new JsonObject();
			gasBiproducts.addProperty(VoltaicRecipeSerializer.COUNT, this.gasBiproducts.size());
			JsonObject gasJson;
			GasStack stack;
			int index = 0;
			for (ProbableGas biproduct : this.gasBiproducts) {
				gasJson = new JsonObject();
				stack = biproduct.getFullStack();
				gasJson.addProperty("gas", VoltaicRegistries.gasRegistry().getKey(stack.getGas()).toString());
				gasJson.addProperty("amount", stack.getAmount());
				gasJson.addProperty("temp", stack.getTemperature());
				gasJson.addProperty("pressure", stack.getPressure());
				gasJson.addProperty("chance", biproduct.getChance());
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

	public static record GasIngWrapper(double amt, double temp, double pressure) {

	}

}
