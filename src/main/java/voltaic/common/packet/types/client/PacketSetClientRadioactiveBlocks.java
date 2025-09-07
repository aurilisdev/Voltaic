package voltaic.common.packet.types.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.NetworkHandler;

import java.util.HashMap;

public class PacketSetClientRadioactiveBlocks implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETCLIENTRADIOACTIVEBLOCKS_PACKETID = NetworkHandler.id("packetsetclientradioactiveblocks");
    public static final Type<PacketSetClientRadioactiveBlocks> TYPE = new Type<>(PACKET_SETCLIENTRADIOACTIVEBLOCKS_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveBlocks> CODEC = new StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveBlocks>() {
        @Override
        public PacketSetClientRadioactiveBlocks decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Block, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(((BlockItem) ItemStack.STREAM_CODEC.decode(buf).getItem()).getBlock(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveBlocks(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PacketSetClientRadioactiveBlocks packet) {
            buf.writeInt(packet.blocks.size());
            packet.blocks.forEach((block, value) -> {
                ItemStack.STREAM_CODEC.encode(buf, new ItemStack(block));
                RadioactiveObject.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Block, RadioactiveObject> blocks;

    public PacketSetClientRadioactiveBlocks(HashMap<Block, RadioactiveObject> blocks) {
        this.blocks = blocks;
    }

    public static void handle(PacketSetClientRadioactiveBlocks message, IPayloadContext context) {
        ClientBarrierMethods.handleSetClientRadioactiveBlocks(message.blocks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
