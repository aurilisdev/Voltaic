package voltaic.api.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface TileEntitySupplier<T extends TileEntity> {

	public T create(IBlockReader reader);
	
}
