package voltaic.common.packet.types.client;

import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.packet.NetworkHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class PacketSetClientRadiationShielding implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETCLIENTRADIATIONSHIELDING_PACKETID = NetworkHandler.id("packetsetclientradiationshielding");
    public static final Type<PacketSetClientRadiationShielding> TYPE = new Type<>(PACKET_SETCLIENTRADIATIONSHIELDING_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadiationShielding> CODEC = new StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadiationShielding>() {
        @Override
        public PacketSetClientRadiationShielding decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Block, RadiationShielding> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(ByteBufCodecs.fromCodec(BuiltInRegistries.BLOCK.byNameCodec()).decode(buf), RadiationShielding.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadiationShielding(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PacketSetClientRadiationShielding packet) {
            buf.writeInt(packet.shielding.size());
            packet.shielding.forEach((block, value) -> {
                ByteBufCodecs.fromCodec(BuiltInRegistries.BLOCK.byNameCodec()).encode(buf, block);
                RadiationShielding.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Block, RadiationShielding> shielding;

    public PacketSetClientRadiationShielding(HashMap<Block, RadiationShielding> shielding) {
        this.shielding = shielding;
    }

    public static void handle(PacketSetClientRadiationShielding message, IPayloadContext context) {
        ClientBarrierMethods.handleSetClientRadiationShielding(message.shielding);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
