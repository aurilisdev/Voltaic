package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import voltaic.api.codec.StreamCodec;

public record RadioactiveObject(double strength, double amount) {

    public static final RadioactiveObject ZERO = new RadioactiveObject(0, 0);

    public static final Codec<RadioactiveObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("strength").forGetter(RadioactiveObject::strength),
            Codec.DOUBLE.fieldOf("amount").forGetter(RadioactiveObject::amount)
    ).apply(instance, RadioactiveObject::new));

    public static final StreamCodec<ByteBuf, RadioactiveObject> STREAM_CODEC = new StreamCodec<ByteBuf, RadioactiveObject>() {
		
		@Override
		public void encode(ByteBuf buffer, RadioactiveObject value) {
			buffer.writeDouble(value.amount);
			buffer.writeDouble(value.strength);
		}
		
		@Override
		public RadioactiveObject decode(ByteBuf buffer) {
			return new RadioactiveObject(buffer.readDouble(), buffer.readDouble());
		}
	};

}
