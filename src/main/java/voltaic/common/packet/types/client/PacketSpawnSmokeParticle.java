package voltaic.common.packet.types.client;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

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

	public static void encode(PacketSpawnSmokeParticle pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
	}

	public static PacketSpawnSmokeParticle decode(PacketBuffer buf) {
		return new PacketSpawnSmokeParticle(buf.readBlockPos());
	}
}