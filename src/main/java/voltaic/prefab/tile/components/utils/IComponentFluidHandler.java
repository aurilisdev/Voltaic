package voltaic.prefab.tile.components.utils;

import voltaic.api.fluid.PropertyFluidTank;
import voltaic.prefab.tile.components.IComponent;

public interface IComponentFluidHandler extends IComponent {

	public static final int TANK_MULTIPLER = 1000;

	PropertyFluidTank[] getInputTanks();

	PropertyFluidTank[] getOutputTanks();

}
