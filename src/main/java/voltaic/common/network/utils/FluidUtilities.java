package voltaic.common.network.utils;

import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.CapabilityUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidUtilities {

	public static boolean isFluidReceiver(BlockEntity acceptor, Direction dir) {
		return acceptor != null && acceptor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir).isPresent();
	}

	public static int receiveFluid(BlockEntity acceptor, Direction direction, FluidStack perReceiver, boolean debug) {

	    if(acceptor == null) {
	        return 0;
	    }
	    
	    IFluidHandler handler = acceptor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction).orElse(CapabilityUtils.EMPTY_FLUID);
	    
		if(handler == CapabilityUtils.EMPTY_FLUID) {
		    return 0;
		}

		for (int i = 0; i < handler.getTanks(); i++) {

			if (handler.isFluidValid(i, perReceiver)) {

				return handler.fill(perReceiver, debug ? FluidAction.SIMULATE : FluidAction.EXECUTE);

			}
		}

		return 0;
	}

	public static boolean canInputFluid(BlockEntity acceptor, Direction direction) {
		return isFluidReceiver(acceptor, direction);
	}

	public static void outputToPipe(GenericTile tile, FluidTank[] tanks, Direction... outputDirections) {

		Direction facing = tile.getFacing();

		for (Direction relative : outputDirections) {

			Direction direction = BlockEntityUtils.getRelativeSide(facing, relative);

			BlockEntity faceTile = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(direction));

			if (faceTile == null) {
				continue;
			}

			IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).orElse(CapabilityUtils.EMPTY_FLUID);

			if(handler == null) {
			    continue;
			}

			for (FluidTank fluidTank : tanks) {

				FluidStack tankFluid = fluidTank.getFluid();

				int amtAccepted = handler.fill(tankFluid, FluidAction.EXECUTE);

				FluidStack taken = new FluidStack(tankFluid.getFluid(), amtAccepted);

				fluidTank.drain(taken, FluidAction.EXECUTE);
			}
		}
	}

	public static void drainItem(GenericTile tile, FluidTank[] tanks) {

		ComponentInventory inv = tile.getComponent(IComponentType.Inventory);

		int bucketIndex = inv.getInputBucketStartIndex();

		int size = inv.getInputBucketContents().size();

		if (tanks.length < size) {

			return;

		}

		int index;

		for (int i = 0; i < size; i++) {

			index = bucketIndex + i;

			FluidTank tank = tanks[i];
			ItemStack stack = inv.getItem(index);

			int room = tank.getSpace();

			if (stack.isEmpty() || room <= 0) {
				continue;
			}

			IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);
			
			if(handler == null) {
			    continue;
			}

			FluidStack containerFluid = handler.drain(room, FluidAction.SIMULATE);

			if (containerFluid.isEmpty() || !tank.isFluidValid(containerFluid)) {
				continue;
			}

			int accepted = tank.fill(containerFluid, FluidAction.EXECUTE);

			handler.drain(accepted, FluidAction.EXECUTE);

			inv.setItem(index, handler.getContainer());

		}

	}

	public static void fillItem(GenericTile tile, FluidTank[] tanks) {

		ComponentInventory inv = tile.getComponent(IComponentType.Inventory);

		int bucketIndex = inv.getOutputBucketStartIndex();

		int size = inv.getOutputBucketContents().size();

		if (tanks.length < size) {

			return;

		}

		int index;

		for (int i = 0; i < size; i++) {

			index = bucketIndex + i;

			ItemStack stack = inv.getItem(index);

			FluidTank tank = tanks[i];

			if (stack.isEmpty() || tank.isEmpty()) {
				continue;
			}
			
			IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(CapabilityUtils.EMPTY_FLUID_ITEM);
			
			if(handler == null) {
			    continue;
			}

			FluidStack fluid = tank.getFluid();

			int taken = handler.fill(fluid, FluidAction.EXECUTE);

			tank.drain(taken, FluidAction.EXECUTE);

			inv.setItem(index, handler.getContainer());
		}
	}

	@Deprecated(since = "don't set a filter if you want to allow for all fluids")
	public static Fluid[] getAllRegistryFluids() {
	    return BuiltInRegistries.FLUID.stream().toArray(Fluid[]::new);
	}

}
