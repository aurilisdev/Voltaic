package voltaic.api.multiblock.assemblybased;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.GasTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.Scheduler;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * @author skip999
 */
public abstract class TileMultiblockController extends TileReplaceable {

	public final List<TileMultiblockSlave> slaveList = new ArrayList<>();

	public final ListProperty<BlockPos> slavePositions = property(new ListProperty<>(PropertyTypes.BLOCK_POS_LIST, "slavepositions", new ArrayList<>())).onTileLoaded((prop) -> {

		if (level.isClientSide()) {
			return;
		}

		Scheduler.schedule(2, () -> {
			slaveList.clear();
			prop.getValue().forEach(blockPos -> {
				slaveList.add((TileMultiblockSlave) level.getBlockEntity(worldPosition.offset(blockPos)));
			});
		});

	});

	public final SingleProperty<Boolean> isFormed = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "isformed", false));

	private boolean isDestroyed = false;

	public TileMultiblockController(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
		super(tileEntityTypeIn, worldPos, blockState);

		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickCommon(this::tickCommon).tickClient(this::tickClient));

	}

	public void tickServer(ComponentTickable tickable) {

	}

	public void tickCommon(ComponentTickable tickable) {

	}

	public void tickClient(ComponentTickable tickable) {

	}

	public void checkFormed() {

		Direction facing = getFacing().getOpposite();

		List<MultiblockSlaveNode> nodes = Multiblock.getNodes(level, getResourceKey(), facing);

		BlockPos nodePos;
		BlockState nodeState;

		boolean formed = true;

		for (MultiblockSlaveNode node : nodes) {

			nodePos = getBlockPos().offset(node.offset());

			nodeState = level.getBlockState(nodePos);

			if (node.hasBlockTag() && !nodeState.is(node.taggedBlocks()) || !nodeState.is(node.replaceState().getBlock())) {

				formed = false;
				break;
			}
			/*

			if (node.hasBlockTag()) {

				for (net.minecraft.world.level.block.state.properties.Property<?> prop : node.placeState().getProperties()) {

					if (!nodeState.hasProperty(prop)) {

						formed = false;
						break;
					}

				}

				if (!formed) {
					break;
				}

			}

			 */

		}

		isFormed.setValue(formed);

	}

	public void formMultiblock() {

		Direction facing = getFacing().getOpposite();

		List<MultiblockSlaveNode> nodes = Multiblock.getNodes(level, getResourceKey(), facing);

		BlockPos nodePos;

		TileMultiblockSlave slave;

		int index = 0;

		for (MultiblockSlaveNode node : nodes) {

			nodePos = getBlockPos().offset(node.offset());

			slavePositions.addValue(nodePos, index);

			level.setBlockAndUpdate(nodePos, node.placeState().setValue(VoltaicBlockStates.FACING, getFacing()));

			slave = (TileMultiblockSlave) level.getBlockEntity(nodePos);

			slaveList.add(slave);

			slave.setDisguise(node.replaceState());

			slave.controller.setValue(getBlockPos());

			slave.index.setValue(index);

			slave.renderModel.setValue(node.model());

			index++;

		}

		level.playSound(null, getBlockPos(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

	}

	public void destroyMultiblock() {

		isDestroyed = true;

		for (BlockPos pos : slavePositions.getValue()) {

			if(level.getBlockEntity(pos) instanceof TileMultiblockSlave slave) {
				slave.onBlockDestroyed();
			}

		}

		isFormed.setValue(false);

		slavePositions.wipeList();
		
		slaveList.clear();
		
		isDestroyed = false;

		level.playSound(null, getBlockPos(), SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

	}

	public @Nullable ICapabilityElectrodynamic getSlaveCapabilityElectrodynamic(TileMultiblockSlave slave, @Nullable Direction side) {
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

	public int getSlaveComparatorSignal(TileMultiblockSlave slave) {
		return getComparatorSignal();
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
	public void onBlockDestroyed() {
		super.onBlockDestroyed();
		if (!level.isClientSide()) {
			destroyMultiblock();
		}
	}

	public void onSlaveBlockStateUpdate(TileMultiblockSlave slave, BlockState slaveOldState, BlockState slaveNewState) {

	}

	public void onSlaveEnergyChange(TileMultiblockSlave slave, ComponentElectrodynamic slaveCap) {

	}

	public void onSlaveEntityInside(TileMultiblockSlave slave, BlockState slaveState, Level level, BlockPos slavePos, Entity slaveEntity) {

	}

	public void onSlaveFluidTankChange(TileMultiblockSlave slave, FluidTank slaveTank) {

	}

	public void onSlaveGasTankChange(TileMultiblockSlave slave, GasTank slaveTank) {

	}

	public void onSlaveInventoryChange(TileMultiblockSlave slave, ComponentInventory slaveInv, int slaveSlot) {

	}

	public InteractionResult slaveUseWithoutItem(TileMultiblockSlave slave, Player player, BlockHitResult hitResult) {
		return useWithoutItem(player, hitResult);
	}

	public ItemInteractionResult slaveUseWithItem(TileMultiblockSlave slave, ItemStack used, Player player, InteractionHand hand, BlockHitResult hit) {
		return useWithItem(used, player, hand, hit);
	}

	public void onSlaveNeightborChanged(TileMultiblockSlave slave, BlockPos slaveNeighbor, boolean blockStateTrigger) {

	}

	public void onSlavePlace(TileMultiblockSlave slave, BlockState slaveOldState, boolean isMoving) {

	}

	public void onSlaveDestroyed(TileMultiblockSlave slave) {
		if (isDestroyed) {
			return;
		}
		if (!level.isClientSide) {
			destroyMultiblock();
		}
	}

	public VoxelShape getSlaveShape(TileMultiblockSlave slave) {
		return Multiblock.getNodes(level, getResourceKey(), getFacing()).get(slave.index.getValue()).renderShape();
	}

	public abstract ResourceLocation getMultiblockId();

	public abstract ResourceKey<Multiblock> getResourceKey();

}
