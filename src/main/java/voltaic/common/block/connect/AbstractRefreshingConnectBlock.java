package voltaic.common.block.connect;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import voltaic.api.network.cable.IRefreshableCable;
import voltaic.prefab.tile.types.GenericConnectTile;
import voltaic.prefab.utilities.WorldUtils;

public abstract class AbstractRefreshingConnectBlock<CONDUCTOR extends GenericConnectTile & IRefreshableCable> extends AbstractConnectBlock {

    public AbstractRefreshingConnectBlock(Properties properties, double radius) {
        super(properties, radius);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (worldIn.isClientSide()) {
            return;
        }
        TileEntity tile = worldIn.getBlockEntity(pos);
        CONDUCTOR conductor = getCableIfValid(tile);
        if (conductor == null || conductor.isRemoved()) {
            return;
        }

        BlockPos relPos;

        EnumConnectType[] connections = new EnumConnectType[6];

        for (Direction dir : Direction.values()) {
            relPos = pos.relative(dir);
            connections[dir.ordinal()] = getConnection(worldIn.getBlockState(relPos), worldIn.getBlockEntity(relPos), conductor, dir);
        }

        conductor.writeConnections(Direction.values(), connections);

    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {

        super.onNeighborChange(state, world, pos, neighbor);

        if (world.isClientSide()) {
            return;
        }
        TileEntity tile = world.getBlockEntity(pos);
        CONDUCTOR conductor = getCableIfValid(tile);
        if (conductor == null || conductor.isRemoved()) {
            return;
        }

        Direction facing = WorldUtils.getDirectionFromPosDelta(pos, neighbor);

        EnumConnectType currConnection = conductor.readConnections()[facing.ordinal()];

        EnumConnectType connection = getConnection(world.getBlockState(neighbor), world.getBlockEntity(neighbor), conductor, facing);

        if (currConnection != connection && conductor.writeConnection(facing, connection)) {
            conductor.updateNetwork(facing);
        }

    }
    
    @Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    	
    	if (world.isClientSide()) {
            return super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
        }
        TileEntity tile = world.getBlockEntity(currentPos);
        CONDUCTOR conductor = getCableIfValid(tile);
        if (conductor == null || conductor.isRemoved()) {
            return super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
        }

        EnumConnectType currConnection = conductor.readConnections()[facing.ordinal()];

        EnumConnectType connection = getConnection(world.getBlockState(facingPos), world.getBlockEntity(facingPos), conductor, facing);

        if (currConnection != connection && conductor.writeConnection(facing, connection)) {
            conductor.updateNetwork(facing);
        }
    	
		return super.updateShape(stateIn, facing, facingState, world, currentPos, facingPos);
	}

    public abstract EnumConnectType getConnection(BlockState otherState, TileEntity otherTile, CONDUCTOR thisConductor, Direction dir);

    @Nullable
    public abstract CONDUCTOR getCableIfValid(TileEntity tile);

}
