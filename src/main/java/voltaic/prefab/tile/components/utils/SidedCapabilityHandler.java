package voltaic.prefab.tile.components.utils;

import java.util.function.IntFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.utilities.BlockEntityUtils;

/**
 * Maintains the directional capability dispatch shared by single-tank handler
 * components. The handler type remains specific to the owning component.
 *
 * @param <Handler> the capability handler type
 */
public final class SidedCapabilityHandler<Handler> {

    private final IntFunction<Handler[]> arrayFactory;
    private Handler[] handlers;
    private boolean sided;

    public SidedCapabilityHandler(IntFunction<Handler[]> arrayFactory) {
	this.arrayFactory = arrayFactory;
	handlers = arrayFactory.apply(Direction.values().length);
    }

    public void enableSidedAccess() {
	sided = true;
    }

    public boolean isSided() {
	return sided;
    }

    @Nullable
    public Handler getSided(@Nullable Direction side) {
	if (!sided || side == null)
	    return null;
	return handlers[side.ordinal()];
    }

    @Nullable
    public Handler get(@Nullable Direction side, Handler unsidedHandler) {
	if (!sided)
	    return unsidedHandler;
	return getSided(side);
    }

    public boolean requiresRefresh(BlockState oldState, BlockState newState) {
	return sided && oldState.hasProperty(VoltaicBlockStates.FACING)
		&& newState.hasProperty(VoltaicBlockStates.FACING)
		&& oldState.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING);
    }

    public void refresh(Direction facing, Direction[] inputDirections, Supplier<Handler> inputHandler,
	    Direction[] outputDirections, Supplier<Handler> outputHandler) {
	handlers = arrayFactory.apply(Direction.values().length);
	if (!sided)
	    return;

	assign(facing, inputDirections, inputHandler.get());
	assign(facing, outputDirections, outputHandler.get());
    }

    private void assign(Direction facing, Direction[] directions, Handler handler) {
	for (Direction direction : directions) {
	    handlers[BlockEntityUtils.getRelativeSide(facing, direction).ordinal()] = handler;
	}
    }
}
