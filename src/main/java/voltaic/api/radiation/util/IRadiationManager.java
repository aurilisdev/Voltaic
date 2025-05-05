package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.api.radiation.SimpleRadiationSource;

import java.util.List;

/**
 * An abstraction and refactor of Radiation System by AurilisDev
 * <p>
 * Implementations of this are capability-based and will handle the distribution of radiation to radiation recipients
 * within its perview. The assumption is that implementations of this will use ray casting to apply radiation effects
 * to surrounding recipients, however the logic is left to the implementer. It is also assumed this manager will be
 * ticked to perform its logic.
 *
 */
public interface IRadiationManager {

    /**
     * A list of all permanent radiation sources currently handled by this manager. You should not
     * remove items from this list.
     *
     * @return
     */
    public List<SimpleRadiationSource> getPermanentSources(World world);

    public List<TemporaryRadiationSource> getTemporarySources(World world);

    public List<FadingRadiationSource> getFadingSources(World world);

    public List<BlockPos> getPermanentLocations(World world);

    public List<BlockPos> getTemporaryLocations(World world);

    public List<BlockPos> getFadingLocations(World world);

    /**
     * Adds a radiation source to this manager. The manager will then categorize it accordingly
     *
     * @param source
     */
    public void addRadiationSource(SimpleRadiationSource source, World level);

    public int getReachOfSource(World world, BlockPos pos);

    /**
     * The default rate at which this manager will disipate any FadingRadiationSources
     *
     * @param radiationDisipation
     */
    public void setDisipation(double radiationDisipation, World level);

    public void setLocalizedDisipation(double disipation, BlockPosVolume area, World level);

    public void removeLocalizedDisipation(BlockPosVolume area, World level);

    /**
     * Removes a radiation source from this manager
     *
     * @param pos                     The location of the source
     * @param shouldLeaveFadingSource Whether the removed source should leave behind a fading radiation source
     * @return
     */
    public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, World level);

    public void wipeAllSources(World level);


    /**
     * Returns whether the handler has changed during the tick
     *
     * @param world
     * @return
     */
    public void tick(World world);
    
    CompoundNBT toTag();
    
    void fromTag(CompoundNBT tag);


    /**
     * A wrapper class that represents a radiation source that has been removed. Just because the source
     * of radiation itself is gone doesn't mean the radiation immediately disipates.
     *
     * @author skip999
     */
    public static class FadingRadiationSource {

        public static final FadingRadiationSource NONE = new FadingRadiationSource(0, 0, 0);

        public static final Codec<FadingRadiationSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("distance").forGetter(instance0 -> instance0.distance),
                Codec.DOUBLE.fieldOf("strength").forGetter(instance0 -> instance0.strength),
                Codec.DOUBLE.fieldOf("amount").forGetter(instance0 -> instance0.radiation)

        ).apply(instance, FadingRadiationSource::new));


        public final int distance;
        public double strength;
        public double radiation;

        public FadingRadiationSource(int distance, double strength, double radiation) {
            this.distance = distance;
            this.strength = strength;
            this.radiation = radiation;
        }

    }

    public static class TemporaryRadiationSource {

        public static final TemporaryRadiationSource NONE = new TemporaryRadiationSource(0, 0, 0, false, 0);

        public static final Codec<TemporaryRadiationSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("ticks").forGetter(instance0 -> instance0.ticks),
                Codec.DOUBLE.fieldOf("strength").forGetter(instance0 -> instance0.strength),
                Codec.DOUBLE.fieldOf("amount").forGetter(instance0 -> instance0.radiation),
                Codec.BOOL.fieldOf("leavefading").forGetter(instance0 -> instance0.leaveFading),
                Codec.INT.fieldOf("distance").forGetter(instance0 -> instance0.distance)

        ).apply(instance, TemporaryRadiationSource::new));

        public int ticks;
        public final double strength;
        public final double radiation;
        public final boolean leaveFading;
        public final int distance;

        public TemporaryRadiationSource(int ticks, double strength, double radiation, boolean leaveFading, int distance) {
            this.ticks = ticks;
            this.strength = strength;
            this.radiation = radiation;
            this.leaveFading = leaveFading;
            this.distance = distance;
        }


    }


}
