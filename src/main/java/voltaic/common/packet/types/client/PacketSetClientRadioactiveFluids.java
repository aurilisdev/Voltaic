package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadioactiveObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketSetClientRadioactiveFluids {

    public static final StreamCodec<PacketBuffer, PacketSetClientRadioactiveFluids> CODEC = new StreamCodec<PacketBuffer, PacketSetClientRadioactiveFluids>() {
        @Override
        public PacketSetClientRadioactiveFluids decode(PacketBuffer buf) {
            int count = buf.readInt();
            HashMap<Fluid, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(StreamCodec.FLUID_STACK.decode(buf).getFluid(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveFluids(values);
        }

        @Override
        public void encode(PacketBuffer buf, PacketSetClientRadioactiveFluids packet) {
            buf.writeInt(packet.fluids.size());
            packet.fluids.forEach((fluid, value) -> {
                StreamCodec.FLUID_STACK.encode(buf, new FluidStack(fluid, 1));
                RadioactiveObject.STREAM_CODEC.encode(buf, value);
            });

        }

    };

    private final HashMap<Fluid, RadioactiveObject> fluids;

    public PacketSetClientRadioactiveFluids(HashMap<Fluid, RadioactiveObject> items) {
        this.fluids = items;
    }

    public static void handle(PacketSetClientRadioactiveFluids message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleSetClientRadioactiveFluids(message.fluids);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetClientRadioactiveFluids message, PacketBuffer buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadioactiveFluids decode(PacketBuffer buf) {
		return CODEC.decode(buf);
	}
}
