package voltaic.prefab.tile.components.utils;

import voltaic.api.gas.PropertyGasTank;
import voltaic.prefab.tile.components.IComponent;

public interface IComponentGasHandler extends IComponent {

	public static final int TANK_MULTIPLIER = 1000;

	PropertyGasTank[] getInputTanks();

	PropertyGasTank[] getOutputTanks();

}
