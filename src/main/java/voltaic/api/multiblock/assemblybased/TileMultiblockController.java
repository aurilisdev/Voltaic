package voltaic.api.multiblock.assemblybased;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandler;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.Scheduler;

/**
 * @author skip999
 */
public abstract class TileMultiblockController extends TileReplaceable {

    public final List<TileMultiblockSlave> slaveList = new ArrayList<>();

    public final ListProperty<BlockPos> slavePositions = property(
	    new ListProperty<>(getPropertyManager(), PropertyTypes.BLOCK_POS_LIST, "slavepositions", new ArrayList<>()))
	    .onTileLoaded(prop -> {
		Level level = this.level;
		if (level == null || level.isClientSide())
		    return;

		Scheduler.schedule(2, () -> {
		    slaveList.clear();
		    prop.getValue().forEach(blockPos -> slaveList
			    .add((TileMultiblockSlave) level.getBlockEntity(worldPosition.offset(blockPos))));
		});

	    });

    public final SingleProperty<Boolean> isFormed = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.BOOLEAN, "isformed", false));

    private boolean isDestroyed = false;

    public TileMultiblockController(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
	super(tileEntityTypeIn, worldPos, blockState);
	addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickCommon(this::tickCommon)
		.tickClient(this::tickClient));

    }

    public void tickServer(Level level, ComponentTickable tickable) {

    }

    public void tickCommon(Level level, ComponentTickable tickable) {

    }

    public void tickClient(Level level, ComponentTickable tickable) {

    }

    public void checkFormed() {
	Level world = getLevel();
	if (world == null)
	    return;

	Direction facing = getFacing().getOpposite();
	List<MultiblockSlaveNode> nodes = Multiblock.getNodes(world, getResourceKey(), facing);
	boolean formed = true;

	for (MultiblockSlaveNode node : nodes) {
	    BlockPos nodePos = getBlockPos().offset(node.offset());
	    BlockState nodeState = world.getBlockState(nodePos);

	    if (node.hasBlockTag() && !nodeState.is(node.taggedBlocks())
		    || !nodeState.is(node.replaceState().getBlock())) {
		formed = false;
		break;
	    }
	}

	isFormed.setValue(formed);
    }

    public void formMultiblock() {
	Level world = getLevel();
	if (world == null)
	    return;

	Direction facing = getFacing().getOpposite();
	List<MultiblockSlaveNode> nodes = Multiblock.getNodes(world, getResourceKey(), facing);
	int index = 0;

	for (MultiblockSlaveNode node : nodes) {
	    BlockPos nodePos = getBlockPos().offset(node.offset());

	    slavePositions.addValue(nodePos, index);
	    world.setBlockAndUpdate(nodePos, node.placeState().setValue(VoltaicBlockStates.FACING, getFacing()));

	    if (world.getBlockEntity(nodePos) instanceof TileMultiblockSlave slaveBlockEntity) {
		slaveList.add(slaveBlockEntity);

		slaveBlockEntity.setDisguise(node.replaceState());
		slaveBlockEntity.controller.setValue(getBlockPos());
		slaveBlockEntity.index.setValue(index);
		slaveBlockEntity.renderModel.setValue(node.model());

		index++;
	    }
	}

	world.playSound(null, getBlockPos(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void destroyMultiblock(Level level) {
	isDestroyed = true;

	for (BlockPos pos : slavePositions.getValue()) {
	    if (level.getBlockEntity(pos) instanceof TileMultiblockSlave slave) {
		slave.onBlockDestroyed(level);
	    }
	}

	isFormed.setValue(false);
	slavePositions.wipeList();
	slaveList.clear();
	isDestroyed = false;

	level.playSound(null, getBlockPos(), SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public @Nullable ICapabilityElectrodynamic getSlaveCapabilityElectrodynamic(TileMultiblockSlave slave,
	    @Nullable Direction side) {
	return null;
    }

    public @Nullable IItemHandler getSlaveItemHandlerCapability(TileMultiblockSlave slave, @Nullable Direction side) {
	return null;
    }

    public @Nullable IFluidHandler getSlaveFluidHandlerCapability(TileMultiblockSlave slave, @Nullable Direction side) {
	return null;
    }

    public @Nullable IGasHandler getSlaveGasHandlerCapability(TileMultiblockSlave slave, @Nullable Direction side) {
	return null;
    }

    public int getSlaveComparatorSignal(Level level, TileMultiblockSlave slave) {
	return getComparatorSignal(level);
    }

    public int getSlaveDirectSignal(TileMultiblockSlave slave, Direction slaveDir) {
	return getDirectSignal(slaveDir);
    }

    public int getSlaveSignal(TileMultiblockSlave slave, Direction slaveDir) {
	return getSignal(slaveDir);
    }

    public boolean isSlavePoweredByRedstone(TileMultiblockSlave slave) {
	return isPoweredByRedstone();
    }

    @Override
    public void onBlockDestroyed(Level level) {
	super.onBlockDestroyed(level);
	if (!level.isClientSide()) {
	    destroyMultiblock(level);
	}
    }

    public void onSlaveBlockStateUpdate(TileMultiblockSlave slave, BlockState slaveOldState, BlockState slaveNewState) {

    }

    public void onSlaveEnergyChange(TileMultiblockSlave slave, ComponentElectrodynamic slaveCap) {

    }

    public void onSlaveEntityInside(TileMultiblockSlave slave, BlockState slaveState, Level level, BlockPos slavePos,
	    Entity slaveEntity) {

    }

    public void onSlaveFluidTankChange(TileMultiblockSlave slave, FluidTank slaveTank) {

    }

    public void onSlaveGasTankChange(TileMultiblockSlave slave, GasTank slaveTank) {

    }

    public void onSlaveInventoryChange(TileMultiblockSlave slave, ComponentInventory slaveInv, int slaveSlot) {

    }

    public InteractionResult slaveUseWithoutItem(Level level, TileMultiblockSlave slave, Player player,
	    BlockHitResult hitResult) {
	return useWithoutItem(level, player, hitResult);
    }

    public ItemInteractionResult slaveUseWithItem(Level level, TileMultiblockSlave slave, ItemStack used, Player player,
	    InteractionHand hand, BlockHitResult hit) {
	return useWithItem(level, used, player, hand, hit);
    }

    public void onSlaveNeighbourChanged(LevelReader reader, TileMultiblockSlave slave, BlockPos slaveNeighbor,
	    boolean blockStateTrigger) {

    }

    public void onSlavePlace(Level level, TileMultiblockSlave slave, BlockState slaveOldState, boolean isMoving) {

    }

    public void onSlaveDestroyed(Level level, TileMultiblockSlave slave) {
	if (isDestroyed)
	    return;

	if (!level.isClientSide) {
	    destroyMultiblock(level);
	}
    }

    public VoxelShape getSlaveShape(TileMultiblockSlave slave) {
	Level level = this.level;
	if (level == null)
	    return Shapes.empty();

	return Multiblock.getNodes(level, getResourceKey(), getFacing()).get(slave.index.getValue()).renderShape();
    }

    public abstract ResourceLocation getMultiblockId();

    public abstract ResourceKey<Multiblock> getResourceKey();

}
