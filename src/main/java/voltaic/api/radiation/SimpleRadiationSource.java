package voltaic.api.radiation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import voltaic.api.radiation.util.IRadiationSource;
import voltaic.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SimpleRadiationSource(double amount, double strength, int distance, boolean isTemporary, int ticks, BlockPos location, boolean shouldLinger) implements IRadiationSource {

    public static final SimpleRadiationSource NONE = new SimpleRadiationSource(0, 0, 0, false, 0, BlockEntityUtils.OUT_OF_REACH, false);

    public static final Codec<SimpleRadiationSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.DOUBLE.fieldOf("amount").forGetter(SimpleRadiationSource::amount),
            Codec.DOUBLE.fieldOf("strength").forGetter(SimpleRadiationSource::strength),
            Codec.INT.fieldOf("spread").forGetter(SimpleRadiationSource::getDistanceSpread),
            Codec.BOOL.fieldOf("temporary").forGetter(SimpleRadiationSource::isTemporary),
            Codec.INT.fieldOf("persistanceticks").forGetter(SimpleRadiationSource::getPersistanceTicks),
            BlockPos.CODEC.fieldOf("location").forGetter(SimpleRadiationSource::location),
            Codec.BOOL.fieldOf("lingers").forGetter(SimpleRadiationSource::shouldLinger)


    ).apply(instance, SimpleRadiationSource::new));

    public static final StreamCodec<ByteBuf, SimpleRadiationSource> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);


    @Override
    public double getRadiationAmount() {
        return amount();
    }

    @Override
    public double getRadiationStrength() {
        return strength();
    }

    @Override
    public int getDistanceSpread() {
        return distance();
    }

    @Override
    public boolean isTemporary() {
        return isTemporary;
    }

    @Override
    public int getPersistanceTicks() {
        return ticks();
    }

    @Override
    public BlockPos getSourceLocation() {
        return location();
    }

    @Override
    public boolean shouldLeaveLingeringSource() {
        return shouldLinger();
    }
}
