package voltaic.api.radiation.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/**
 * Reads the physical item contents of a block entity for radiation purposes.
 * <p>
 * Readers are consulted in registration order before the ordinary inventory
 * reader; the first reader that returns <code>true</code> handles the holder and
 * no other reader runs, so one holder is processed exactly once. Quantities are
 * reported as <code>long</code> per item type; never create oversized stacks or
 * iterate once per stored item.
 */
@FunctionalInterface
public interface IRadiationContentsReader {

    /**
     * @return true if this reader handled the block entity (even if it contained
     *         nothing radioactive)
     */
    boolean read(BlockEntity blockEntity, ItemEmissionAccumulator contents);
}
