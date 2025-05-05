package voltaic.api.radiation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import voltaic.api.codec.StreamCodec;
import voltaic.api.radiation.util.IRadiationSource;
import voltaic.prefab.utilities.BlockEntityUtils;

public class SimpleRadiationSource implements IRadiationSource {

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

    public static final StreamCodec<PacketBuffer, SimpleRadiationSource> STREAM_CODEC = new StreamCodec<PacketBuffer, SimpleRadiationSource>() {
		
		@Override
		public void encode(PacketBuffer buffer, SimpleRadiationSource value) {
			StreamCodec.DOUBLE.encode(buffer, value.amount);
			StreamCodec.DOUBLE.encode(buffer, value.strength);
			StreamCodec.INT.encode(buffer, value.distance);
			StreamCodec.BOOL.encode(buffer, value.isTemporary);
			StreamCodec.INT.encode(buffer, value.ticks);
			StreamCodec.BLOCK_POS.encode(buffer, value.location);
			StreamCodec.BOOL.encode(buffer, value.shouldLinger);
		}
		
		@Override
		public SimpleRadiationSource decode(PacketBuffer buffer) {
			return new SimpleRadiationSource(StreamCodec.DOUBLE.decode(buffer), StreamCodec.DOUBLE.decode(buffer), StreamCodec.INT.decode(buffer), StreamCodec.BOOL.decode(buffer), StreamCodec.INT.decode(buffer), StreamCodec.BLOCK_POS.decode(buffer), StreamCodec.BOOL.decode(buffer));
		}
	};
	
	private final double amount;
	private final double strength;
	private final int distance;
	private final boolean isTemporary;
	private final int ticks;
	private final BlockPos location;
	private final boolean shouldLinger;
	
	public SimpleRadiationSource(double amount, double strength, int distance, boolean isTemporary, int ticks, BlockPos location, boolean shouldLinger) {
		this.amount = amount;
		this.strength = strength;
		this.distance = distance;
		this.isTemporary = isTemporary;
		this.ticks = ticks;
		this.location = location;
		this.shouldLinger = shouldLinger;
	}

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
    
    public double amount() {
    	return amount;
    }
    
    public double strength() {
    	return strength;
    }
    
    public int distance() {
    	return distance;
    }
    
    public int ticks() {
    	return ticks;
    }
    
    public BlockPos location() {
    	return location;
    }
    
    public boolean shouldLinger() {
    	return shouldLinger;
    }

}
