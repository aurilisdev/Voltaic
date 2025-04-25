package voltaic.common.packet.types.server;

import java.util.UUID;
import java.util.function.Supplier;

import voltaic.api.codec.StreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateCarriedItemServer {

    public static final StreamCodec<FriendlyByteBuf, PacketUpdateCarriedItemServer> CODEC = new StreamCodec<FriendlyByteBuf, PacketUpdateCarriedItemServer>() {
		
		@Override
		public void encode(FriendlyByteBuf buffer, PacketUpdateCarriedItemServer value) {
			StreamCodec.ITEM_STACK.encode(buffer, value.carriedItem);
			StreamCodec.BLOCK_POS.encode(buffer, value.tilePos);
			StreamCodec.UUID.encode(buffer, value.playerId);
		}
		
		@Override
		public PacketUpdateCarriedItemServer decode(FriendlyByteBuf buffer) {
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
			ServerBarrierMethods.handleUpdateCarriedItemServer(context.get().getSender().level(), message.carriedItem, message.tilePos, message.playerId);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateCarriedItemServer message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketUpdateCarriedItemServer decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
