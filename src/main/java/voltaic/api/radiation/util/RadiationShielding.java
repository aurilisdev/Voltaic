package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import voltaic.api.codec.StreamCodec;

public record RadiationShielding(double amount, double level) {

    public static final RadiationShielding ZERO = new RadiationShielding(0, 0);

    public static final Codec<RadiationShielding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(RadiationShielding::amount),
            Codec.DOUBLE.fieldOf("level").forGetter(RadiationShielding::level)
    ).apply(instance, RadiationShielding::new));

    public static final StreamCodec<ByteBuf, RadiationShielding> STREAM_CODEC = new StreamCodec<ByteBuf, RadiationShielding>() {
		
		@Override
		public void encode(ByteBuf buffer, RadiationShielding value) {
			buffer.writeDouble(value.amount);
			buffer.writeDouble(value.level);
		}
		
		@Override
		public RadiationShielding decode(ByteBuf buffer) {
			return new RadiationShielding(buffer.readDouble(), buffer.readDouble());
		}
	};

}
