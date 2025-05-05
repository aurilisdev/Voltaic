package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import voltaic.api.codec.StreamCodec;

public class CountableIngredient extends Ingredient {

	public static final Codec<CountableIngredient> CODEC_DIRECT_ITEM = RecordCodecBuilder.create(instance ->
	//
	instance.group(
			//
			ItemStack.CODEC.fieldOf("item").forGetter(instance0 -> new ItemStack(instance0.item)),
			//
			Codec.INT.fieldOf("count").forGetter(instance0 -> instance0.stackSize)

	)
			//
			.apply(instance, (item, amount) -> new CountableIngredient(new ItemStack(item.getItem(), amount)))

	);

	public static final Codec<CountableIngredient> CODEC_TAGGED_ITEM = RecordCodecBuilder.create(instance ->
	//
	instance.group(
			//
			ITag.codec(() -> ItemTags.getAllTags()).fieldOf("tag").forGetter(instance0 -> instance0.tag),
			//
			Codec.INT.fieldOf("count").forGetter(instance0 -> instance0.stackSize)

	)
			//
			.apply(instance, (tag, count) -> new CountableIngredient((INamedTag<Item>)tag, count))
	//

	);

	public static final Codec<CountableIngredient> CODEC = Codec.either(CODEC_TAGGED_ITEM, CODEC_DIRECT_ITEM).xmap(either -> either.map(tag -> tag, item -> item), value -> {
		//

		if (value.tag != null) {
			return Either.left(value);
		} else if (value.ingredient != null) {
			return Either.right(value);
		} else {
			throw new UnsupportedOperationException("The Countable Ingredient neither has a tag nor a direct item value defined!");
		}

	});

	public static final Codec<List<CountableIngredient>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<PacketBuffer, CountableIngredient> STREAM_CODEC = new StreamCodec<PacketBuffer, CountableIngredient>() {

		@Override
		public void encode(PacketBuffer buffer, CountableIngredient value) {
			buffer.writeBoolean(value.item == null);
			if(value.item == null) {
				StreamCodec.RESOURCE_LOCATION.encode(buffer, value.tag.getName());
				StreamCodec.INT.encode(buffer, value.stackSize);
			} else {
				StreamCodec.ITEM_STACK.encode(buffer, new ItemStack(value.item, value.stackSize));
			}
		}

		@Override
		public CountableIngredient decode(PacketBuffer buffer) {
			if(buffer.readBoolean()) {
				return new CountableIngredient(ItemTags.createOptional(StreamCodec.RESOURCE_LOCATION.decode(buffer)), StreamCodec.INT.decode(buffer));
			} else {
				return new CountableIngredient(StreamCodec.ITEM_STACK.decode(buffer));
			}
		}

	};

	public static final StreamCodec<PacketBuffer, List<CountableIngredient>> LIST_STREAM_CODEC = new StreamCodec<PacketBuffer, List<CountableIngredient>>() {

		@Override
		public void encode(PacketBuffer buf, List<CountableIngredient> ings) {
			buf.writeInt(ings.size());
			for (CountableIngredient ing : ings) {
				STREAM_CODEC.encode(buf, ing);
			}
		}

		@Override
		public List<CountableIngredient> decode(PacketBuffer buf) {
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
	private INamedTag<Item> tag;
	@Nullable
	private Item item;

	@Nullable
	private ItemStack[] countedItems;

	public CountableIngredient(ItemStack stack) {
		super(Stream.empty());
		ingredient = Ingredient.of(stack);
		item = stack.getItem();
		stackSize = stack.getCount();
	}

	public CountableIngredient(INamedTag<Item> tag, int stackSize) {
		super(Stream.empty());
		ingredient = Ingredient.of(tag);
		this.tag = tag;
		this.stackSize = stackSize;
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
		if (obj instanceof CountableIngredient) {
			CountableIngredient otherIng = (CountableIngredient) obj;

			return otherIng.stackSize == stackSize && ingredient.equals(otherIng.ingredient);

		}
		return false;
	}

}
