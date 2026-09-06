package voltaic.api.multiblock.assemblybased;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.Voltaic;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandler;
import voltaic.client.model.block.modelproperties.ModelPropertySlaveNode;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicTiles;

public class TileMultiblockSlave extends TileReplaceable {

    public final SingleProperty<BlockPos> controller = property(new SingleProperty<>(getPropertyManager(),
	    PropertyTypes.BLOCK_POS, "controllerpos", BlockEntityUtils.OUT_OF_REACH)).onTileLoaded(prop -> {
		Level level = getLevel();
		if (level == null || !level.isClientSide()
			|| level.getBlockEntity(prop.getValue()) instanceof TileMultiblockController)
		    return;

		Minecraft.getInstance().execute(() -> {
		    Level currentLevel = getLevel();
		    BlockState disguise = getDisguise();
		    if (currentLevel != null && disguise != null) {
			currentLevel.setBlockAndUpdate(getBlockPos(), disguise);
		    }
		});
	    });

    public final SingleProperty<Integer> index = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "nodeindex", -1));

    public final SingleProperty<ResourceLocation> renderModel = property(new SingleProperty<>(getPropertyManager(),
	    PropertyTypes.RESOURCE_LOCATION, "model", MultiblockSlaveNode.NOMODEL));

    private boolean destroyed = false;

    public TileMultiblockSlave(BlockPos worldPos, BlockState blockState) {
	super(VoltaicTiles.TILE_MULTIBLOCK_SLAVE.get(), worldPos, blockState);
	addComponent(new ComponentTickable(this).tickServer(this::tickServer));
    }

    private void tickServer(Level level, ComponentTickable componentTickable) {
	if (disguisedBlock.getValue() == 27068) {
	    Voltaic.LOGGER.info(getDisguise());
	}
    }

    @Nullable
    private TileMultiblockController getController() {
	Level level = getLevel();
	if (level == null)
	    return null;

	return getController(level);
    }

    @Nullable
    private TileMultiblockController getController(Level level) {
	if (level.getBlockEntity(controller.getValue()) instanceof TileMultiblockController controller)
	    return controller;
	return null;
    }

    @Override
    public int getComparatorSignal(Level level) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveComparatorSignal(level, this);

	return super.getComparatorSignal(level);
    }

    @Override
    public int getDirectSignal(Direction dir) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveDirectSignal(this, dir);

	return super.getDirectSignal(dir);
    }

    @Override
    public int getSignal(Direction dir) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveSignal(this, dir);

	return super.getSignal(dir);
    }

    @Override
    public boolean isPoweredByRedstone() {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.isSlavePoweredByRedstone(this);

	return super.isPoweredByRedstone();
    }

    @Override
    public void onBlockDestroyed(Level level) {
	super.onBlockDestroyed(level);
	if (destroyed)
	    return;

	destroyed = true;
	BlockState disguise = getDisguise();
	if (!level.isClientSide && disguise != null) {
	    level.setBlockAndUpdate(getBlockPos(), disguise);
	}

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveDestroyed(level, this);
	}
    }

    @Override
    public void onBlockStateUpdate(Level level, BlockState oldState, BlockState newState) {
	super.onBlockStateUpdate(level, oldState, newState);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveBlockStateUpdate(this, oldState, newState);
	}
    }

    @Override
    public void onEnergyChange(ComponentElectrodynamic cap) {
	super.onEnergyChange(cap);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveEnergyChange(this, cap);
	}
    }

    @Override
    public void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
	super.onEntityInside(state, level, pos, entity);

	TileMultiblockController controller = getController(level);
	if (controller != null) {
	    controller.onSlaveEntityInside(this, state, level, pos, entity);
	}
    }

    @Override
    public void onFluidTankChange(FluidTank tank) {
	super.onFluidTankChange(tank);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveFluidTankChange(this, tank);
	}
    }

    @Override
    public void onGasTankChange(GasTank tank) {
	super.onGasTankChange(tank);

	TileMultiblockController controller = getController();

	if (controller != null) {
	    controller.onSlaveGasTankChange(this, tank);
	}
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int index) {
	super.onInventoryChange(inv, index);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveInventoryChange(this, inv, index);
	}
    }

    @Override
    public InteractionResult useWithoutItem(Level level, Player player, BlockHitResult hit) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.slaveUseWithoutItem(level, this, player, hit);

	return super.useWithoutItem(level, player, hit);
    }

    @Override
    public ItemInteractionResult useWithItem(Level level, ItemStack used, Player player, InteractionHand hand,
	    BlockHitResult hit) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.slaveUseWithItem(level, this, used, player, hand, hit);

	return super.useWithItem(level, used, player, hand, hit);
    }

    @Override
    public void onNeighbourChanged(LevelReader reader, BlockPos neighbor, boolean blockStateTrigger) {
	super.onNeighbourChanged(reader, neighbor, blockStateTrigger);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlaveNeighbourChanged(reader, this, neighbor, blockStateTrigger);
	}
    }

    @Override
    public void onPlace(Level level, BlockState state, boolean isMoving) {
	super.onPlace(level, state, isMoving);

	TileMultiblockController controller = getController();
	if (controller != null) {
	    controller.onSlavePlace(level, this, state, isMoving);
	}
    }

    public VoxelShape getShape() {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveShape(this);

	return Shapes.block();
    }

    @Override
    public @Nullable ICapabilityElectrodynamic getElectrodynamicCapability(@Nullable Direction side) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveCapabilityElectrodynamic(this, side);

	return super.getElectrodynamicCapability(side);
    }

    @Override
    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveItemHandlerCapability(this, side);

	return super.getItemHandlerCapability(side);
    }

    @Override
    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveFluidHandlerCapability(this, side);

	return super.getFluidHandlerCapability(side);
    }

    @Override
    public @Nullable IGasHandler getGasHandlerCapability(@Nullable Direction side) {
	TileMultiblockController controller = getController();
	if (controller != null)
	    return controller.getSlaveGasHandlerCapability(this, side);

	return super.getGasHandlerCapability(side);
    }

    @Override
    public ModelData getModelData() {
	return ModelData.builder().with(ModelPropertySlaveNode.INSTANCE,
		new ModelPropertySlaveNode.SlaveNodeWrapper(renderModel.getValue(), getFacing())).build();
    }
}