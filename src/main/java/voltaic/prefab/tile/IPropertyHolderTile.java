package voltaic.prefab.tile;

import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.prefab.properties.PropertyManager;

public interface IPropertyHolderTile {
	PropertyManager getPropertyManager();

	default BlockEntity getTile() {
		return (BlockEntity) this;
	}
}
