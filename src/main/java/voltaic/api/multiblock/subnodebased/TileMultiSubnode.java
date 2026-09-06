package voltaic.api.multiblock.subnodebased;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.IGasHandler;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicTiles;

public class TileMultiSubnode extends GenericTile {

    public final SingleProperty<BlockPos> parentPos = property(new SingleProperty<>(getPropertyManager(),
	    PropertyTypes.BLOCK_POS, "nodePos", BlockEntityUtils.OUT_OF_REACH));

    public final SingleProperty<Integer> nodeIndex = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "nodeIndex", 0));

    @Nullable
    public VoxelShape shapeCache;

    public TileMultiSubnode(BlockPos worldPosition, BlockState blockState) {
	super(VoltaicTiles.TILE_MULTI.get(), worldPosition, blockState);
    }

    @Nullable
    private IMultiblockParentTile getParent() {
	Level level = getLevel();
	if (level == null || !(level.getBlockEntity(parentPos.getValue()) instanceof IMultiblockParentTile parent))
	    return null;

	return parent;
    }

    @Override
    @Nullable
    public ICapabilityElectrodynamic getElectrodynamicCapability(@Nullable Direction side) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return null;

	return parent.getSubnodeElectrodynamicCapability(this, side);
    }

    @Override
    @Nullable
    public IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return null;

	return parent.getSubnodeFluidHandlerCapability(this, side);
    }

    @Override
    @Nullable
    public IGasHandler getGasHandlerCapability(@Nullable Direction side) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return null;

	return parent.getSubnodeGasHandlerCapability(this, side);
    }

    @Override
    @Nullable
    public IItemHandler getItemHandlerCapability(@Nullable Direction side) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return null;

	return parent.getSubnodeItemHandlerCapability(this, side);
    }

    public void setData(BlockPos parentPos, int subnodeIndex) {
	this.parentPos.setValue(parentPos);
	nodeIndex.setValue(subnodeIndex);
	shapeCache = null;
	setChanged();
    }

    public VoxelShape getShape() {
	if (shapeCache != null)
	    return shapeCache;

	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return Shapes.block();

	return shapeCache = parent.getSubNodes().getSubnodes(parent.getFacingDirection())[nodeIndex.getValue()]
		.getShape(parent.getFacingDirection());
    }

    @Override
    public void onNeighbourChanged(LevelReader reader, BlockPos neighbor, boolean blockStateTrigger) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return;

	parent.onSubnodeNeighborChange(reader, this, neighbor, blockStateTrigger);
    }

    @Override
    public InteractionResult useWithoutItem(Level level, Player player, BlockHitResult hit) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return super.useWithoutItem(level, player, hit);

	return parent.onSubnodeUseWithoutItem(level, player, hit, this);
    }

    @Override
    public ItemInteractionResult useWithItem(Level level, ItemStack used, Player player, InteractionHand hand,
	    BlockHitResult hit) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return super.useWithItem(level, used, player, hand, hit);

	return parent.onSubnodeUseWithItem(level, used, player, hand, hit, this);
    }

    @Override
    public void onPlace(Level level, BlockState oldState, boolean isMoving) {
	super.onPlace(level, oldState, isMoving);

	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return;

	parent.onSubnodePlace(this, level, oldState, isMoving);
    }

    @Override
    public int getComparatorSignal(Level level) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return 0;

	return parent.getSubdnodeComparatorSignal(level, this);
    }

    @Override
    public void onBlockDestroyed(Level level) {
	IMultiblockParentTile parent = getParent();

	if (parent != null) {
	    parent.onSubnodeDestroyed(level, this);
	}

	super.onBlockDestroyed(level);
    }

    @Override
    public int getDirectSignal(Direction dir) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return 0;

	return parent.getDirectSignal(this, dir);
    }

    @Override
    public int getSignal(Direction dir) {
	IMultiblockParentTile parent = getParent();
	if (parent == null)
	    return 0;

	return parent.getSignal(this, dir);
    }
}