package voltaic.api.radiation.util;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/**
 * Reads actual stored items, never filters, recipes or a remote network view.
 * The first matching reader owns the contents of this item. The accumulator
 * supplies nesting limits, quantity multiplication and backing store deduplication.
 * Readers returning false must leave the accumulator unchanged.
 */
@FunctionalInterface
public interface IRadiationItemContentsReader {
    boolean read(ItemStack stack, @Nullable Level level, ItemEmissionAccumulator contents);
}
