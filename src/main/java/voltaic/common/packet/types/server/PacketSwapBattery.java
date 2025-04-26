package voltaic.common.packet.types.server;

import java.util.UUID;
import java.util.function.Supplier;

import voltaic.api.codec.StreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSwapBattery {

    public static final StreamCodec<ByteBuf, PacketSwapBattery> CODEC = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, PacketSwapBattery value) {
			StreamCodec.UUID.encode(buffer, value.playerId);
		}

		@Override
		public PacketSwapBattery decode(ByteBuf buffer) {
			return new PacketSwapBattery(StreamCodec.UUID.decode(buffer));
		}
    	
    };

    private final UUID playerId;

    public PacketSwapBattery(UUID uuid) {
        playerId = uuid;
    }

    public static void handle(PacketSwapBattery message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().serverLevel();
			if (world != null) {
				ServerBarrierMethods.handleSwapBattery(world, message.playerId);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSwapBattery message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSwapBattery decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
