package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ProbableItem extends AbstractProbable<ItemStack> {

    public static final Codec<ProbableItem> CODEC = RecordCodecBuilder.create(instance -> instance
	    .group(ItemStack.CODEC.fieldOf("item").forGetter(ProbableItem::getFullStack),
		    Codec.DOUBLE.fieldOf("chance").forGetter(ProbableItem::getChance))
	    .apply(instance, ProbableItem::new));

    public static final Codec<List<ProbableItem>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, ProbableItem> STREAM_CODEC = new StreamCodec<>() {
	@Override
	public ProbableItem decode(RegistryFriendlyByteBuf buf) {
	    return new ProbableItem(ItemStack.STREAM_CODEC.decode(buf), buf.readDouble());
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf, ProbableItem item) {
	    ItemStack.STREAM_CODEC.encode(buf, item.getFullStack());
	    buf.writeDouble(item.getChance());
	}
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ProbableItem>> LIST_STREAM_CODEC = new StreamCodec<>() {
	@Override
	public List<ProbableItem> decode(RegistryFriendlyByteBuf buf) {
	    int count = buf.readInt();
	    List<ProbableItem> items = new ArrayList<>();
	    for (int i = 0; i < count; i++) {
		items.add(STREAM_CODEC.decode(buf));
	    }
	    return items;
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf, List<ProbableItem> probable) {
	    buf.writeInt(probable.size());
	    for (ProbableItem item : probable) {
		STREAM_CODEC.encode(buf, item);
	    }
	}
    };

    public static final List<ProbableItem> NONE = List.of();

    public ProbableItem(ItemStack stack, double chance) {
	super(stack, chance);
    }

    public ItemStack roll() {
	double random = nextRoll();
	if (passesChance(random)) {
	    double amount = isGuaranteed() ? stack.getCount() : stack.getCount() * random;
	    int itemCount = (int) Math.ceil(amount);
	    return new ItemStack(stack.getItem(), itemCount);
	}
	return ItemStack.EMPTY;
    }

}
