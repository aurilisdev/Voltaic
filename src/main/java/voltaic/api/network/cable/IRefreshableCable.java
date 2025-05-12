package voltaic.api.network.cable;

import net.minecraft.util.Direction;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.tile.types.GenericRefreshingConnectTile;

public interface IRefreshableCable<CONDUCTORTYPE, T extends AbstractNetwork<? extends GenericRefreshingConnectTile<?, ?, ?>, ?, ?, ?>> extends IAbstractCable<CONDUCTORTYPE, T> {

	void updateNetwork(Direction...dirs);

	void destroyViolently();

}
