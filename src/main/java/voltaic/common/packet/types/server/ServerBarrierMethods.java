package voltaic.common.packet.types.server;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.api.item.IItemElectric;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.IPropertyHolderTile;

public class ServerBarrierMethods {

    public static void handleSendUpdatePropertiesServer(Level level, BlockPos tilePos, CompoundTag data, int index) {
	ServerLevel world = (ServerLevel) level;
	if (world == null) {
	    return;
	}
	BlockEntity tile = world.getBlockEntity(tilePos);
	if (tile instanceof IPropertyHolderTile holder) {
	    holder.getPropertyManager().loadDataFromClient(index, data);
	}
    }

    public static void handleSwapBattery(Level level, UUID playerId) {
	ServerLevel world = (ServerLevel) level;
	if (world == null) {
	    return;
	}
	Player player = world.getPlayerByUUID(playerId);
	ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
	if (!handItem.isEmpty() && handItem.getItem() instanceof IItemElectric electric) {
	    electric.swapBatteryPackFirstItem(handItem, player);
	}
    }

    public static void handleUpdateCarriedItemServer(Level level, ItemStack carriedItem, BlockPos tilePos,
	    UUID playerId) {
	ServerLevel world = (ServerLevel) level;
	if (world == null) {
	    return;
	}
	GenericTile tile = (GenericTile) world.getBlockEntity(tilePos);
	if (tile != null) {
	    tile.updateCarriedItemInContainer(carriedItem, playerId);
	}
    }

}
