package voltaic.common.packet.types.server;

//import electrodynamics.prefab.properties.PropertyManager.PropertyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import voltaic.common.packet.NetworkHandler;

public class PacketSendUpdatePropertiesServer implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SENDUPDATEPROPERTIESSERVER_PACKETID = NetworkHandler
	    .id("packetsendupdatepropertiesserver");
    public static final Type<PacketSendUpdatePropertiesServer> TYPE = new Type<>(
	    PACKET_SENDUPDATEPROPERTIESSERVER_PACKETID);
    public static final StreamCodec<FriendlyByteBuf, PacketSendUpdatePropertiesServer> CODEC = new StreamCodec<>() {

	@Override
	public void encode(FriendlyByteBuf buf, PacketSendUpdatePropertiesServer packet) {
	    buf.writeNbt(packet.data);
	    buf.writeInt(packet.index);
	    buf.writeBlockPos(packet.tilePos);

	}

	@Override
	public PacketSendUpdatePropertiesServer decode(FriendlyByteBuf buf) {
	    return new PacketSendUpdatePropertiesServer(
		    buf.readNbt() instanceof CompoundTag tag ? tag : new CompoundTag(), buf.readInt(),
		    buf.readBlockPos());
	}
    };

    private final BlockPos tilePos;
    private final int index;
    private final CompoundTag data;

    public PacketSendUpdatePropertiesServer(CompoundTag data, int index, BlockPos tilePos) {
	this.tilePos = tilePos;
	this.index = index;
	this.data = data;
    }

    public static void handle(PacketSendUpdatePropertiesServer message, IPayloadContext context) {
	if (context.player().level() instanceof ServerLevel serverLevel) {
	    ServerBarrierMethods.handleSendUpdatePropertiesServer(serverLevel, message.tilePos, message.data,
		    message.index);
	}
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}
