package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.api.codec.StreamCodec;

/**
 * Extension of Ingredient that adds Fluid compatibility
 * 
 * @author skip999
 *
 */
public class FluidIngredient extends Ingredient {

    // Mojank...

    public static final Codec<FluidIngredient> CODEC_DIRECT_FLUID = RecordCodecBuilder.create(instance ->
    //
    instance.group(
	    //
	    ForgeRegistries.FLUIDS.getCodec().fieldOf("fluid").forGetter(instance0 -> instance0.fluid),
	    //
	    Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

    )
	    //
	    .apply(instance, FluidIngredient::new)

    );

    public static final Codec<FluidIngredient> CODEC_TAGGED_FLUID = RecordCodecBuilder.create(instance ->
    //
    instance.group(
	    //
	    TagKey.codec(ForgeRegistries.Keys.FLUIDS).fieldOf("tag").forGetter(instance0 -> instance0.tag),
	    //
	    Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

    )
	    //
	    .apply(instance, FluidIngredient::new)
    //

    );

    public static final Codec<FluidIngredient> CODEC = Codec.either(CODEC_TAGGED_FLUID, CODEC_DIRECT_FLUID)
	    .xmap(either -> either.map(tag -> tag, fluid -> fluid), value -> {

		if (value.tag != null) {
		    return Either.left(value);
		} else if (value.fluid != null) {
		    return Either.right(value);
		} else {
		    throw new UnsupportedOperationException(
			    "The Fluid Ingredient neither has a tag nor a direct fluid value defined!");
		}

	    });

    public static final Codec<List<FluidIngredient>> LIST_CODEC = CODEC.listOf();

    private static final byte TYPE_TAG = 0;
    private static final byte TYPE_FLUID = 1;

    public static final StreamCodec<FriendlyByteBuf, FluidIngredient> STREAM_CODEC = new StreamCodec<>() {

	@Override
	public void encode(FriendlyByteBuf buf, FluidIngredient ing) {

	    if (ing.tag != null) {
		buf.writeByte(TYPE_TAG);
		buf.writeResourceLocation(ing.tag.location());
		buf.writeInt(ing.amount);
		return;
	    }

	    if (ing.fluid != null) {
		buf.writeByte(TYPE_FLUID);
		StreamCodec.FLUID_STACK.encode(buf, new FluidStack(ing.fluid, ing.amount));
		return;
	    }

	    throw new IllegalStateException("FluidIngredient has no representation");
	}

	@Override
	public FluidIngredient decode(FriendlyByteBuf buf) {

	    return switch (buf.readByte()) {

	    case TYPE_TAG -> new FluidIngredient(TagKey.create(ForgeRegistries.Keys.FLUIDS, buf.readResourceLocation()),
		    buf.readInt());

	    case TYPE_FLUID -> new FluidIngredient(StreamCodec.FLUID_STACK.decode(buf));

	    default -> throw new IllegalStateException("Unknown FluidIngredient type");

	    };
	}
    };

    public static final StreamCodec<FriendlyByteBuf, List<FluidIngredient>> LIST_STREAM_CODEC = new StreamCodec<>() {

	@Override
	public void encode(FriendlyByteBuf buf, List<FluidIngredient> ings) {
	    buf.writeInt(ings.size());

	    for (FluidIngredient ing : ings) {
		STREAM_CODEC.encode(buf, ing);
	    }
	}

	@Override
	public List<FluidIngredient> decode(FriendlyByteBuf buf) {
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
	super(Stream.empty());
	this.tag = tag;
	this.fluid = fluid;
	this.amount = amount;
    }

    public FluidIngredient(FluidStack fluidStack) {
	this(null, fluidStack.getFluid(), fluidStack.getAmount());
    }

    public FluidIngredient(Fluid fluid, int amount) {
	this(null, fluid, amount);
    }

    public FluidIngredient(TagKey<Fluid> tag, int amount) {
	this(tag, null, amount);
    }

    @Override
    public boolean test(ItemStack stack) {
	return false;
    }

    @Override
    public ItemStack[] getItems() {
	return new ItemStack[] {};
    }

    @Override
    public boolean isSimple() {
	return false;
    }

    public boolean testFluid(@Nullable FluidStack stack) {

	if (stack == null || stack.isEmpty()) {
	    return false;
	}

	if (stack.getAmount() < amount) {
	    return false;
	}

	if (tag != null) {
	    return ForgeRegistries.FLUIDS.tags().getTag(tag).contains(stack.getFluid());
	}

	if (fluid != null) {
	    return stack.getFluid().isSame(fluid);
	}

	return false;
    }

    public List<FluidStack> getMatchingFluids() {

	if (tag != null) {

	    List<FluidStack> fluids = new ArrayList<>();

	    ForgeRegistries.FLUIDS.tags().getTag(tag).forEach(fluid -> {
		fluids.add(new FluidStack(fluid, amount));
	    });

	    return fluids;
	}

	if (fluid != null) {
	    return List.of(new FluidStack(fluid, amount));
	}

	return List.of();
    }

    public int getAmount() {
	return amount;
    }

    @Override
    public String toString() {

	if (tag != null) {
	    return "Fluid Tag: #" + tag.location() + ", Amt: " + amount;
	}

	if (fluid != null) {
	    return "Fluid: " + ForgeRegistries.FLUIDS.getKey(fluid) + ", Amt: " + amount;
	}

	return "Empty FluidIngredient";
    }

    @Override
    public boolean equals(Object obj) {
	return this == obj || obj instanceof FluidIngredient other && amount == other.amount
		&& Objects.equals(tag, other.tag) && Objects.equals(fluid, other.fluid);
    }

    @Override
    public int hashCode() {
	return Objects.hash(tag, fluid, amount);
    }

}