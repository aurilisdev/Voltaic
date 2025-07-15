package voltaic.common.packet.types.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;

public class PacketUpdateCariedItemClient {
	
public static final StreamCodec<PacketBuffer, PacketUpdateCariedItemClient> CODEC = new StreamCodec<PacketBuffer, PacketUpdateCariedItemClient>() {
		
		@Override
		public void encode(PacketBuffer buffer, PacketUpdateCariedItemClient value) {
			StreamCodec.ITEM_STACK.encode(buffer, value.carriedItem);
			StreamCodec.BLOCK_POS.encode(buffer, value.tilePos);
			StreamCodec.UUID.encode(buffer, value.playerId);
		}
		
		@Override
		public PacketUpdateCariedItemClient decode(PacketBuffer buffer) {
			return new PacketUpdateCariedItemClient(StreamCodec.ITEM_STACK.decode(buffer), StreamCodec.BLOCK_POS.decode(buffer), StreamCodec.UUID.decode(buffer));
		}
	};

    private final ItemStack carriedItem;
    private final BlockPos tilePos;
    private final UUID playerId;

    public PacketUpdateCariedItemClient(ItemStack carriedItem, BlockPos tilePos, UUID playerId) {
        this.carriedItem = carriedItem;
        this.tilePos = tilePos;
        this.playerId = playerId;
    }
    
    public static void handle(PacketUpdateCariedItemClient message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleUpdateCarriedItemClient(message.carriedItem, message.tilePos, message.playerId);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateCariedItemClient message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketUpdateCariedItemClient decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}

}
