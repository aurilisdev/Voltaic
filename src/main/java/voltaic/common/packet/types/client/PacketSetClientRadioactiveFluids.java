package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.RadioactiveObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.HashMap;
import java.util.function.Supplier;

public class PacketSetClientRadioactiveFluids {

    public static final StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveFluids> CODEC = new StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveFluids>() {
        @Override
        public PacketSetClientRadioactiveFluids decode(FriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Fluid, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(StreamCodec.FLUID_STACK.decode(buf).getFluid(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveFluids(values);
        }

        @Override
        public void encode(FriendlyByteBuf buf, PacketSetClientRadioactiveFluids packet) {
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

	public static void encode(PacketSetClientRadioactiveFluids message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadioactiveFluids decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
