package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketBuffer;
import voltaic.api.codec.StreamCodec;

public class RadiationShielding {

    public static final RadiationShielding ZERO = new RadiationShielding(0, 0);

    public static final Codec<RadiationShielding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(RadiationShielding::amount),
            Codec.DOUBLE.fieldOf("level").forGetter(RadiationShielding::level)
    ).apply(instance, RadiationShielding::new));

    public static final StreamCodec<PacketBuffer, RadiationShielding> STREAM_CODEC = new StreamCodec<PacketBuffer, RadiationShielding>() {
		
		@Override
		public void encode(PacketBuffer buffer, RadiationShielding value) {
			buffer.writeDouble(value.amount);
			buffer.writeDouble(value.level);
		}
		
		@Override
		public RadiationShielding decode(PacketBuffer buffer) {
			return new RadiationShielding(buffer.readDouble(), buffer.readDouble());
		}
	};
	
	private final double amount;
	private final double level;
	
	public RadiationShielding(double amount, double level) {
		this.amount = amount;
		this.level = level;
	}
	
	public double amount() {
		return amount;
	}
	
	public double level() {
		return level;
	}

}
