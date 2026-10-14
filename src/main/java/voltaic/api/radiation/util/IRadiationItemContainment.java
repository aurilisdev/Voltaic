package voltaic.api.radiation.util;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** An item may explicitly attenuate its contents before exposure is calculated. */
public interface IRadiationItemContainment {
    /** Fraction that escapes, from zero to one; does not shield the item's own material. */
    double getEscapingFraction(ItemStack stack, @Nullable Level level);
}
