package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketBuffer;
import voltaic.api.codec.StreamCodec;

public class RadioactiveObject {

    public static final RadioactiveObject ZERO = new RadioactiveObject(0, 0);

    public static final Codec<RadioactiveObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("strength").forGetter(RadioactiveObject::strength),
            Codec.DOUBLE.fieldOf("amount").forGetter(RadioactiveObject::amount)
    ).apply(instance, RadioactiveObject::new));

    public static final StreamCodec<PacketBuffer, RadioactiveObject> STREAM_CODEC = new StreamCodec<PacketBuffer, RadioactiveObject>() {
		
		@Override
		public void encode(PacketBuffer buffer, RadioactiveObject value) {
			buffer.writeDouble(value.amount);
			buffer.writeDouble(value.strength);
		}
		
		@Override
		public RadioactiveObject decode(PacketBuffer buffer) {
			return new RadioactiveObject(buffer.readDouble(), buffer.readDouble());
		}
	};
	
	private final double amount;
	private final double strength;
	
	public RadioactiveObject(double strength, double amount) {
		this.strength = strength;
		this.amount = amount;
	}
	
	public double strength() {
		return strength;
	}
	
	public double amount() {
		return amount;
	}

}
