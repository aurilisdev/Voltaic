package voltaic.api.multiblock.subnodebased;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileMultiSubnode extends GenericTile {

	public final SingleProperty<BlockPos> parentPos = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "nodePos", BlockEntityUtils.OUT_OF_REACH));
	public final SingleProperty<Integer> nodeIndex = property(new SingleProperty<>(PropertyTypes.INTEGER, "nodeIndex", 0));

	public VoxelShape shapeCache;

	public TileMultiSubnode(BlockPos worldPosition, BlockState blockState) {
		super(VoltaicTiles.TILE_MULTI.get(), worldPosition, blockState);
		addComponent(new ComponentPacketHandler(this));
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
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

		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {

			return shapeCache = node.getSubNodes().getSubnodes(node.getFacingDirection())[nodeIndex.getValue()].getShape(node.getFacingDirection());

		}

		return Shapes.block();
	}

	@Override
	public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
			node.onSubnodeNeighborChange(this, neighbor, blockStateTrigger);
		}
	}

	@Override
	public InteractionResult use(Player player, InteractionHand handIn, BlockHitResult hit) {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
			return node.onSubnodeUse(player, handIn, hit, this);
		}
		return super.use(player, handIn, hit);
	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
			node.onSubnodePlace(this, oldState, isMoving);
		}
	}

	@Override
	public int getComparatorSignal() {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
			return node.getSubdnodeComparatorSignal(this);
		}
		return 0;
	}

	@Override
	public void onBlockDestroyed() {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile node) {
			node.onSubnodeDestroyed(this);
		}
		super.onBlockDestroyed();
	}

	@Override
	public int getDirectSignal(Direction dir) {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile parent) {
			return parent.getDirectSignal(this, dir);
		}
		return 0;
	}

	@Override
	public int getSignal(Direction dir) {
		if (level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile parent) {
			return parent.getSignal(this, dir);
		}
		return 0;
	}

}
