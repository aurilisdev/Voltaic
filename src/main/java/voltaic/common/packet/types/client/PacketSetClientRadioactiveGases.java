package voltaic.common.packet.types.client;

import voltaic.api.gas.Gas;
import voltaic.api.gas.GasStack;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class PacketSetClientRadioactiveGases implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETCLIENTRADIOACTIVEGASES_PACKETID = NetworkHandler.id("packetsetclientradioactivegases");
    public static final Type<PacketSetClientRadioactiveGases> TYPE = new Type<>(PACKET_SETCLIENTRADIOACTIVEGASES_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveGases> CODEC = new StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveGases>() {
        @Override
        public PacketSetClientRadioactiveGases decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Gas, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(GasStack.STREAM_CODEC.decode(buf).getGas(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveGases(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PacketSetClientRadioactiveGases packet) {
            buf.writeInt(packet.gases.size());
            packet.gases.forEach((gas, value) -> {
                GasStack.STREAM_CODEC.encode(buf, new GasStack(gas, 1, Gas.ROOM_TEMPERATURE, Gas.PRESSURE_AT_SEA_LEVEL));
                RadioactiveObject.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Gas, RadioactiveObject> gases;

    public PacketSetClientRadioactiveGases(HashMap<Gas, RadioactiveObject> items) {
        this.gases = items;
    }

    public static void handle(PacketSetClientRadioactiveGases message, IPayloadContext context) {
        ClientBarrierMethods.handleSetClientRadioactiveGases(message.gases);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
