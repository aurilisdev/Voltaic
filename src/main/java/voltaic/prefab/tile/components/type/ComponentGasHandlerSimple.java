package voltaic.prefab.tile.components.type;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.PropertyGasTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.tile.components.utils.RegistryValidatorUtils;
import voltaic.prefab.tile.components.utils.SidedCapabilityHandler;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicGases;

/**
 * Extension of the PropertyGasTank making it usable as a ComponentGasHandler
 * 
 * This ComponentGasHandler has only one tank with programmable inputs and
 * outputs where as ComponentGasHandlerMulti has distinct input and output tanks
 * 
 * @author skip999
 *
 */
public class ComponentGasHandlerSimple extends PropertyGasTank implements IComponentGasHandler {

    private Gas[] validGases = {};
    public Direction[] inputDirections = {};
    public Direction[] outputDirections = {};
    private TagKey<Gas>[] validGasTags = (TagKey<Gas>[]) new TagKey<?>[0];

    private final HashSet<Gas> validatorGases = new HashSet<>();

    private final SidedCapabilityHandler<IGasHandler> sidedHandler = new SidedCapabilityHandler<>(IGasHandler[]::new);

    public ComponentGasHandlerSimple(GenericTile holder, String key, int capacity, int maxTemperature,
	    int maxPressure) {
	super(holder, key, capacity, maxTemperature, maxPressure);
    }

    public ComponentGasHandlerSimple(GenericTile holder, String key, int capacity, int maxTemperature, int maxPressure,
	    Predicate<GasStack> isGasValid) {
	super(holder, key, capacity, maxTemperature, maxPressure, isGasValid);
    }

    protected ComponentGasHandlerSimple(PropertyGasTank other) {
	super(other);
    }

    public ComponentGasHandlerSimple setInputDirections(BlockEntityUtils.MachineDirection... directions) {
	sidedHandler.enableSidedAccess();
	inputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	return this;
    }

    public ComponentGasHandlerSimple setOutputDirections(BlockEntityUtils.MachineDirection... directions) {
	sidedHandler.enableSidedAccess();
	outputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	return this;
    }

    public ComponentGasHandlerSimple universalInput() {
	inputDirections = Direction.values();
	sidedHandler.enableSidedAccess();
	return this;
    }

    public ComponentGasHandlerSimple universalOutput() {
	outputDirections = Direction.values();
	sidedHandler.enableSidedAccess();
	return this;
    }

    @Override
    public ComponentGasHandlerSimple setValidator(Predicate<GasStack> predicate) {
	return (ComponentGasHandlerSimple) super.setValidator(predicate);
    }

    @Override
    public ComponentGasHandlerSimple setOnGasCondensed(BiConsumer<GasTank, GenericTile> onGasCondensed) {
	return (ComponentGasHandlerSimple) super.setOnGasCondensed(onGasCondensed);
    }

    public ComponentGasHandlerSimple setValidGases(Gas... gases) {
	validGases = gases;
	return this;
    }

    public ComponentGasHandlerSimple setValidGasTags(TagKey<Gas>... gasTags) {
	validGasTags = gasTags;
	return this;
    }

    @Override
    public PropertyGasTank[] getInputTanks() {
	return asArray();
    }

    @Override
    public PropertyGasTank[] getOutputTanks() {
	return asArray();
    }

    @Override
    public IComponentType getType() {
	return IComponentType.GasHandler;
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    @Override
    public void refreshIfUpdate(Level level, BlockState oldState, BlockState newState) {
	if (sidedHandler.requiresRefresh(oldState, newState)) {
	    defineOptionals(level, newState.getValue(VoltaicBlockStates.FACING));
	}
    }

    @Override
    public @Nullable IGasHandler getCapability(@Nullable Direction direction, CapabilityInputType mode) {
	return sidedHandler.get(direction, this);
    }

    @Override
    public void refresh(Level level) {
	defineOptionals(level, holder.getFacing());
    }

    private void defineOptionals(Level level, Direction facing) {
	level.invalidateCapabilities(holder.getBlockPos());
	sidedHandler.refresh(facing, inputDirections, () -> new InputTank(this), outputDirections,
		() -> new OutputTank(this));
    }

    @Override
    public void onLoad(Level level) {
	IComponentGasHandler.super.onLoad(level);
	RegistryValidatorUtils.populate(validatorGases, validGases, validGasTags, VoltaicGases.GAS_REGISTRY);
	if (!validatorGases.isEmpty()) {
	    isGasValid = gasStack -> validatorGases.contains(gasStack.getGas());
	}
    }

    private class InputTank extends ComponentGasHandlerSimple {

	public InputTank(ComponentGasHandlerSimple property) {
	    super(property);
	}

	@Override
	public GasStack drain(int amount, GasAction action) {
	    return GasStack.EMPTY;
	}

	@Override
	public GasStack drain(GasStack resource, GasAction action) {
	    return GasStack.EMPTY;
	}

    }

    private class OutputTank extends ComponentGasHandlerSimple {

	public OutputTank(ComponentGasHandlerSimple property) {
	    super(property);
	}

	@Override
	public int fill(GasStack resource, GasAction action) {
	    return 0;
	}

    }

}
