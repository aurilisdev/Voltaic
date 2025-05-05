package voltaic.prefab.block;

import javax.annotation.Nullable;

import voltaic.common.block.states.VoltaicBlockStates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class GenericEntityBlockWaterloggable extends GenericEntityBlock implements IWaterLoggable {

	protected GenericEntityBlockWaterloggable(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(VoltaicBlockStates.WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(VoltaicBlockStates.WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return super.getStateForPlacement(context).setValue(VoltaicBlockStates.WATERLOGGED, (fluidstate.getType().is(FluidTags.WATER) && fluidstate.isSource()));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(VoltaicBlockStates.WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(VoltaicBlockStates.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

}
