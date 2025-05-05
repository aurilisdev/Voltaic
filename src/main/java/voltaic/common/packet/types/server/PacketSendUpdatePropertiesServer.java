package voltaic.common.packet.types.server;

import voltaic.api.codec.StreamCodec;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSendUpdatePropertiesServer {
	
    public static final StreamCodec<PacketBuffer, PacketSendUpdatePropertiesServer> CODEC = new StreamCodec<PacketBuffer, PacketSendUpdatePropertiesServer>() {

        @Override
        public void encode(PacketBuffer buf, PacketSendUpdatePropertiesServer packet) {
            //buf.writeInt(packet.wrapper.index());
            //buf.writeResourceLocation(packet.wrapper.type().getId());
            //packet.wrapper.type().getPacketCodec().encode(buf, packet.wrapper.value());

            buf.writeNbt(packet.data);
            buf.writeInt(packet.index);
            buf.writeBlockPos(packet.tilePos);

        }

        @Override
        public PacketSendUpdatePropertiesServer decode(PacketBuffer buf) {
            return new PacketSendUpdatePropertiesServer(buf.readNbt(), buf.readInt(), buf.readBlockPos());
            //int index = buf.readInt();
            //IPropertyType type = PropertyManager.REGISTERED_PROPERTIES.get(buf.readResourceLocation());

            //return new PacketSendUpdatePropertiesServer(new PropertyWrapper(index, type, type.getPacketCodec().decode(buf), null), buf.readBlockPos());
        }
    };

    private final BlockPos tilePos;

    private int index;

    private CompoundNBT data;
    //private final PropertyWrapper wrapper;

    public PacketSendUpdatePropertiesServer(CompoundNBT data, int index, BlockPos tilePos) {
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
			ServerWorld world = context.get().getSender().getLevel();
			if (world != null) {
				ServerBarrierMethods.handleSendUpdatePropertiesServer(world, message.tilePos, message.data, message.index);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSendUpdatePropertiesServer message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSendUpdatePropertiesServer decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
