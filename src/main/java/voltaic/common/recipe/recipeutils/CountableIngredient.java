package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import voltaic.api.codec.StreamCodec;

public class CountableIngredient extends Ingredient {

	public static final Codec<CountableIngredient> CODEC_DIRECT_ITEM = RecordCodecBuilder.create(instance ->
	//
	instance.group(
			//
			BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(instance0 -> instance0.item),
			//
			Codec.INT.fieldOf("count").forGetter(instance0 -> instance0.stackSize)

	)
			//
			.apply(instance, (item, amount) -> new CountableIngredient(item, amount))

	);

	public static final Codec<CountableIngredient> CODEC_TAGGED_ITEM = RecordCodecBuilder.create(instance ->
	//
	instance.group(
			//
			TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(instance0 -> instance0.tag),
			//
			Codec.INT.fieldOf("count").forGetter(instance0 -> instance0.stackSize)

	)
			//
			.apply(instance, (tag, count) -> new CountableIngredient(tag, count))
	//

	);

	public static final Codec<CountableIngredient> CODEC = Codec.either(CODEC_TAGGED_ITEM, CODEC_DIRECT_ITEM).xmap(either -> either.map(tag -> tag, item -> item), value -> {
		//

		if (value.tag != null) {
			return Either.left(value);
		} else if (value.item != null) {
			return Either.right(value);
		} else {
			throw new UnsupportedOperationException("The Fluid Ingredient neither has a tag nor a direct fluid value defined!");
		}

	});

	public static final Codec<List<CountableIngredient>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<FriendlyByteBuf, CountableIngredient> STREAM_CODEC = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buffer, CountableIngredient value) {
			value.ingredient.toNetwork(buffer);
			buffer.writeInt(value.stackSize);
		}

		@Override
		public CountableIngredient decode(FriendlyByteBuf buffer) {
			return new CountableIngredient(Ingredient.fromNetwork(buffer), buffer.readInt());
		}

	};

	public static final StreamCodec<FriendlyByteBuf, List<CountableIngredient>> LIST_STREAM_CODEC = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buf, List<CountableIngredient> ings) {
			buf.writeInt(ings.size());
			for (CountableIngredient ing : ings) {
				STREAM_CODEC.encode(buf, ing);
			}
		}

		@Override
		public List<CountableIngredient> decode(FriendlyByteBuf buf) {
			int length = buf.readInt();
			List<CountableIngredient> ings = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				ings.add(STREAM_CODEC.decode(buf));
			}
			return ings;
		}
	};

	private final int stackSize;

	private final Ingredient ingredient;

	@Nullable
	private TagKey<Item> tag;
	@Nullable
	private Item item;

	@Nullable
	private ItemStack[] countedItems;

	public CountableIngredient(ItemStack stack) {
		this(Ingredient.of(stack), stack.getCount());
	}

	public CountableIngredient(Ingredient ingredient, int stackSize) {
		super(Stream.empty());
		this.ingredient = ingredient;
		this.stackSize = stackSize;

	}

	public CountableIngredient(TagKey<Item> tag, int stackSize) {
		this(Ingredient.of(tag), stackSize);
		this.tag = tag;
	}

	public CountableIngredient(Item item, int stackSize) {
		this(Ingredient.of(new ItemStack(item)), stackSize);
		this.item = item;
	}

	@Override
	public boolean test(ItemStack stack) {
		return ingredient.test(stack) && stackSize <= stack.getCount();
	}

	@Override
	public ItemStack[] getItems() {
		if (countedItems == null) {
			ItemStack[] items = ingredient.getItems();
			for (ItemStack item : items) {
				item.setCount(stackSize);
			}
			countedItems = items;
		}
		return countedItems;
	}

	public ItemStack[] getItemsArray() {
		if (countedItems == null) {
			ItemStack[] items = ingredient.getItems();
			for (ItemStack item : items) {
				item.setCount(stackSize);
			}
			countedItems = items;
		}
		return countedItems;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	public int getStackSize() {
		return stackSize;
	}

	@Override
	public String toString() {
		return getItemsArray().length == 0 ? "empty" : getItemsArray()[0].toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CountableIngredient otherIng) {

			return otherIng.stackSize == stackSize && ingredient.equals(otherIng.ingredient);

		}
		return false;
	}

}
