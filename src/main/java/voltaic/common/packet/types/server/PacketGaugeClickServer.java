package voltaic.common.packet.types.server;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkEvent;
import voltaic.api.fluid.PropertyFluidTank;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.gas.PropertyGasTank;
import voltaic.common.packet.NetworkHandler;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.tile.types.GenericGasTile;
import voltaic.registers.VoltaicCapabilities;

/**
 * Sent when a player clicks a fluid/gas gauge while carrying an item, so the
 * server performs the tank transfer authoritatively.
 */
public record PacketGaugeClickServer(BlockPos pos, boolean gas, boolean input, int index) {

    public static final int CONDENSED_INDEX = -1;
    private static final double MAX_DISTANCE_SQR = 64.0;

    public static void encode(PacketGaugeClickServer message, FriendlyByteBuf buffer) {
	buffer.writeBlockPos(message.pos);
	buffer.writeBoolean(message.gas);
	buffer.writeBoolean(message.input);
	buffer.writeVarInt(message.index);
    }

    public static PacketGaugeClickServer decode(FriendlyByteBuf buffer) {
	return new PacketGaugeClickServer(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean(),
		buffer.readVarInt());
    }

    public static <C extends IComponent, T> void sendClickToServer(GenericTile owner, IComponentType type, boolean gas,
	    T tank, Function<C, T[]> inputTanks, Function<C, T[]> outputTanks) {

	C component = owner.<C>getComponent(type);
	T[] inputs = inputTanks.apply(component);
	for (int i = 0; i < inputs.length; i++) {
	    if (inputs[i] == tank) {
		NetworkHandler.CHANNEL.sendToServer(new PacketGaugeClickServer(owner.getBlockPos(), gas, true, i));
		return;
	    }
	}

	T[] outputs = outputTanks.apply(component);
	for (int i = 0; i < outputs.length; i++) {
	    if (outputs[i] == tank) {
		NetworkHandler.CHANNEL.sendToServer(new PacketGaugeClickServer(owner.getBlockPos(), gas, false, i));
		return;
	    }
	}
    }

    public static void handle(PacketGaugeClickServer message, Supplier<NetworkEvent.Context> contextSupplier) {

	NetworkEvent.Context context = contextSupplier.get();
	context.enqueueWork(() -> handleServer(message, context.getSender()));
	context.setPacketHandled(true);
    }

    private static void handleServer(PacketGaugeClickServer message, @Nullable ServerPlayer player) {
	if (player == null || message.index < CONDENSED_INDEX)
	    return;

	if (player.distanceToSqr(message.pos.getX() + 0.5, message.pos.getY() + 0.5,
		message.pos.getZ() + 0.5) > MAX_DISTANCE_SQR)
	    return;

	// Do not load chunks based on a position supplied by the client.
	if (!player.level().hasChunkAt(message.pos))
	    return;

	if (!(player.level().getBlockEntity(message.pos) instanceof GenericTile tile))
	    return;

	if (!(player.containerMenu instanceof GenericContainerBlockEntity<?> menu) || menu.getSafeHost() != tile)
	    return;

	ItemStack carried = player.containerMenu.getCarried();
	if (carried.isEmpty())
	    return;

	ItemStack result;
	if (message.index == CONDENSED_INDEX) {
	    result = handleCondensed(tile, carried);
	} else if (message.gas) {
	    result = handleGas(tile, carried, message);
	} else {
	    result = handleFluid(tile, carried, message);
	}

	// Always reply to correct the client's predicted carried item.
	tile.updateCarriedItemInContainer(result == null ? carried : result, player.getUUID());
    }

    private static @Nullable ItemStack handleCondensed(GenericTile tile, ItemStack carried) {

	if (!(tile instanceof GenericGasTile gasTile))
	    return null;

	IFluidHandlerItem handler = carried.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
	if (handler == null)
	    return null;

	FluidStack condensed = gasTile.condensedFluidFromGas.getValue();
	if (condensed.isEmpty())
	    return null;

	int taken = handler.fill(condensed.copy(), IFluidHandler.FluidAction.EXECUTE);
	if (taken <= 0)
	    return null;

	FluidStack remaining = condensed.copy();
	remaining.shrink(taken);
	gasTile.condensedFluidFromGas.setValue(remaining);
	return handler.getContainer();
    }

    private static @Nullable ItemStack handleFluid(GenericTile tile, ItemStack carried,
	    PacketGaugeClickServer message) {

	IComponentFluidHandler component = tile.<IComponentFluidHandler>getComponent(IComponentType.FluidHandler);
	if (component == null)
	    return null;

	PropertyFluidTank[] tanks = message.input ? component.getInputTanks() : component.getOutputTanks();
	if (message.index < 0 || message.index >= tanks.length)
	    return null;

	PropertyFluidTank tank = tanks[message.index];

	IFluidHandlerItem handler = carried.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
	if (handler == null)
	    return null;

	int taken = handler.fill(tank.getFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
	if (taken > 0) {
	    tank.drain(taken, IFluidHandler.FluidAction.EXECUTE);
	    return handler.getContainer();
	}

	for (int i = 0; i < handler.getTanks(); i++) {
	    FluidStack fluid = handler.getFluidInTank(i);
	    taken = tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
	    if (taken <= 0)
		continue;

	    handler.drain(taken, IFluidHandler.FluidAction.EXECUTE);
	    return handler.getContainer();
	}

	return null;
    }

    private static @Nullable ItemStack handleGas(GenericTile tile, ItemStack carried, PacketGaugeClickServer message) {

	IComponentGasHandler component = tile.<IComponentGasHandler>getComponent(IComponentType.GasHandler);
	if (component == null)
	    return null;

	PropertyGasTank[] tanks = message.input ? component.getInputTanks() : component.getOutputTanks();
	if (message.index < 0 || message.index >= tanks.length)
	    return null;

	PropertyGasTank tank = tanks[message.index];

	IGasHandlerItem handler = carried.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM).orElse(null);
	if (handler == null)
	    return null;

	int taken = handler.fill(tank.getGas().copy(), GasAction.EXECUTE);
	if (taken > 0) {
	    tank.drain(taken, GasAction.EXECUTE);
	    return handler.getContainer();
	}

	for (int i = 0; i < handler.getTanks(); i++) {
	    GasStack gas = handler.getGasInTank(i);
	    taken = tank.fill(gas, GasAction.EXECUTE);
	    if (taken <= 0)
		continue;

	    handler.drain(taken, GasAction.EXECUTE);
	    return handler.getContainer();
	}

	return null;
    }
}