package voltaic.common.packet.types.client;

import voltaic.api.codec.StreamCodec;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasStack;
import voltaic.api.radiation.util.RadioactiveObject;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketSetClientRadioactiveGases {

    public static final StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveGases> CODEC = new StreamCodec<FriendlyByteBuf, PacketSetClientRadioactiveGases>() {
        @Override
        public PacketSetClientRadioactiveGases decode(FriendlyByteBuf buf) {
            int count = buf.readInt();
            HashMap<Gas, RadioactiveObject> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                values.put(GasStack.STREAM_CODEC.decode(buf).getGas(), RadioactiveObject.STREAM_CODEC.decode(buf));
            }
            return new PacketSetClientRadioactiveGases(values);
        }

        @Override
        public void encode(FriendlyByteBuf buf, PacketSetClientRadioactiveGases packet) {
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

    public static void handle(PacketSetClientRadioactiveGases message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientBarrierMethods.handleSetClientRadioactiveGases(message.gases);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketSetClientRadioactiveGases message, FriendlyByteBuf buf) {
		CODEC.encode(buf, message);
	}

	public static PacketSetClientRadioactiveGases decode(FriendlyByteBuf buf) {
		return CODEC.decode(buf);
	}
}
