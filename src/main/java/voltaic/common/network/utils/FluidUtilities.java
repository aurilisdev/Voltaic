package voltaic.common.network.utils;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.BlockEntityUtils;

public class FluidUtilities {

    public static boolean isFluidReceiver(@Nullable BlockEntity acceptor, Direction dir) {
	if (acceptor == null)
	    return false;

	Level level = acceptor.getLevel();
	if (level == null)
	    return false;

	return level.getCapability(Capabilities.FluidHandler.BLOCK, acceptor.getBlockPos(), acceptor.getBlockState(),
		acceptor, dir) != null;
    }

    public static int receiveFluid(BlockEntity acceptor, Direction direction, FluidStack perReceiver, boolean debug) {
	Level level = acceptor.getLevel();
	if (level == null)
	    return 0;

	IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, acceptor.getBlockPos(),
		acceptor.getBlockState(), acceptor, direction);
	if (handler == null)
	    return 0;

	for (int i = 0; i < handler.getTanks(); i++) {
	    if (handler.isFluidValid(i, perReceiver))
		return handler.fill(perReceiver, debug ? FluidAction.SIMULATE : FluidAction.EXECUTE);
	}
	return 0;
    }

    public static boolean canInputFluid(@Nullable BlockEntity acceptor, Direction direction) {
	return isFluidReceiver(acceptor, direction);
    }

    public static void outputToPipe(GenericTile tile, FluidTank[] tanks, Direction... outputDirections) {
	Direction facing = tile.getFacing();
	Level level = tile.getLevel();
	if (level == null)
	    return;

	for (Direction relative : outputDirections) {
	    Direction direction = BlockEntityUtils.getRelativeSide(facing, relative);

	    BlockEntity faceTile = level.getBlockEntity(tile.getBlockPos().relative(direction));
	    if (faceTile == null) {
		continue;
	    }

	    IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, faceTile.getBlockPos(),
		    faceTile.getBlockState(), faceTile, direction.getOpposite());
	    if (handler == null) {
		continue;
	    }

	    for (FluidTank fluidTank : tanks) {
		FluidStack tankFluid = fluidTank.getFluid();
		int amountAccepted = handler.fill(tankFluid, FluidAction.EXECUTE);
		FluidStack taken = new FluidStack(tankFluid.getFluid(), amountAccepted);

		fluidTank.drain(taken, FluidAction.EXECUTE);
	    }
	}
    }

    public static void drainItem(GenericTile tile, FluidTank[] tanks) {
	ComponentInventory inv = tile.<ComponentInventory>getComponent(IComponentType.Inventory).orElse(null);
	if (inv == null)
	    return;

	int bucketIndex = inv.getInputBucketStartIndex();
	int size = inv.getInputBucketContents().size();
	if (tanks.length < size)
	    return;

	for (int i = 0; i < size; i++) {
	    int index = bucketIndex + i;
	    FluidTank tank = tanks[i];
	    ItemStack stack = inv.getItem(index);
	    int room = tank.getSpace();

	    if (stack.isEmpty() || room <= 0) {
		continue;
	    }

	    IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
	    if (handler == null) {
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
	ComponentInventory inv = tile.<ComponentInventory>getComponent(IComponentType.Inventory).orElse(null);
	if (inv == null)
	    return;

	int bucketIndex = inv.getOutputBucketStartIndex();
	int size = inv.getOutputBucketContents().size();
	if (tanks.length < size)
	    return;

	for (int i = 0; i < size; i++) {
	    int index = bucketIndex + i;

	    ItemStack stack = inv.getItem(index);
	    FluidTank tank = tanks[i];
	    if (stack.isEmpty() || tank.isEmpty()) {
		continue;
	    }

	    IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
	    if (handler == null) {
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
