package voltaic.prefab.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.api.radiation.util.IRadiationItemContentsReader;
import voltaic.api.radiation.util.IRadiationItemContainment;
import voltaic.common.settings.VoltaicConfig;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.type.ComponentFluidHandlerMulti;
import voltaic.prefab.tile.components.type.ComponentFluidHandlerSimple;
import voltaic.prefab.tile.components.type.ComponentGasHandlerMulti;
import voltaic.prefab.tile.components.type.ComponentGasHandlerSimple;
import voltaic.prefab.tile.components.type.ComponentInventory;

public class RadiationUtils {

    /**
     * Result of an item emission calculation. Radiation is summed by quantity,
     * strength is the penetration of the strongest material.
     */
    public record ItemEmission(double amount, double strength, int range) {

	public static final ItemEmission NONE = new ItemEmission(0, 0, 0);

	public boolean isRadioactive() {
	    return amount > 0;
	}
    }

    /**
     * Collects item types with <code>long</code> quantities so that large stores
     * are processed once per item type rather than once per item. Quantities are
     * promoted to <code>double</code> before multiplication and never converted to
     * <code>int</code>.
     */
    public static class ItemEmissionAccumulator {

        public static final int MAX_NESTING_DEPTH = 3;
        public static final int MAX_NESTED_STACKS = 1024;

        private static final class ReadState {
            private int work;
            private final Set<Object> stores = new HashSet<>();
        }

        @Nullable
        private final Level level;
        private final ReadState state;
        private final int depth;
        private double radiation;
        private double strength;

        public ItemEmissionAccumulator() {
            this(null);
        }

        public ItemEmissionAccumulator(@Nullable Level level) {
            this(level, new ReadState(), 0);
        }

        private ItemEmissionAccumulator(@Nullable Level level, ReadState state, int depth) {
            this.level = level;
            this.state = state;
            this.depth = depth;
        }

        public ItemEmissionAccumulator add(ItemStack stack) {
            return add(stack, stack.getCount());
        }

        /**
         * Adds a component-bearing item type with a long quantity. Quantity is
         * independent of the sample stack's count, so network counts never need
         * to be squeezed into an ItemStack.
         */
        public ItemEmissionAccumulator add(ItemStack stack, long quantity) {
            if (stack.isEmpty() || quantity <= 0 || (depth > 0 && state.work++ >= MAX_NESTED_STACKS)) {
                return this;
            }
            add(stack.getItem(), quantity);
            if (depth >= MAX_NESTING_DEPTH || state.work >= MAX_NESTED_STACKS) {
                return this;
            }
            ItemEmissionAccumulator nested = new ItemEmissionAccumulator(level, state, depth + 1);
            boolean handled = false;
            for (IRadiationItemContentsReader reader : ITEM_READERS) {
                if (reader.read(stack, level, nested)) {
                    handled = true;
                    break;
                }
            }
            if (!handled) {
                ItemContainerContents container = stack.get(DataComponents.CONTAINER);
                if (container != null) {
                    for (ItemStack inner : container.nonEmptyItems()) {
                        if (!nested.hasRemainingWork()) break;
                        nested.add(inner);
                    }
                } else {
                    BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
                    if (bundle != null) {
                        for (ItemStack inner : bundle.items()) {
                            if (!nested.hasRemainingWork()) break;
                            nested.add(inner);
                        }
                    }
                }
            }
            double escaping = stack.getItem() instanceof IRadiationItemContainment containment
                    ? clampEscapingFraction(containment.getEscapingFraction(stack, level)) : 1;
            double amount = saturatedMultiply(saturatedMultiply(nested.radiation, escaping), quantity);
            radiation = saturatedAdd(radiation, amount);
            if (amount > 0) strength = Math.max(strength, nested.strength);
            return this;
        }

        /** Bounded nested traversal; top level stores still visit every item type. */
        public boolean hasRemainingWork() {
            return depth == 0 || state.work < MAX_NESTED_STACKS;
        }

        /**
         * Reads a backing store at most once in this calculation. Use a stable
         * reference (for example an RS storage UUID), never a network identity.
         */
        public void readStorage(Object identity, Consumer<ItemEmissionAccumulator> read) {
            if (state.stores.add(identity)) read.accept(this);
        }

        public ItemEmissionAccumulator add(Item item, long quantity) {
            if (quantity <= 0) return this;
            RadioactiveObject rads = RadioactiveItemRegister.getValue(item);
            if (!(rads.amount() > 0)) return this;
            radiation = saturatedAdd(radiation, saturatedMultiply(rads.amount(), quantity));
            if (!Double.isNaN(rads.strength())) {
                strength = Math.max(strength, Math.min(Double.MAX_VALUE, rads.strength()));
            }
            return this;
        }

        public ItemEmission build(int maxRange) {
            return radiation > 0 ? new ItemEmission(radiation, strength,
                    getItemRadiationRange(radiation, maxRange)) : ItemEmission.NONE;
        }

        public ItemEmission build() {
            return build(getConfiguredMaxItemRange());
        }
    }

    private static final List<IRadiationItemContentsReader> ITEM_READERS = new CopyOnWriteArrayList<>();

    public static void registerItemReader(IRadiationItemContentsReader reader) {
        ITEM_READERS.add(reader);
    }

    public static double saturatedMultiply(double a, double b) {
        if (!(a > 0) || !(b > 0)) return 0;
        double result = a * b;
        return Double.isFinite(result) ? result : Double.MAX_VALUE;
    }

    public static double saturatedAdd(double a, double b) {
        double result = a + b;
        return Double.isFinite(result) ? result : Double.MAX_VALUE;
    }

    public static double clampEscapingFraction(double fraction) {
        return Double.isNaN(fraction) ? 1 : Math.max(0, Math.min(1, fraction));
    }

    public static ItemEmission applyContainment(ItemEmission emission, double fraction) {
        return applyContainment(emission, fraction, getConfiguredMaxItemRange());
    }

    public static ItemEmission applyContainment(ItemEmission emission, double fraction, int maxRange) {
        double amount = saturatedMultiply(emission.amount(), clampEscapingFraction(fraction));
        return amount > 0 ? new ItemEmission(amount, emission.strength(),
                getItemRadiationRange(amount, maxRange)) : ItemEmission.NONE;
    }

    private static int getConfiguredMaxItemRange() {
	return VoltaicConfig.INSTANCE.MAX_ITEM_RADIATION_RANGE.get();
    }

    /**
     * The range of radioactive items; the same formula used for dropped items,
     * bounded by the configured maximum. The cap only limits the searched area;
     * callers keep the full amount.
     */
    public static int getItemRadiationRange(double amount) {
	return getItemRadiationRange(amount, getConfiguredMaxItemRange());
    }

    public static int getItemRadiationRange(double amount, int maxRange) {
	if (!(amount > 0)) {
	    return 0;
	}
	double range = Math.sqrt(amount) / (5 * Math.sqrt(2)) * 1.25;
	return (int) Math.min(range, Math.max(0, maxRange));
    }

    public static ItemEmission calculateItemEmission(Item item, long quantity) {
	return new ItemEmissionAccumulator().add(item, quantity).build();
    }

    public static ItemEmission calculateItemEmission(Iterable<ItemStack> items) {
        return calculateItemEmission(null, items);
    }

    public static ItemEmission calculateItemEmission(@Nullable Level level, Iterable<ItemStack> items) {
	ItemEmissionAccumulator accumulator = new ItemEmissionAccumulator(level);
	for (ItemStack item : items) {
	    accumulator.add(item);
	}
	return accumulator.build();
    }

    public static void handleRadioactiveGases(Level level, GenericTile tile, ComponentGasHandlerMulti multi, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveGases(level, tile, multi.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveGases(level, tile, multi.getOutputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveGases(Level level, GenericTile tile, ComponentGasHandlerSimple simple,
	    int radius, boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveGases(level, tile, simple.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveGases(Level level, GenericTile tile, GasTank[] tanks, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	double totRadiation = 0;
	double totStrength = 0;

	for (GasTank tank : tanks) {
	    if (tank.isEmpty()) {
		continue;
	    }

	    GasStack gas = tank.getGas();
	    RadioactiveObject rads = RadioactiveGasRegister.getValue(gas.getGas());
	    if (rads.amount() <= 0) {
		continue;
	    }

	    totRadiation += rads.amount() * gas.getAmount() * gas.getPressure();
	    totStrength = Math.max(totStrength, rads.strength());
	}
	if (totRadiation <= 0)
	    return;

	RadiationSystem.addRadiationSource(level, new SimpleRadiationSource(totRadiation, totStrength, radius, isTemp,
		ticks, tile.getBlockPos(), shouldLinger, shouldCombine));

    }

    public static void handleRadioactiveFluids(Level level, GenericTile tile, ComponentFluidHandlerMulti multi,
	    int radius, boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveFluids(level, tile, multi.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveFluids(level, tile, multi.getOutputTanks(), radius, isTemp, ticks, shouldLinger,
		shouldCombine);
    }

    public static void handleRadioactiveFluids(Level level, GenericTile tile, ComponentFluidHandlerSimple simple,
	    int radius, boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveFluids(level, tile, simple.getInputTanks(), radius, isTemp, ticks, shouldLinger,
		shouldCombine);
    }

    public static void handleRadioactiveFluids(Level level, GenericTile tile, FluidTank[] tanks, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	double totRadiation = 0;
	double totStrength = 0;

	for (FluidTank tank : tanks) {
	    if (tank.isEmpty()) {
		continue;
	    }

	    FluidStack fluid = tank.getFluid();
	    RadioactiveObject rads = RadioactiveFluidRegister.getValue(fluid.getFluid());
	    if (rads.amount() <= 0) {
		continue;
	    }

	    totRadiation += rads.amount() * fluid.getAmount();
	    totStrength = Math.max(totStrength, rads.strength());
	}
	if (totRadiation <= 0)
	    return;

	RadiationSystem.addRadiationSource(level, new SimpleRadiationSource(totRadiation, totStrength, radius, isTemp,
		ticks, tile.getBlockPos(), shouldLinger, shouldCombine));
    }

    public static void handleRadioactiveItems(Level level, GenericTile tile, ComponentInventory inv, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveItems(level, tile, inv.getInputContents(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveItems(level, tile, inv.getOutputContents(), radius, isTemp, ticks, shouldLinger,
		shouldCombine);
    }

    public static void handleRadioactiveItems(Level level, GenericTile tile, List<ItemStack> items, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
        ItemEmission emission = calculateItemEmission(level, items);
        if (!emission.isRadioactive()) return;
        // Machine callers retain their explicit range, lifetime and lingering rules.
        RadiationSystem.addRadiationSource(level, new SimpleRadiationSource(emission.amount(), emission.strength(),
                radius, isTemp, ticks, tile.getBlockPos(), shouldLinger, shouldCombine));
    }

}
