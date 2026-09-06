package voltaic.common.block.connect;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.network.cable.IRefreshableCable;
import voltaic.prefab.tile.types.GenericConnectTile;
import voltaic.prefab.utilities.WorldUtils;

public abstract class AbstractRefreshingConnectBlock<CONDUCTOR extends GenericConnectTile & IRefreshableCable>
	extends AbstractConnectBlock {

    public AbstractRefreshingConnectBlock(Properties properties, double radius) {
	super(properties, radius);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
	super.onPlace(state, worldIn, pos, oldState, isMoving);
	if (worldIn.isClientSide())
	    return;

	BlockEntity tile = worldIn.getBlockEntity(pos);
	CONDUCTOR conductor = getCableIfValid(tile);
	if (conductor == null || conductor.isRemoved())
	    return;

	BlockPos relPos;

	EnumConnectType[] connections = new EnumConnectType[6];

	for (Direction dir : Direction.values()) {
	    relPos = pos.relative(dir);
	    connections[dir.ordinal()] = getConnection(worldIn.getBlockState(relPos), worldIn.getBlockEntity(relPos),
		    conductor, dir);
	}

	conductor.writeConnections(Direction.values(), connections);

    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
	super.onNeighborChange(state, world, pos, neighbor);

	if (world.isClientSide())
	    return;

	BlockEntity tile = world.getBlockEntity(pos);
	CONDUCTOR conductor = getCableIfValid(tile);
	if (conductor == null || conductor.isRemoved())
	    return;

	Direction facing = WorldUtils.getDirectionFromPosDelta(pos, neighbor);
	if (facing == null)
	    return;

	EnumConnectType currConnection = conductor.readConnections()[facing.ordinal()];

	EnumConnectType connection = getConnection(world.getBlockState(neighbor), world.getBlockEntity(neighbor),
		conductor, facing);

	if (currConnection != connection && !conductor.writeConnection(facing, connection))
	    return;

	conductor.updateNetwork(facing);

    }

    public abstract EnumConnectType getConnection(BlockState otherState, @Nullable BlockEntity otherTile,
	    CONDUCTOR thisConductor, Direction dir);

    public abstract @Nullable CONDUCTOR getCableIfValid(BlockEntity tile);

}
