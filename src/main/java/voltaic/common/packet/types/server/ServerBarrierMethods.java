package voltaic.common.packet.types.server;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import voltaic.api.item.IItemElectric;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.IPropertyHolderTile;

public class ServerBarrierMethods {

    public static void handleSendUpdatePropertiesServer(ServerLevel serverLevel, BlockPos tilePos, CompoundTag data,
	    int index) {
	if (serverLevel.getBlockEntity(tilePos) instanceof IPropertyHolderTile holder) {
	    holder.getPropertyManager().loadDataFromClient(serverLevel, index, data);
	}
    }

    public static void handleSwapBattery(ServerLevel serverLevel, UUID playerId) {
	Player player = serverLevel.getPlayerByUUID(playerId);
	if (player == null)
	    return;
	ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
	if (!handItem.isEmpty() && handItem.getItem() instanceof IItemElectric electric) {
	    electric.swapBatteryPackFirstItem(handItem, player);
	}
    }

    public static void handleUpdateCarriedItemServer(ServerLevel serverLevel, ItemStack carriedItem, BlockPos tilePos,
	    UUID playerId) {
	if (serverLevel.getBlockEntity(tilePos) instanceof GenericTile genericTile) {
	    genericTile.updateCarriedItemInContainer(carriedItem, playerId);
	}
    }

}
