package electrodynamics.common.recipe.categories.fluid2gas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import electrodynamics.api.gas.GasStack;
import electrodynamics.common.recipe.ElectrodynamicsRecipeSerializer;
import electrodynamics.common.recipe.recipeutils.FluidIngredient;
import electrodynamics.common.recipe.recipeutils.ProbableFluid;
import electrodynamics.common.recipe.recipeutils.ProbableGas;
import electrodynamics.common.recipe.recipeutils.ProbableItem;
import electrodynamics.prefab.utilities.CodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Fluid2GasRecipeSerializer<T extends Fluid2GasRecipe> extends ElectrodynamicsRecipeSerializer<T> {

    @SuppressWarnings("unused")
	private final Fluid2GasRecipe.Factory<T> factory;
    private final MapCodec<T> codec;

    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public Fluid2GasRecipeSerializer(Fluid2GasRecipe.Factory<T> factory) {
        this.factory = factory;
        codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                                //
                                Codec.STRING.fieldOf(GROUP).forGetter(T::getGroup),
                                //
                                FluidIngredient.LIST_CODEC.fieldOf(FLUID_INPUTS).forGetter(T::getFluidIngredients),
                                //
                                GasStack.CODEC.fieldOf(OUTPUT).forGetter(T::getGasRecipeOutput),
                                //
                                Codec.DOUBLE.optionalFieldOf(EXPERIENCE, 0.0).forGetter(T::getXp),
                                //
                                Codec.INT.fieldOf(TICKS).forGetter(T::getTicks),
                                //
                                Codec.DOUBLE.fieldOf(USAGE_PER_TICK).forGetter(T::getUsagePerTick),
                                //
                                ProbableItem.LIST_CODEC.optionalFieldOf(ITEM_BIPRODUCTS, ProbableItem.NONE).forGetter(T::getItemBiproducts),
                                //
                                ProbableFluid.LIST_CODEC.optionalFieldOf(FLUID_BIPRODUCTS, ProbableFluid.NONE).forGetter(T::getFluidBiproducts),
                                //
                                ProbableGas.LIST_CODEC.optionalFieldOf(GAS_BIPRODUCTS, ProbableGas.NONE).forGetter(T::getGasBiproducts)
                                //

                        )
                        //
                        .apply(instance, factory::create)

        );

        streamCodec = CodecUtils.composite(
                ByteBufCodecs.STRING_UTF8, T::getGroup,
                FluidIngredient.LIST_STREAM_CODEC, T::getFluidIngredients,
                GasStack.STREAM_CODEC, T::getGasRecipeOutput,
                ByteBufCodecs.DOUBLE, T::getXp,
                ByteBufCodecs.INT, T::getTicks,
                ByteBufCodecs.DOUBLE, T::getUsagePerTick,
                ProbableItem.LIST_STREAM_CODEC, T::getItemBiproducts,
                ProbableFluid.LIST_STREAM_CODEC, T::getFluidBiproducts,
                ProbableGas.LIST_STREAM_CODEC, T::getGasBiproducts,
                factory::create

        );
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    /*
    @Override
    public @Nullable T fromNetwork(FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        boolean hasItemBi = buffer.readBoolean();
        boolean hasFluidBi = buffer.readBoolean();
        boolean hasGasBi = buffer.readBoolean();
        List<FluidIngredient> inputs = FluidIngredient.readList(buffer);
        GasStack output = GasStack.readFromBuffer(buffer);
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
        return factory.create(group, inputs, output, experience, ticks, usagePerTick, itemBi, fluidBi, gasBi);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeBoolean(recipe.hasItemBiproducts());
        buffer.writeBoolean(recipe.hasFluidBiproducts());
        buffer.writeBoolean(recipe.hasGasBiproducts());
        FluidIngredient.writeList(buffer, recipe.getFluidIngredients());
        recipe.getGasRecipeOutput().writeToBuffer(buffer);
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
