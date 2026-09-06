package voltaic.prefab.tile.types;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicSounds;

public class GenericMaterialTile extends GenericTile {

    public GenericMaterialTile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
	super(tileEntityTypeIn, worldPos, blockState);
    }

    @Override
    public ItemInteractionResult useWithItem(Level level, ItemStack used, Player player, InteractionHand hand,
	    BlockHitResult hit) {
	IFluidHandlerItem handlerFluidItem = used.getCapability(Capabilities.FluidHandler.ITEM);

	if (handlerFluidItem != null && hasComponent(IComponentType.FluidHandler)) {
	    IComponentFluidHandler fluidHandler = this.<IComponentFluidHandler>getComponent(IComponentType.FluidHandler)
		    .get();

	    for (FluidTank tank : fluidHandler.getInputTanks()) {
		int space = tank.getSpace();
		FluidStack containedFluid = handlerFluidItem.drain(space, FluidAction.SIMULATE);

		if (containedFluid.isEmpty()) {
		    continue;
		}

		if (!level.isClientSide) {
		    tank.fill(containedFluid, FluidAction.EXECUTE);

		    if (!player.isCreative()) {
			handlerFluidItem.drain(space, FluidAction.EXECUTE);
		    }

		    level.playSound(null, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1, 1);
		    player.setItemInHand(hand, handlerFluidItem.getContainer());
		}

		return ItemInteractionResult.CONSUME;
	    }

	    for (FluidTank tank : fluidHandler.getOutputTanks()) {
		FluidStack tankFluid = tank.getFluid();
		int taken = handlerFluidItem.fill(tankFluid, FluidAction.EXECUTE);

		if (taken <= 0) {
		    continue;
		}

		if (!level.isClientSide) {
		    tank.drain(taken, FluidAction.EXECUTE);
		    level.playSound(null, player.blockPosition(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1, 1);
		    player.setItemInHand(hand, handlerFluidItem.getContainer());
		}

		return ItemInteractionResult.CONSUME;
	    }
	}

	IGasHandlerItem handlerGasItem = used.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM);

	if (handlerGasItem != null && hasComponent(IComponentType.GasHandler)) {
	    IComponentGasHandler gasHandler = this.<IComponentGasHandler>getComponent(IComponentType.GasHandler).get();

	    for (GasTank tank : gasHandler.getInputTanks()) {
		int space = tank.getSpace();
		GasStack containedGas = handlerGasItem.drain(space, GasAction.SIMULATE);

		if (containedGas.isEmpty()) {
		    continue;
		}

		if (!level.isClientSide) {
		    tank.fill(containedGas, GasAction.EXECUTE);

		    if (!player.isCreative()) {
			handlerGasItem.drain(space, GasAction.EXECUTE);
		    }

		    level.playSound(null, player.blockPosition(), VoltaicSounds.SOUND_PRESSURERELEASE.get(),
			    SoundSource.PLAYERS, 1, 1);

		    player.setItemInHand(hand, handlerGasItem.getContainer());
		}

		return ItemInteractionResult.CONSUME;
	    }

	    for (GasTank tank : gasHandler.getOutputTanks()) {
		GasStack tankGas = tank.getGas();
		int taken = handlerGasItem.fill(tankGas, GasAction.EXECUTE);

		if (taken <= 0) {
		    continue;
		}

		if (!level.isClientSide) {
		    tank.drain(taken, GasAction.EXECUTE);

		    level.playSound(null, player.blockPosition(), VoltaicSounds.SOUND_PRESSURERELEASE.get(),
			    SoundSource.PLAYERS, 1, 1);

		    player.setItemInHand(hand, handlerGasItem.getContainer());
		}

		return ItemInteractionResult.CONSUME;
	    }
	}

	return super.useWithItem(level, used, player, hand, hit);
    }
}