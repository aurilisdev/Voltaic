package voltaic.common.packet.types.server;

import voltaic.api.codec.StreamCodec;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSendUpdatePropertiesServer {
	
    public static final StreamCodec<FriendlyByteBuf, PacketSendUpdatePropertiesServer> CODEC = new StreamCodec<>() {

        @Override
        public void encode(FriendlyByteBuf buf, PacketSendUpdatePropertiesServer packet) {
            //buf.writeInt(packet.wrapper.index());
            //buf.writeResourceLocation(packet.wrapper.type().getId());
            //packet.wrapper.type().getPacketCodec().encode(buf, packet.wrapper.value());

            buf.writeNbt(packet.data);
            buf.writeInt(packet.index);
            buf.writeBlockPos(packet.tilePos);

        }

        @Override
        public PacketSendUpdatePropertiesServer decode(FriendlyByteBuf buf) {
            return new PacketSendUpdatePropertiesServer(buf.readNbt(), buf.readInt(), buf.readBlockPos());
            //int index = buf.readInt();
            //IPropertyType type = PropertyManager.REGISTERED_PROPERTIES.get(buf.readResourceLocation());

            //return new PacketSendUpdatePropertiesServer(new PropertyWrapper(index, type, type.getPacketCodec().decode(buf), null), buf.readBlockPos());
        }
    };

    private final BlockPos tilePos;

    private int index;

    private CompoundTag data;
    //private final PropertyWrapper wrapper;

    public PacketSendUpdatePropertiesServer(CompoundTag data, int index, BlockPos tilePos) {
        this.tilePos = tilePos;
        this.index = index;
        this.data = data;
        //wrapper = new PropertyWrapper(property.getIndex(), property.getType(), property.get(), property);
    }

    /*
    public PacketSendUpdatePropertiesServer(PropertyWrapper property, BlockPos tilePos) {
        this.tilePos = tilePos;
        wrapper = property;
    }

     */

    public static void handle(PacketSendUpdatePropertiesServer message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().serverLevel();
			if (world != null) {
				ServerBarrierMethods.handleSendUpdatePropertiesServer(world, message.tilePos, message.data, message.index);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSendUpdatePropertiesServer message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSendUpdatePropertiesServer decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
