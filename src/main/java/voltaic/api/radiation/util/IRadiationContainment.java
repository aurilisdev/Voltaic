package voltaic.api.radiation.util;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Implemented by a block (or block entity) that deliberately contains the
 * radiation of its own contents, such as a purpose built shielded container.
 * <p>
 * Ordinary storage does not implement this and emits normally. The factor is
 * applied exactly once, to the emitted amount, before world shielding; blocks
 * are never assumed to be radiation proof from their appearance.
 */
public interface IRadiationContainment {

    /**
     * @return the fraction of the contents' radiation that escapes, from 0 (fully
     *         contained) to 1 (no containment)
     */
    double getEscapingFraction(BlockEntity container);
}
