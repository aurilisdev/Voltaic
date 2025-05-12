package voltaic.prefab.tile;

import net.minecraft.tileentity.TileEntity;
import voltaic.prefab.properties.PropertyManager;

public interface IPropertyHolderTile {
	PropertyManager getPropertyManager();

	default TileEntity getTile() {
		return (TileEntity) this;
	}
}
