package voltaic.common.packet.types.server;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketUpdateCarriedItemServer {

    public static final StreamCodec<PacketBuffer, PacketUpdateCarriedItemServer> CODEC = new StreamCodec<PacketBuffer, PacketUpdateCarriedItemServer>() {
		
		@Override
		public void encode(PacketBuffer buffer, PacketUpdateCarriedItemServer value) {
			StreamCodec.ITEM_STACK.encode(buffer, value.carriedItem);
			StreamCodec.BLOCK_POS.encode(buffer, value.tilePos);
			StreamCodec.UUID.encode(buffer, value.playerId);
		}
		
		@Override
		public PacketUpdateCarriedItemServer decode(PacketBuffer buffer) {
			return new PacketUpdateCarriedItemServer(StreamCodec.ITEM_STACK.decode(buffer), StreamCodec.BLOCK_POS.decode(buffer), StreamCodec.UUID.decode(buffer));
		}
	};

    private final ItemStack carriedItem;
    private final BlockPos tilePos;
    private final UUID playerId;

    public PacketUpdateCarriedItemServer(ItemStack carriedItem, BlockPos tilePos, UUID playerId) {
        this.carriedItem = carriedItem;
        this.tilePos = tilePos;
        this.playerId = playerId;
    }
    
    public static void handle(PacketUpdateCarriedItemServer message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerBarrierMethods.handleUpdateCarriedItemServer(context.get().getSender().level, message.carriedItem, message.tilePos, message.playerId);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateCarriedItemServer message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketUpdateCarriedItemServer decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
