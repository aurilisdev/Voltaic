package voltaic.prefab.utilities;

import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.RadioactiveObject;
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

	private double radiation = 0;
	private double strength = 0;

	public ItemEmissionAccumulator add(ItemStack stack) {
	    return stack.isEmpty() ? this : add(stack.getItem(), stack.getCount());
	}

	public ItemEmissionAccumulator add(Item item, long quantity) {
	    if (quantity <= 0) {
		return this;
	    }
	    RadioactiveObject rads = RadioactiveItemRegister.getValue(item);
	    double perItem = rads.amount();
	    if (!(perItem > 0) || Double.isNaN(perItem)) {
		return this;
	    }
	    radiation = saturatedAdd(radiation, perItem * (double) quantity);
	    double itemStrength = rads.strength();
	    if (!Double.isNaN(itemStrength)) {
		strength = Math.max(strength, itemStrength);
	    }
	    return this;
	}

	public ItemEmission build(int maxRange) {
	    if (!(radiation > 0)) {
		return ItemEmission.NONE;
	    }
	    return new ItemEmission(radiation, strength, getItemRadiationRange(radiation, maxRange));
	}

	public ItemEmission build() {
	    return build(getConfiguredMaxItemRange());
	}

	private static double saturatedAdd(double a, double b) {
	    double sum = a + b;
	    return Double.isFinite(sum) ? sum : Double.MAX_VALUE;
	}
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
	ItemEmissionAccumulator accumulator = new ItemEmissionAccumulator();
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
	double totRadiation = 0;
	double totStrength = 0;

	for (ItemStack item : items) {
	    if (item.isEmpty()) {
		continue;
	    }

	    RadioactiveObject rads = RadioactiveItemRegister.getValue(item.getItem());
	    if (rads.amount() <= 0) {
		continue;
	    }

	    totRadiation += rads.amount() * item.getCount();
	    totStrength = Math.max(totStrength, rads.strength());
	}
	if (totRadiation <= 0)
	    return;

	RadiationSystem.addRadiationSource(level, new SimpleRadiationSource(totRadiation, totStrength, radius, isTemp,
		ticks, tile.getBlockPos(), shouldLinger, shouldCombine));

    }

}
