package voltaic.common.packet.types.client;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadioactiveObject;

public class PacketSetClientRadioactiveBlocks {

    public static final StreamCodec<PacketBuffer, PacketSetClientRadioactiveBlocks> CODEC = new StreamCodec<PacketBuffer, PacketSetClientRadioactiveBlocks>() {
        @Override
        public PacketSetClientRadioactiveBlocks decode(PacketBuffer buf) {
            int count = buf.readInt();
            HashMap<Block, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(((BlockItem) StreamCodec.ITEM_STACK.decode(buf).getItem()).getBlock(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveBlocks(values);
        }

        @Override
        public void encode(PacketBuffer buf, PacketSetClientRadioactiveBlocks packet) {
            buf.writeInt(packet.blocks.size());
            packet.blocks.forEach((block, value) -> {
                StreamCodec.ITEM_STACK.encode(buf, new ItemStack(block));
                RadioactiveObject.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Block, RadioactiveObject> blocks;

    public PacketSetClientRadioactiveBlocks(HashMap<Block, RadioactiveObject> blocks) {
        this.blocks = blocks;
    }

    public static void handle(PacketSetClientRadioactiveBlocks message, Supplier<Context> context) {
    	Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleSetClientRadioactiveBlocks(message.blocks);
		});
		ctx.setPacketHandled(true);
    }

    public static void encode(PacketSetClientRadioactiveBlocks packet, PacketBuffer buf) {
    	CODEC.encode(buf, packet);
    }
    
    public static PacketSetClientRadioactiveBlocks decode(PacketBuffer buf) {
    	return CODEC.decode(buf);
    }
    
}
