package voltaic.prefab.tile.components.utils;

import org.jetbrains.annotations.Nullable;

import voltaic.api.fluid.PropertyFluidTank;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponent;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IComponentFluidHandler extends IComponent {

	public static final int TANK_MULTIPLER = 1000;

	PropertyFluidTank[] getInputTanks();

	PropertyFluidTank[] getOutputTanks();
	
	@Nullable
	public IFluidHandler getCapability(@Nullable Direction side, CapabilityInputType type);

}
