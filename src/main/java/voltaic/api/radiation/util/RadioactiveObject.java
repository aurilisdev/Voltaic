package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RadioactiveObject(double strength, double amount) {

    public static final RadioactiveObject ZERO = new RadioactiveObject(0, 0);

    public static final Codec<RadioactiveObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("strength").forGetter(RadioactiveObject::strength),
            Codec.DOUBLE.fieldOf("amount").forGetter(RadioactiveObject::amount)
    ).apply(instance, RadioactiveObject::new));

    public static final StreamCodec<ByteBuf, RadioactiveObject> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

}
