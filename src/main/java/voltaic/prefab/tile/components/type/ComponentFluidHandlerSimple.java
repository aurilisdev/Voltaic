package voltaic.prefab.tile.components.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import voltaic.api.fluid.PropertyFluidTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.utilities.BlockEntityUtils;

/**
 * Extension of PropertyFluidTank implementing directional I/O and the Component
 * system
 * 
 * This is a separate class because ComponentFluidHandlerMulti is does not have
 * segregated input and output tanks. Instead it has a single tank that is used
 * for both functions.
 * 
 * This class also has no concept of a RecipeType tied to it since recipes have
 * segregated inputs and outputs.
 * 
 * @author skip999
 *
 */
public class ComponentFluidHandlerSimple extends PropertyFluidTank implements IComponentFluidHandler {

    public Direction[] inputDirections = {};
    public Direction[] outputDirections = {};
    private Fluid[] validFluids = {};
    private TagKey<Fluid>[] validFluidTags = (TagKey<Fluid>[]) new TagKey<?>[0];

    private final HashSet<Fluid> validatorFluids = new HashSet<>();

    private IFluidHandler[] sidedOptionals = new IFluidHandler[6]; // Down Up North South West East

    @Nullable
    private IFluidHandler inputOptional = null;
    @Nullable
    private IFluidHandler outputOptional = null;

    private boolean isSided = false;

    public ComponentFluidHandlerSimple(int capacity, Predicate<FluidStack> validator, GenericTile holder, String key) {
	super(capacity, validator, holder, key);
    }

    public ComponentFluidHandlerSimple(int capacity, GenericTile holder, String key) {
	super(capacity, holder, key);
    }

    protected ComponentFluidHandlerSimple(ComponentFluidHandlerSimple other) {
	super(other);
    }

    public ComponentFluidHandlerSimple setInputDirections(BlockEntityUtils.MachineDirection... directions) {
	inputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	isSided = true;
	return this;
    }

    public ComponentFluidHandlerSimple setOutputDirections(BlockEntityUtils.MachineDirection... directions) {
	outputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	isSided = true;
	return this;
    }

    @Override
    public ComponentFluidHandlerSimple setCapacity(int capacity) {
	return (ComponentFluidHandlerSimple) super.setCapacity(capacity);
    }

    @Override
    public ComponentFluidHandlerSimple setValidator(Predicate<FluidStack> validator) {
	return (ComponentFluidHandlerSimple) super.setValidator(validator);
    }

    public ComponentFluidHandlerSimple setValidFluids(Fluid... fluids) {
	validFluids = fluids;
	return this;
    }

    public ComponentFluidHandlerSimple setValidFluidTags(TagKey<Fluid>... fluids) {
	validFluidTags = fluids;
	return this;
    }

    @Override
    public IComponentType getType() {
	return IComponentType.FluidHandler;
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    @Override
    public void refreshIfUpdate(Level level, BlockState oldState, BlockState newState) {
	if (isSided && oldState.hasProperty(VoltaicBlockStates.FACING)
		&& newState.hasProperty(VoltaicBlockStates.FACING)
		&& oldState.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING)) {
	    defineOptionals(level, newState.getValue(VoltaicBlockStates.FACING));
	}
    }

    @Override
    public @Nullable IFluidHandler getCapability(@Nullable Direction side, CapabilityInputType type) {
	if (!isSided)
	    return this;

	if (side == null)
	    return null;

	return sidedOptionals[side.ordinal()];
    }

    @Override
    public void refresh(Level level) {
	defineOptionals(level, holder.getFacing());
    }

    private void defineOptionals(Level level, Direction facing) {
	level.invalidateCapabilities(holder.getBlockPos());
	sidedOptionals = new IFluidHandler[6];
	inputOptional = null;
	outputOptional = null;

	if (!isSided)
	    return;

	inputOptional = new InputTank(this);
	for (Direction dir : inputDirections) {
	    int index = BlockEntityUtils.getRelativeSide(facing, dir).ordinal();
	    sidedOptionals[index] = inputOptional;
	}

	outputOptional = new OutputTank(this);
	for (Direction dir : outputDirections) {
	    int index = BlockEntityUtils.getRelativeSide(facing, dir).ordinal();
	    sidedOptionals[index] = outputOptional;
	}
    }

    @Override
    public void onLoad(Level level) {
	IComponentFluidHandler.super.onLoad(level);
	Collections.addAll(validatorFluids, validFluids);
	for (TagKey<Fluid> tag : validFluidTags) {
	    BuiltInRegistries.FLUID.getTag(tag).get().stream()
		    .forEach(holder -> { validatorFluids.add(holder.value()); });
	}
	if (!validatorFluids.isEmpty()) {
	    validator = fluidStack -> validatorFluids.contains(fluidStack.getFluid());
	}
    }

    @Override
    public PropertyFluidTank[] getInputTanks() {
	return toArray();
    }

    @Override
    public PropertyFluidTank[] getOutputTanks() {
	return toArray();
    }

    public PropertyFluidTank[] toArray() {
	return new PropertyFluidTank[] { this };
    }

    @Override
    public boolean equals(@Nullable Object obj) {
	if (obj instanceof ComponentFluidHandlerSimple tank)
	    return tank.getFluid().equals(getFluid()) && tank.getCapacity() == getCapacity();
	return false;
    }

    private class InputTank extends ComponentFluidHandlerSimple {

	public InputTank(ComponentFluidHandlerSimple property) {
	    super(property);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
	    return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
	    return FluidStack.EMPTY;
	}

    }

    private class OutputTank extends ComponentFluidHandlerSimple {

	public OutputTank(ComponentFluidHandlerSimple property) {
	    super(property);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
	    return 0;
	}

    }

}
