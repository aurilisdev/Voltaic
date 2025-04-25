package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadioactiveObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketSetClientRadioactiveItems {
	
	public static final StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveItems> CODEC = new StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveItems>() {
		@Override
		public PacketSetClientRadioactiveItems decode(FriendlyByteBuf buf) {
			int count = buf.readInt();
			HashMap<Item, RadioactiveObject> values = new HashMap<>();
			for (int i = 0; i < count; i++) {
				values.put(StreamCodec.ITEM_STACK.decode(buf).getItem(), RadioactiveObject.STREAM_CODEC.decode(buf));
			}
			return new PacketSetClientRadioactiveItems(values);
		}

		@Override
		public void encode(FriendlyByteBuf buf, PacketSetClientRadioactiveItems packet) {
			buf.writeInt(packet.items.size());
			packet.items.forEach((item, value) -> {
				StreamCodec.ITEM_STACK.encode(buf, new ItemStack(item));
				RadioactiveObject.STREAM_CODEC.encode(buf, value);
			});

		}

	};

	private final HashMap<Item, RadioactiveObject> items;

	public PacketSetClientRadioactiveItems(HashMap<Item, RadioactiveObject> items) {
		this.items = items;
	}

	public static void handle(PacketSetClientRadioactiveItems message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleSetClientRadioactiveItems(message.items);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetClientRadioactiveItems message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadioactiveItems decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
