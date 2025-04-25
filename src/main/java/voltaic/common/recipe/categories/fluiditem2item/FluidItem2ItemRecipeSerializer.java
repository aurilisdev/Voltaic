package voltaic.common.recipe.categories.fluiditem2item;

import java.util.List;

import com.google.gson.JsonObject;

import voltaic.api.codec.StreamCodec;
import voltaic.common.recipe.VoltaicRecipeSerializer;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.utilities.CodecUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FluidItem2ItemRecipeSerializer<T extends FluidItem2ItemRecipe> extends VoltaicRecipeSerializer<T> {
	
	private final FluidItem2ItemRecipe.Factory<T> factory;

    public FluidItem2ItemRecipeSerializer(FluidItem2ItemRecipe.Factory<T> factory) {
       super(CodecUtils.composite(
                StreamCodec.RESOURCE_LOCATION, T::getId,
                CountableIngredient.LIST_STREAM_CODEC, T::getCountedIngredients,
                FluidIngredient.LIST_STREAM_CODEC, T::getFluidIngredients,
                StreamCodec.ITEM_STACK, T::getItemRecipeOutput,
                StreamCodec.DOUBLE, T::getXp,
                StreamCodec.INT, T::getTicks,
                StreamCodec.DOUBLE, T::getUsagePerTick,
                ProbableItem.LIST_STREAM_CODEC, T::getItemBiproducts,
                ProbableFluid.LIST_STREAM_CODEC, T::getFluidBiproducts,
                ProbableGas.LIST_STREAM_CODEC, T::getGasBiproducts,
                factory::create
        ));
       this.factory = factory;
    }
    
    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject recipeJson) {
    	List<CountableIngredient> inputs = getItemIngredients(recipeId, recipeJson);
    	List<FluidIngredient> fluidInputs = getFluidIngredients(recipeId, recipeJson);
		ItemStack output = getItemOutput(recipeId, recipeJson);
		double experience = getExperience(recipeJson);
		int ticks = getTicks(recipeId, recipeJson);
		double usagePerTick = getUsagePerTick(recipeId, recipeJson);
		List<ProbableItem> itemBi = getItemBiproducts(recipeId, recipeJson);
		List<ProbableFluid> fluidBi = getFluidBiproducts(recipeId, recipeJson);
		List<ProbableGas> gasBi = getGasBiproducts(recipeId, recipeJson);
    	return factory.create(recipeId, inputs, fluidInputs, output, experience, ticks, usagePerTick, itemBi, fluidBi, gasBi);
    }

    /*
    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        boolean hasItemBi = buffer.readBoolean();
        boolean hasFluidBi = buffer.readBoolean();
        boolean hasGasBi = buffer.readBoolean();
        List<CountableIngredient> inputs = CountableIngredient.readList(buffer);
        List<FluidIngredient> fluidInputs = FluidIngredient.readList(buffer);
        ItemStack output = buffer.readItem();
        double experience = buffer.readDouble();
        int ticks = buffer.readInt();
        double usagePerTick = buffer.readDouble();
        List<ProbableItem> itemBi = null;
        List<ProbableFluid> fluidBi = null;
        List<ProbableGas> gasBi = null;
        if (hasItemBi) {
            itemBi = ProbableItem.readList(buffer);
        }
        if (hasFluidBi) {
            fluidBi = ProbableFluid.readList(buffer);

        }
        if (hasGasBi) {
            gasBi = ProbableGas.readList(buffer);
        }
        return factory.create(group, inputs, fluidInputs, output, experience, ticks, usagePerTick, itemBi, fluidBi, gasBi);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeBoolean(recipe.hasItemBiproducts());
        buffer.writeBoolean(recipe.hasFluidBiproducts());
        buffer.writeBoolean(recipe.hasGasBiproducts());
        CountableIngredient.writeList(buffer, recipe.getCountedIngredients());
        FluidIngredient.writeList(buffer, recipe.getFluidIngredients());
        buffer.writeItem(recipe.getItemOutputNoAccess());
        buffer.writeDouble(recipe.getXp());
        buffer.writeInt(recipe.getTicks());
        buffer.writeDouble(recipe.getUsagePerTick());
        if (recipe.hasItemBiproducts()) {
            ProbableItem.writeList(buffer, recipe.getItemBiproducts());
        }
        if (recipe.hasFluidBiproducts()) {
            ProbableFluid.writeList(buffer, recipe.getFluidBiproducts());
        }
        if (recipe.hasGasBiproducts()) {
            ProbableGas.writeList(buffer, recipe.getGasBiproducts());
        }
    }

     */

}
