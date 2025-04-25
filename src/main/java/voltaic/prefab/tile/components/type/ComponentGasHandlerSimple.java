package voltaic.prefab.tile.components.type;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.PropertyGasTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicRegistries;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Extension of the PropertyGasTank making it usable as a ComponentGasHandler
 * 
 * This ComponentGasHandler has only one tank with programmable inputs and outputs where as ComponentGasHandlerMulti has
 * distinct input and output tanks
 * 
 * @author skip999
 *
 */
public class ComponentGasHandlerSimple extends PropertyGasTank implements IComponentGasHandler {

    @Nullable
    public Direction[] inputDirections;
    @Nullable
    public Direction[] outputDirections;

    private boolean isSided = false;

    @Nullable
    private TagKey<Gas>[] validGasTags;
    @Nullable
    private Gas[] validGases;

    private HashSet<Gas> validatorGases = new HashSet<>();

    private IGasHandler[] sidedOptionals = new IGasHandler[6]; // Down Up North South West East

    @Nullable
    private IGasHandler inputOptional = null;
    @Nullable
    private IGasHandler outputOptional = null;

    public ComponentGasHandlerSimple(GenericTile holder, String key, int capacity, int maxTemperature, int maxPressure) {
        super(holder, key, capacity, maxTemperature, maxPressure);
    }

    public ComponentGasHandlerSimple(GenericTile holder, String key, int capacity, int maxTemperature, int maxPressure, Predicate<GasStack> isGasValid) {
        super(holder, key, capacity, maxTemperature, maxPressure, isGasValid);
    }

    protected ComponentGasHandlerSimple(PropertyGasTank other) {
        super(other);
    }

    public ComponentGasHandlerSimple setInputDirections(BlockEntityUtils.MachineDirection... directions) {
        isSided = true;
        inputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
        return this;
    }

    public ComponentGasHandlerSimple setOutputDirections(BlockEntityUtils.MachineDirection... directions) {
        isSided = true;
        outputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
        return this;
    }

    public ComponentGasHandlerSimple universalInput() {
        inputDirections = Direction.values();
        return this;
    }

    public ComponentGasHandlerSimple universalOutput() {
        outputDirections = Direction.values();
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

    public ComponentGasHandlerSimple setValidFluids(Gas... fluids) {
        validGases = fluids;
        return this;
    }

    public ComponentGasHandlerSimple setValidFluidTags(TagKey<Gas>... fluids) {
        validGasTags = fluids;
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
    public void holder(GenericTile holder) {
        this.holder = holder;
    }

    @Override
    public GenericTile getHolder() {
        return holder;
    }

    @Override
    public void refreshIfUpdate(BlockState oldState, BlockState newState) {
        if (isSided && oldState.hasProperty(VoltaicBlockStates.FACING) && newState.hasProperty(VoltaicBlockStates.FACING) && oldState.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING)) {
            defineOptionals(newState.getValue(VoltaicBlockStates.FACING));
        }
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side, CapabilityInputType inputType) {
    	if (!isSided) {
            return LazyOptional.of(() -> this).cast();
        }
        if (side == null) {
            return LazyOptional.empty();
        }
        return LazyOptional.of(() -> sidedOptionals[side.ordinal()]).cast();
    }

    @Override
    public void refresh() {

        defineOptionals(holder.getFacing());

    }

    private void defineOptionals(Direction facing) {

        holder.invalidateCaps();

        sidedOptionals = new IGasHandler[6];

        inputOptional = null;

        outputOptional = null;

        if (isSided) {

            // Input

            if (inputDirections != null) {
                inputOptional = new InputTank(this);

                for (Direction dir : inputDirections) {
                    sidedOptionals[BlockEntityUtils.getRelativeSide(facing, dir).ordinal()] = inputOptional;
                }
            }

            if (outputDirections != null) {
                outputOptional = new OutputTank(this);

                for (Direction dir : outputDirections) {
                    sidedOptionals[BlockEntityUtils.getRelativeSide(facing, dir).ordinal()] = outputOptional;
                }
            }

        }

    }

    @Override
    public void onLoad() {
        IComponentGasHandler.super.onLoad();
        if (validGases != null) {
            for (Gas gas : validGases) {
                validatorGases.add(gas);
            }
        }
        if (validGasTags != null) {
            for (TagKey<Gas> tag : validGasTags) {
            	for (Gas gas : VoltaicRegistries.gasRegistry().tags().getTag(tag).stream().toList()) {
					validatorGases.add(gas);
				}
            }
        }
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
