package voltaic.prefab.utilities;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.RadioactiveObject;
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

    public static void handleRadioactiveGases(GenericTile tile, ComponentGasHandlerMulti multi, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveGases(tile, multi.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveGases(tile, multi.getOutputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveGases(GenericTile tile, ComponentGasHandlerSimple simple, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveGases(tile, simple.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveGases(GenericTile tile, GasTank[] tanks, int radius, boolean isTemp, int ticks,
	    boolean shouldLinger, boolean shouldCombine) {

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

	if (totRadiation <= 0) {
	    return;
	}

	RadiationSystem.addRadiationSource(tile.getLevel(), new SimpleRadiationSource(totRadiation, totStrength, radius,
		isTemp, ticks, tile.getBlockPos(), shouldLinger, shouldCombine));

    }

    public static void handleRadioactiveFluids(GenericTile tile, ComponentFluidHandlerMulti multi, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveFluids(tile, multi.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveFluids(tile, multi.getOutputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveFluids(GenericTile tile, ComponentFluidHandlerSimple simple, int radius,
	    boolean isTemp, int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveFluids(tile, simple.getInputTanks(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveFluids(GenericTile tile, FluidTank[] tanks, int radius, boolean isTemp,
	    int ticks, boolean shouldLinger, boolean shouldCombine) {

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

	if (totRadiation <= 0) {
	    return;
	}

	RadiationSystem.addRadiationSource(tile.getLevel(), new SimpleRadiationSource(totRadiation, totStrength, radius,
		isTemp, ticks, tile.getBlockPos(), shouldLinger, shouldCombine));

    }

    public static void handleRadioactiveItems(GenericTile tile, ComponentInventory inv, int radius, boolean isTemp,
	    int ticks, boolean shouldLinger, boolean shouldCombine) {
	handleRadioactiveItems(tile, inv.getInputContents(), radius, isTemp, ticks, shouldLinger, shouldCombine);
	handleRadioactiveItems(tile, inv.getOutputContents(), radius, isTemp, ticks, shouldLinger, shouldCombine);
    }

    public static void handleRadioactiveItems(GenericTile tile, List<ItemStack> items, int radius, boolean isTemp,
	    int ticks, boolean shouldLinger, boolean shouldCombine) {

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

	if (totRadiation <= 0) {
	    return;
	}

	RadiationSystem.addRadiationSource(tile.getLevel(), new SimpleRadiationSource(totRadiation, totStrength, radius,
		isTemp, ticks, tile.getBlockPos(), shouldLinger, shouldCombine));

    }

}
