package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadiationShielding;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetClientRadiationShielding {

	public static final StreamCodec<PacketBuffer, PacketSetClientRadiationShielding> CODEC = new StreamCodec<PacketBuffer, PacketSetClientRadiationShielding>() {
		@Override
		public PacketSetClientRadiationShielding decode(PacketBuffer buf) {
			int count = buf.readInt();
			HashMap<Block, RadiationShielding> values = new HashMap<>();
			for (int i = 0; i < count; i++) {
				values.put(StreamCodec.BLOCK.decode(buf), RadiationShielding.STREAM_CODEC.decode(buf));
			}
			return new PacketSetClientRadiationShielding(values);
		}

		@Override
		public void encode(PacketBuffer buf, PacketSetClientRadiationShielding packet) {
			buf.writeInt(packet.shielding.size());
			packet.shielding.forEach((block, value) -> {
				StreamCodec.BLOCK.encode(buf, block);
				RadiationShielding.STREAM_CODEC.encode(buf, value);
			});

		}

	};

	private final HashMap<Block, RadiationShielding> shielding;

	public PacketSetClientRadiationShielding(HashMap<Block, RadiationShielding> shielding) {
		this.shielding = shielding;
	}

	public static void handle(PacketSetClientRadiationShielding message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleSetClientRadiationShielding(message.shielding);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetClientRadiationShielding message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadiationShielding decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}

}
