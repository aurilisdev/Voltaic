package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadioactiveObject;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetClientRadioactiveItems {
	
	public static final StreamCodec<PacketBuffer, PacketSetClientRadioactiveItems> CODEC = new StreamCodec<PacketBuffer, PacketSetClientRadioactiveItems>() {
		@Override
		public PacketSetClientRadioactiveItems decode(PacketBuffer buf) {
			int count = buf.readInt();
			HashMap<Item, RadioactiveObject> values = new HashMap<>();
			for (int i = 0; i < count; i++) {
				values.put(StreamCodec.ITEM_STACK.decode(buf).getItem(), RadioactiveObject.STREAM_CODEC.decode(buf));
			}
			return new PacketSetClientRadioactiveItems(values);
		}

		@Override
		public void encode(PacketBuffer buf, PacketSetClientRadioactiveItems packet) {
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

	public static void encode(PacketSetClientRadioactiveItems message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadioactiveItems decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
