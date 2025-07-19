package voltaic.api.radiation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import voltaic.api.radiation.util.IRadiationSource;
import voltaic.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class SimpleRadiationSource implements IRadiationSource {

    public static final SimpleRadiationSource NONE = new SimpleRadiationSource(0, 0, 0, false, 0, BlockEntityUtils.OUT_OF_REACH, false, false);

    public static final Codec<SimpleRadiationSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.DOUBLE.fieldOf("amount").forGetter(SimpleRadiationSource::amount),
            Codec.DOUBLE.fieldOf("strength").forGetter(SimpleRadiationSource::strength),
            Codec.INT.fieldOf("spread").forGetter(SimpleRadiationSource::getDistanceSpread),
            Codec.BOOL.fieldOf("temporary").forGetter(SimpleRadiationSource::isTemporary),
            Codec.INT.fieldOf("persistanceticks").forGetter(SimpleRadiationSource::getPersistanceTicks),
            BlockPos.CODEC.optionalFieldOf("location", BlockEntityUtils.OUT_OF_REACH).forGetter(SimpleRadiationSource::location),
            Codec.BOOL.fieldOf("lingers").forGetter(SimpleRadiationSource::shouldLinger),
            Codec.BOOL.fieldOf("shouldcombine").forGetter(SimpleRadiationSource::shouldCombine)


    ).apply(instance, SimpleRadiationSource::new));

    public static final StreamCodec<ByteBuf, SimpleRadiationSource> STREAM_CODEC = new StreamCodec<ByteBuf, SimpleRadiationSource>() {

        @Override
        public void encode(ByteBuf buffer, SimpleRadiationSource value) {
            ByteBufCodecs.DOUBLE.encode(buffer, value.amount);
            ByteBufCodecs.DOUBLE.encode(buffer, value.strength);
            ByteBufCodecs.INT.encode(buffer, value.distance);
            ByteBufCodecs.BOOL.encode(buffer, value.isTemporary);
            ByteBufCodecs.INT.encode(buffer, value.ticks);
            BlockPos.STREAM_CODEC.encode(buffer, value.location);
            ByteBufCodecs.BOOL.encode(buffer, value.shouldLinger);
            ByteBufCodecs.BOOL.encode(buffer, value.shouldCombine);
        }

        @Override
        public SimpleRadiationSource decode(ByteBuf buffer) {
            return new SimpleRadiationSource(ByteBufCodecs.DOUBLE.decode(buffer), ByteBufCodecs.DOUBLE.decode(buffer), ByteBufCodecs.INT.decode(buffer), ByteBufCodecs.BOOL.decode(buffer), ByteBufCodecs.INT.decode(buffer), BlockPos.STREAM_CODEC.decode(buffer), ByteBufCodecs.BOOL.decode(buffer), ByteBufCodecs.BOOL.decode(buffer));
        }
    };

    private final double amount;
    private final double strength;
    private final int distance;
    private final boolean isTemporary;
    private final int ticks;
    private final BlockPos location;
    private final boolean shouldLinger;
    private final boolean shouldCombine;
    private final AABB boundingBox;
    public final ChunkPos chunkPos;

    public SimpleRadiationSource(double amount, double strength, int distance, boolean isTemporary, int ticks, BlockPos location, boolean shouldLinger, boolean shouldCombine) {
        this.amount = amount;
        this.strength = strength;
        this.distance = distance;
        this.isTemporary = isTemporary;
        this.ticks = ticks;
        this.location = location;
        this.shouldLinger = shouldLinger;
        this.shouldCombine = shouldCombine;
        boundingBox = new AABB(location).inflate(distance);
        this.chunkPos = new ChunkPos(location);
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

    public boolean shouldCombine() {
        return this.shouldCombine;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

}
