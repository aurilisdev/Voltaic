package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import voltaic.registers.VoltaicIngredients;

public class FluidIngredient implements Predicate<FluidStack>, ICustomIngredient {

    public static final MapCodec<FluidIngredient> CODEC_DIRECT_FLUID = RecordCodecBuilder.mapCodec(instance ->
    //
    instance.group(
	    //
	    BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(instance0 -> instance0.fluid),
	    //
	    Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

    )
	    //
	    .apply(instance, FluidIngredient::new)

    );

    public static final MapCodec<FluidIngredient> CODEC_TAGGED_FLUID = RecordCodecBuilder.mapCodec(instance ->
    //
    instance.group(
	    //
	    TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(instance0 -> instance0.tag),
	    //
	    Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

    )
	    //
	    .apply(instance, FluidIngredient::new)
    //

    );

    public static final MapCodec<FluidIngredient> CODEC = NeoForgeExtraCodecs
	    .xor(CODEC_TAGGED_FLUID, CODEC_DIRECT_FLUID)
	    .xmap(either -> either.map(tag -> tag, fluid -> fluid), value -> {
		//

		if (value.tag != null)
		    return Either.left(value);
		else if (value.fluid != null)
		    return Either.right(value);
		else
		    throw new UnsupportedOperationException(
			    "The Fluid Ingredient neither has a tag nor a direct fluid value defined!");

	    });

    public static final Codec<List<FluidIngredient>> LIST_CODEC = CODEC.codec().listOf();

    private static final byte TYPE_TAG = 0;
    private static final byte TYPE_FLUID = 1;

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> STREAM_CODEC = new StreamCodec<>() {

	@Override
	public void encode(RegistryFriendlyByteBuf buf, FluidIngredient ing) {
	    TagKey<Fluid> tag = ing.tag;
	    if (tag != null) {
		buf.writeByte(TYPE_TAG);
		buf.writeResourceLocation(tag.location());
		buf.writeInt(ing.amount);
		return;
	    }

	    Fluid fluid = ing.fluid;
	    if (fluid != null) {
		buf.writeByte(TYPE_FLUID);
		FluidStack.STREAM_CODEC.encode(buf, new FluidStack(fluid, ing.amount));
		return;
	    }

	    throw new IllegalStateException("FluidIngredient has no representation");
	}

	@Override
	public FluidIngredient decode(RegistryFriendlyByteBuf buf) {

	    return switch (buf.readByte()) {
	    case TYPE_TAG ->
		new FluidIngredient(TagKey.create(Registries.FLUID, buf.readResourceLocation()), buf.readInt());

	    case TYPE_FLUID -> new FluidIngredient(FluidStack.STREAM_CODEC.decode(buf));

	    default -> throw new IllegalStateException("Unknown FluidIngredient type");
	    };
	}
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidIngredient>> LIST_STREAM_CODEC = new StreamCodec<>() {

	@Override
	public void encode(RegistryFriendlyByteBuf buf, List<FluidIngredient> ings) {
	    buf.writeInt(ings.size());
	    for (FluidIngredient ing : ings) {
		STREAM_CODEC.encode(buf, ing);
	    }
	}

	@Override
	public List<FluidIngredient> decode(RegistryFriendlyByteBuf buf) {
	    int length = buf.readInt();
	    List<FluidIngredient> ings = new ArrayList<>();
	    for (int i = 0; i < length; i++) {
		ings.add(STREAM_CODEC.decode(buf));
	    }
	    return ings;
	}
    };

    @Nullable
    public final TagKey<Fluid> tag;
    @Nullable
    private final Fluid fluid;

    private final int amount;

    private FluidIngredient(@Nullable TagKey<Fluid> tag, @Nullable Fluid fluid, int amount) {
	this.tag = tag;
	this.fluid = fluid;
	this.amount = amount;
    }

    public FluidIngredient(FluidStack fluidStack) {
	this(null, fluidStack.getFluid(), fluidStack.getAmount());
    }

    public FluidIngredient(Fluid fluid, int amount) {
	this(new FluidStack(fluid, amount));
    }

    public FluidIngredient(TagKey<Fluid> tag, int amount) {
	this(tag, null, amount);
    }

    @Override
    public boolean test(ItemStack stack) {
	return false;
    }

    @Override
    public Stream<ItemStack> getItems() {
	return Stream.empty();
    }

    @Override
    public boolean isSimple() {
	return false;
    }

    @Override
    public IngredientType<?> getType() {
	return VoltaicIngredients.FLUID_INGREDIENT_TYPE.get();
    }

    @Override
    public boolean test(@Nullable FluidStack stack) {
	if (stack == null || stack.isEmpty() || stack.getAmount() < amount)
	    return false;
	if (tag != null)
	    return stack.is(tag);
	Fluid fluid = this.fluid;
	if (fluid != null)
	    return stack.getFluid().isSame(fluid);
	return false;
    }

    public List<FluidStack> getMatchingFluids() {
	if (tag != null)
	    return BuiltInRegistries.FLUID.getTag(tag)
		    .map(holders -> holders.stream().map(holder -> new FluidStack(holder, amount)).toList())
		    .orElse(List.of());

	if (fluid != null)
	    return List.of(new FluidStack(fluid, amount));

	return List.of();
    }

    public int getAmount() {
	return amount;
    }

    @Override
    public String toString() {
	if (tag != null)
	    return "Fluid Tag: #" + tag.location() + ", Amt: " + amount;

	if (fluid != null)
	    return "Fluid: " + BuiltInRegistries.FLUID.getKey(fluid) + ", Amt: " + amount;

	return "Empty FluidIngredient";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
	return this == obj || obj instanceof FluidIngredient other && amount == other.amount
		&& Objects.equals(tag, other.tag) && Objects.equals(fluid, other.fluid);
    }

    @Override
    public int hashCode() {
	return 31 * Objects.hash(tag, fluid, amount);
    }

}
