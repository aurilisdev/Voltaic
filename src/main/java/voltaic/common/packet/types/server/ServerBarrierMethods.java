package voltaic.common.packet.types.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import voltaic.prefab.tile.IPropertyHolderTile;

public class ServerBarrierMethods {

    public static void handleSendUpdatePropertiesServer(World level, BlockPos tilePos, CompoundNBT data, int index) {
        ServerWorld world = (ServerWorld) level;
        if (world == null) {
            return;
        }
        TileEntity tile = world.getBlockEntity(tilePos);
        if (tile instanceof IPropertyHolderTile) {
            ((IPropertyHolderTile) tile).getPropertyManager().loadDataFromClient(index, data);
        }
    }

}
