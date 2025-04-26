package voltaic.common.packet.types.client;

import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class PacketSetClientRadioactiveItems implements CustomPacketPayload {

	public static final ResourceLocation PACKET_SETCLIENTRADIOACTIVEITEMS_PACKETID = NetworkHandler.id("packetsetclientradioactiveitems");
	public static final Type<PacketSetClientRadioactiveItems> TYPE = new Type<>(PACKET_SETCLIENTRADIOACTIVEITEMS_PACKETID);

	public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveItems> CODEC = new StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveItems>() {
		@Override
		public PacketSetClientRadioactiveItems decode(RegistryFriendlyByteBuf buf) {
			int count = buf.readInt();
			HashMap<Item, RadioactiveObject> values = new HashMap<>();
			for (int i = 0; i < count; i++) {
				values.put(ItemStack.STREAM_CODEC.decode(buf).getItem(), RadioactiveObject.STREAM_CODEC.decode(buf));
			}
			return new PacketSetClientRadioactiveItems(values);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, PacketSetClientRadioactiveItems packet) {
			buf.writeInt(packet.items.size());
			packet.items.forEach((item, value) -> {
				ItemStack.STREAM_CODEC.encode(buf, new ItemStack(item));
				RadioactiveObject.STREAM_CODEC.encode(buf, value);
			});

		}

	};

	private final HashMap<Item, RadioactiveObject> items;

	public PacketSetClientRadioactiveItems(HashMap<Item, RadioactiveObject> items) {
		this.items = items;
	}

	public static void handle(PacketSetClientRadioactiveItems message, IPayloadContext context) {
		ClientBarrierMethods.handleSetClientRadioactiveItems(message.items);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
