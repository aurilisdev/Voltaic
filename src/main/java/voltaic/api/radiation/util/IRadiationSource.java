package voltaic.api.radiation.util;

import net.minecraft.util.math.BlockPos;

/**
 *  Based on IRadioactiveObject class initially developed by AurilisDev
 *
 * A radiation source is defined as a constant source of radiation. When the source is added to the manager,
 * the manager will continually apply its radiation to the surrounding area.
 *
 */
public interface IRadiationSource {

    /**
     * The amount of radiation in rads that this radiation source will emit
     *
     * @return Radiation in Rads
     */
    double getRadiationAmount();

    /**
     * The strength of this radiation source. The higher the value, the more blocks the radiation will be able to
     * penetrate.
     *
     * @return
     */
    double getRadiationStrength();

    /**
     * The radius a radiation source will emit to
     * @return
     */
    int getDistanceSpread();


    /**
     * This method is used by the radiation manager to determine if this source is temporary or not. If true
     * is returned, then the manager will handle removing this object from the list of active radiation sources
     * once its persistance time has expired. If false is returned, it is the responsibility of this object to
     * remove itself from the manager.
     *
     * @return Whether this radiation source is temporary
     */
    default boolean isTemporary() {
        return true;
    }

    /**
     * This value will be checked if #isTemporary() returns true. Otherwise it will be checked
     *
     * @return How long in game ticks this radiation source will persist for the manager
     */
    default int getPersistanceTicks() {
        return 1;
    }

    BlockPos getSourceLocation();

    boolean shouldLeaveLingeringSource();


}
