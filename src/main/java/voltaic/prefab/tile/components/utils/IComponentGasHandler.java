package voltaic.prefab.tile.components.utils;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.PropertyGasTank;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponent;

public interface IComponentGasHandler extends IComponent {

	public static final int TANK_MULTIPLIER = 1000;

	PropertyGasTank[] getInputTanks();

	PropertyGasTank[] getOutputTanks();
	
	@Nullable
	public IGasHandler getCapability(@Nullable Direction direction, CapabilityInputType mode);

}
