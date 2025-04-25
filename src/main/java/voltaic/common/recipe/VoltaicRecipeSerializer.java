package voltaic.common.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.api.codec.StreamCodec;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.registers.VoltaicRegistries;

public abstract class VoltaicRecipeSerializer<T extends VoltaicRecipe> implements RecipeSerializer<T> {
	
	private final StreamCodec<FriendlyByteBuf, T> streamCodec;
	
	public VoltaicRecipeSerializer(StreamCodec<FriendlyByteBuf, T> streamCodec) {
		this.streamCodec = streamCodec;
	}

    public static final String COUNT = "count";
    public static final String ITEM_INPUTS = "iteminputs";
    public static final String FLUID_INPUTS = "fluidinputs";
    public static final String GAS_INPUTS = "gasinputs";
    public static final String ITEM_BIPRODUCTS = "itembi";
    public static final String FLUID_BIPRODUCTS = "fluidbi";
    public static final String GAS_BIPRODUCTS = "gasbi";
    public static final String OUTPUT = "output";
    public static final String EXPERIENCE = "experience";
    public static final String TICKS = "ticks";
    public static final String USAGE_PER_TICK = "usagepertick";
    public static final String GROUP = "group";
    
    @Override
    public @Nullable T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
    	return streamCodec.decode(pBuffer);
    }
    
    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
    	streamCodec.encode(pBuffer, pRecipe);
    }
    
    public static List<CountableIngredient> getItemIngredients(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(ITEM_INPUTS)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": There are no Item Inputs!");
		}
		JsonObject itemInputs = GsonHelper.getAsJsonObject(json, ITEM_INPUTS);
		if (!itemInputs.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = itemInputs.get(COUNT).getAsInt();
		List<CountableIngredient> itemIngredients = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!itemInputs.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			CountableIngredient.CODEC.decode(JsonOps.INSTANCE, itemInputs.get(i + "")).result().ifPresent(pair -> itemIngredients.add(pair.getFirst()));
		}
		return itemIngredients;
	}

	public static List<FluidIngredient> getFluidIngredients(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(FLUID_INPUTS)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": There are no Fluid Inputs!");
		}
		JsonObject fluidInputs = GsonHelper.getAsJsonObject(json, FLUID_INPUTS);
		if (!fluidInputs.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = fluidInputs.get(COUNT).getAsInt();
		List<FluidIngredient> fluidIngredients = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!fluidInputs.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			FluidIngredient.CODEC.decode(JsonOps.INSTANCE, fluidInputs.get(i + "").getAsJsonObject()).result().ifPresent(pair -> fluidIngredients.add(pair.getFirst()));
		}
		return fluidIngredients;
	}

	public static List<GasIngredient> getGasIngredients(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(GAS_INPUTS)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": There are no Gas Inputs!");
		}
		JsonObject fluidInputs = GsonHelper.getAsJsonObject(json, GAS_INPUTS);
		if (!fluidInputs.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = fluidInputs.get(COUNT).getAsInt();
		List<GasIngredient> gasIngredients = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!fluidInputs.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			GasIngredient.CODEC.decode(JsonOps.INSTANCE, fluidInputs.get(i + "").getAsJsonObject()).result().ifPresent(pair -> gasIngredients.add(pair.getFirst()));
		}
		return gasIngredients;
	}

	@Nullable
	public static List<ProbableItem> getItemBiproducts(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(ITEM_BIPRODUCTS)) {
			return null;
		}
		JsonObject itemBiproducts = GsonHelper.getAsJsonObject(json, ITEM_BIPRODUCTS);

		if (!itemBiproducts.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = itemBiproducts.get(COUNT).getAsInt();
		List<ProbableItem> items = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!itemBiproducts.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			ProbableItem.CODEC.decode(JsonOps.INSTANCE, itemBiproducts.get("" + i).getAsJsonObject()).result().ifPresent(pair -> items.add(pair.getFirst()));
		}
		return items;
	}

	@Nullable
	public static List<ProbableFluid> getFluidBiproducts(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(FLUID_BIPRODUCTS)) {
			return null;
		}
		JsonObject fluidBiproducts = GsonHelper.getAsJsonObject(json, FLUID_BIPRODUCTS);
		if (!fluidBiproducts.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = fluidBiproducts.get(COUNT).getAsInt();
		List<ProbableFluid> gases = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!fluidBiproducts.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			ProbableFluid.CODEC.decode(JsonOps.INSTANCE, fluidBiproducts.get(i + "").getAsJsonObject()).result().ifPresent(pair -> gases.add(pair.getFirst()));
		}
		return gases;
	}

	public static List<ProbableGas> getGasBiproducts(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(GAS_BIPRODUCTS)) {
			return null;
		}
		JsonObject gasBiproducts = GsonHelper.getAsJsonObject(json, GAS_BIPRODUCTS);
		if (!gasBiproducts.has(COUNT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a count field");
		}
		int count = gasBiproducts.get(COUNT).getAsInt();
		List<ProbableGas> fluids = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (!gasBiproducts.has(i + "")) {
				throw new UnsupportedOperationException(recipeId.toString() + ": The count field does not match the input count");
			}
			ProbableGas.CODEC.decode(JsonOps.INSTANCE, gasBiproducts.get(i + "").getAsJsonObject()).result().ifPresent(pair -> fluids.add(pair.getFirst()));
		}
		return fluids;
	}

	public static ItemStack getItemOutput(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(OUTPUT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include an Item output!");
		}
		return CraftingHelper.getItemStack(json.get(OUTPUT).getAsJsonObject(), false);
	}

	public static FluidStack getFluidOutput(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(OUTPUT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a Fluid output!");
		}
		JsonObject fluid = json.get(OUTPUT).getAsJsonObject();
		ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(fluid, "fluid"));
		int amount = GsonHelper.getAsInt(fluid, "amount");
		return new FluidStack(ForgeRegistries.FLUIDS.getValue(resourceLocation), amount);
	}

	public static GasStack getGasOutput(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(OUTPUT)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a Gas output!");
		}
		JsonObject gas = json.get(OUTPUT).getAsJsonObject();
		ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(gas, "gas"));
		int amount = GsonHelper.getAsInt(gas, "amount");
		int temperature = GsonHelper.getAsInt(gas, "temp");
		int pressure = GsonHelper.getAsInt(gas, "pressure");
		return new GasStack(VoltaicRegistries.gasRegistry().getValue(resourceLocation), amount, temperature, pressure);
	}

	public static double getExperience(JsonObject json) {
		return json.has(EXPERIENCE) ? json.get(EXPERIENCE).getAsDouble() : 0;
	}

	public static int getTicks(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(TICKS)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include an operating tick time!");
		}
		return json.get(TICKS).getAsInt();
	}

	public static double getUsagePerTick(ResourceLocation recipeId, JsonObject json) {
		if (!json.has(USAGE_PER_TICK)) {
			throw new UnsupportedOperationException(recipeId.toString() + ": You must include a usage per tick!");
		}
		return json.get(USAGE_PER_TICK).getAsDouble();
	}

}
