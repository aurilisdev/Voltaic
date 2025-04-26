package voltaic.common.packet.types.client;

import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class PacketSetClientRadioactiveFluids implements CustomPacketPayload {

    public static final ResourceLocation PACKET_SETCLIENTRADIOACTIVEFLUIDS_PACKETID = NetworkHandler.id("packetsetclientradioactivefluids");
    public static final Type<PacketSetClientRadioactiveFluids> TYPE = new Type<>(PACKET_SETCLIENTRADIOACTIVEFLUIDS_PACKETID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveFluids> CODEC = new StreamCodec<RegistryFriendlyByteBuf, PacketSetClientRadioactiveFluids>() {
        @Override
        public PacketSetClientRadioactiveFluids decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Fluid, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(FluidStack.STREAM_CODEC.decode(buf).getFluid(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveFluids(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PacketSetClientRadioactiveFluids packet) {
            buf.writeInt(packet.fluids.size());
            packet.fluids.forEach((fluid, value) -> {
                FluidStack.STREAM_CODEC.encode(buf, new FluidStack(fluid, 1));
                RadioactiveObject.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Fluid, RadioactiveObject> fluids;

    public PacketSetClientRadioactiveFluids(HashMap<Fluid, RadioactiveObject> items) {
        this.fluids = items;
    }

    public static void handle(PacketSetClientRadioactiveFluids message, IPayloadContext context) {
        ClientBarrierMethods.handleSetClientRadioactiveFluids(message.fluids);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
