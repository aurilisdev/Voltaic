package voltaic.api.tile;

import net.minecraft.tileentity.TileEntity;

public interface TileEntitySupplier<T extends TileEntity> {

	public T create();
	
}
