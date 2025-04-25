package voltaic.common.packet.types.client;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSpawnSmokeParticle {

	private final BlockPos pos;

	public PacketSpawnSmokeParticle(BlockPos pos) {
		this.pos = pos;
	}

	public static void handle(PacketSpawnSmokeParticle message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handlerSpawnSmokeParicle(message.pos);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSpawnSmokeParticle pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
	}

	public static PacketSpawnSmokeParticle decode(FriendlyByteBuf buf) {
		return new PacketSpawnSmokeParticle(buf.readBlockPos());
	}
}