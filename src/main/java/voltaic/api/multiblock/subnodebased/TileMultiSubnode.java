package voltaic.api.multiblock.subnodebased;

import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicTiles;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileMultiSubnode extends GenericTile {

	public final SingleProperty<BlockPos> parentPos = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "nodePos", BlockEntityUtils.OUT_OF_REACH));
	public final SingleProperty<Integer> nodeIndex = property(new SingleProperty<>(PropertyTypes.INTEGER, "nodeIndex", 0));

	public VoxelShape shapeCache;

	public TileMultiSubnode() {
		super(VoltaicTiles.TILE_MULTI.get());
		addComponent(new ComponentPacketHandler(this));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			return node.getSubnodeCapability(cap, side);
		}
		return LazyOptional.empty();
	}

	public void setData(BlockPos parentPos, int subnodeIndex) {

		this.parentPos.setValue(parentPos);
		nodeIndex.setValue(subnodeIndex);

		setChanged();

	}

	public VoxelShape getShape() {

		if (shapeCache != null) {
			return shapeCache;
		}

		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;

			return shapeCache = node.getSubNodes().getSubnodes(node.getFacingDirection())[nodeIndex.getValue()].getShape(node.getFacingDirection());

		}

		return VoxelShapes.block();
	}

	@Override
	public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			node.onSubnodeNeighborChange(this, neighbor, blockStateTrigger);
		}
	}

	@Override
	public ActionResultType use(PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			return node.onSubnodeUse(player, handIn, hit, this);
		}
		return super.use(player, handIn, hit);
	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			node.onSubnodePlace(this, oldState, isMoving);
		}
	}

	@Override
	public int getComparatorSignal() {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			return node.getSubdnodeComparatorSignal(this);
		}
		return 0;
	}

	@Override
	public void onBlockDestroyed() {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			node.onSubnodeDestroyed(this);
		}
		super.onBlockDestroyed();
	}

	@Override
	public int getDirectSignal(Direction dir) {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			return node.getDirectSignal(this, dir);
		}
		return 0;
	}

	@Override
	public int getSignal(Direction dir) {
		TileEntity tile = level.getBlockEntity(parentPos.getValue());
		if (tile instanceof IMultiblockParentTile) {
			IMultiblockParentTile node = (IMultiblockParentTile) tile;
			return node.getSignal(this, dir);
		}
		return 0;
	}

}
