package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
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
			FluidStack.CODEC.fieldOf("fluid").forGetter(instance0 -> new FluidStack(instance0.fluid, 0)),
			//
			Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

	)
			//
			.apply(instance, (fluid, amount) -> new FluidIngredient(fluid.getFluid(), amount))

	);

	public static final Codec<FluidIngredient> CODEC_TAGGED_FLUID = RecordCodecBuilder.create(instance ->
	//
	instance.group(
			//
			ResourceLocation.CODEC.fieldOf("tag").forGetter(instance0 -> instance0.tag.getName()),
			//
			Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.amount)

	)
			//
			.apply(instance, (tag, amount) -> new FluidIngredient(FluidTags.createOptional(tag), amount))
	//

	);

	public static final Codec<FluidIngredient> CODEC = Codec.either(CODEC_TAGGED_FLUID, CODEC_DIRECT_FLUID).xmap(either -> either.map(tag -> tag, fluid -> fluid), value -> {
		//

		if (value.tag != null) {
			return Either.left(value);
		} else if (value.fluid != null) {
			return Either.right(value);
		} else {
			throw new UnsupportedOperationException("The Fluid Ingredient neither has a tag nor a direct fluid value defined!");
		}

	});

	public static final Codec<List<FluidIngredient>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<PacketBuffer, FluidIngredient> STREAM_CODEC = new StreamCodec<PacketBuffer, FluidIngredient>() {

		@Override
		public void encode(PacketBuffer buf, FluidIngredient ing) {
			List<FluidStack> fluidStacks = ing.getMatchingFluids();
			buf.writeInt(fluidStacks.size());
			for (FluidStack stack : fluidStacks) {
				StreamCodec.FLUID_STACK.encode(buf, stack);
			}
		}

		@Override
		public FluidIngredient decode(PacketBuffer buf) {
			List<FluidStack> stacks = new ArrayList<>();
			int count = buf.readInt();
			for (int i = 0; i < count; i++) {
				stacks.add(StreamCodec.FLUID_STACK.decode(buf));
			}
			return new FluidIngredient(stacks);
		}
	};

	public static final StreamCodec<PacketBuffer, List<FluidIngredient>> LIST_STREAM_CODEC = new StreamCodec<PacketBuffer, List<FluidIngredient>>() {

		@Override
		public void encode(PacketBuffer buf, List<FluidIngredient> ings) {
			buf.writeInt(ings.size());
			for (FluidIngredient ing : ings) {
				STREAM_CODEC.encode(buf, ing);
			}
		}

		@Override
		public List<FluidIngredient> decode(PacketBuffer buf) {
			int length = buf.readInt();
			List<FluidIngredient> ings = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				ings.add(STREAM_CODEC.decode(buf));
			}
			return ings;
		}
	};

	@Nonnull
	private List<FluidStack> fluidStacks;

	@Nullable
	public INamedTag<Fluid> tag;
	@Nullable
	private Fluid fluid;
	private int amount;

	public FluidIngredient(FluidStack fluidStack) {
		super(Stream.empty());
		this.fluid = fluidStack.getFluid();
		this.amount = fluidStack.getAmount();
	}

	public FluidIngredient(Fluid fluid, int amount) {
		this(new FluidStack(fluid, amount));
	}

	public FluidIngredient(List<FluidStack> fluidStack) {
		super(Stream.empty());
		fluidStacks = fluidStack;
		FluidStack fluid = getFluidStack();
		this.fluid = fluid.getFluid();
		this.amount = fluid.getAmount();
	}

	public FluidIngredient(INamedTag<Fluid> tag, int amount) {
		super(Stream.empty());
		this.tag = tag;
		this.amount = amount;

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

	public boolean testFluid(@Nullable FluidStack t) {
		if (t == null || t.isEmpty()) {
			return false;
		}

		for (FluidStack stack : getMatchingFluids()) {
			if (t.getAmount() >= stack.getAmount()) {
				if (t.getFluid().isSame(stack.getFluid())) {
					return true;
				}
			}
		}
		return false;
	}

	public List<FluidStack> getMatchingFluids() {

		if (fluidStacks == null) {

			fluidStacks = new ArrayList<>();

			if (tag != null) {

				FluidTags.getAllTags().getTag(tag.getName()).getValues().forEach(h -> {
					fluidStacks.add(new FluidStack(h, amount));
				});

			} else if (fluid != null) {

				fluidStacks.add(new FluidStack(fluid, amount));

			} else {
				throw new UnsupportedOperationException("Fluid Ingredient has neither a fluid nor a fluid tag defined");
			}

		}

		return fluidStacks;
	}

	public FluidStack getFluidStack() {
		return getMatchingFluids().size() < 1 ? FluidStack.EMPTY : getMatchingFluids().get(0);
	}

	@Override
	public String toString() {
		return "Fluid : " + getFluidStack().getFluid().toString() + ", Amt : " + amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FluidIngredient) {
			FluidIngredient ing = (FluidIngredient) obj;
			return ing.getMatchingFluids().equals(getMatchingFluids()) && ing.amount == amount;
		}
		return false;
	}

}
