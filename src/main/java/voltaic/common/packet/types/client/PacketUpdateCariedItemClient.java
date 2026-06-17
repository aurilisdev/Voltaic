package voltaic.common.packet.types.client;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import voltaic.common.packet.NetworkHandler;

public class PacketUpdateCariedItemClient implements CustomPacketPayload {

    public static final ResourceLocation PACKET_UPDATECARRIEDITEMCLIENT_PACKETID = NetworkHandler
	    .id("packetupdatecarrieditemclient");
    public static final Type<PacketUpdateCariedItemClient> TYPE = new Type<>(PACKET_UPDATECARRIEDITEMCLIENT_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateCariedItemClient> CODEC = new StreamCodec<>() {

	@Override
	public void encode(RegistryFriendlyByteBuf buffer, PacketUpdateCariedItemClient value) {
	    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, value.carriedItem);
	    BlockPos.STREAM_CODEC.encode(buffer, value.tilePos);
	    UUIDUtil.STREAM_CODEC.encode(buffer, value.playerId);
	}

	@Override
	public PacketUpdateCariedItemClient decode(RegistryFriendlyByteBuf buffer) {
	    return new PacketUpdateCariedItemClient(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer),
		    BlockPos.STREAM_CODEC.decode(buffer), UUIDUtil.STREAM_CODEC.decode(buffer));
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

    public static void handle(PacketUpdateCariedItemClient message, IPayloadContext context) {
	ClientBarrierMethods.handleUpdateCarriedItemClient(message.carriedItem, message.tilePos, message.playerId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
	return TYPE;
    }
}
