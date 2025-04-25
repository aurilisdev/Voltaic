package voltaic.api.radiation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.IRadiationSource;
import voltaic.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;

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

    public static final StreamCodec<ByteBuf, SimpleRadiationSource> STREAM_CODEC = new StreamCodec<ByteBuf, SimpleRadiationSource>() {
		
		@Override
		public void encode(ByteBuf buffer, SimpleRadiationSource value) {
			StreamCodec.DOUBLE.encode(buffer, value.amount);
			StreamCodec.DOUBLE.encode(buffer, value.strength);
			StreamCodec.INT.encode(buffer, value.distance);
			StreamCodec.BOOL.encode(buffer, value.isTemporary);
			StreamCodec.INT.encode(buffer, value.ticks);
			StreamCodec.BLOCK_POS.encode(buffer, value.location);
			StreamCodec.BOOL.encode(buffer, value.shouldLinger);
		}
		
		@Override
		public SimpleRadiationSource decode(ByteBuf buffer) {
			return new SimpleRadiationSource(StreamCodec.DOUBLE.decode(buffer), StreamCodec.DOUBLE.decode(buffer), StreamCodec.INT.decode(buffer), StreamCodec.BOOL.decode(buffer), StreamCodec.INT.decode(buffer), StreamCodec.BLOCK_POS.decode(buffer), StreamCodec.BOOL.decode(buffer));
		}
	};


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
