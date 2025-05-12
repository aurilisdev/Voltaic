package voltaic.api.network.cable;

import net.minecraft.tileentity.TileEntity;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.tile.types.GenericRefreshingConnectTile;

public interface IAbstractCable<CONDUCTORTYPE, T extends AbstractNetwork<? extends GenericRefreshingConnectTile<?, ?, ?>, ?, ?, ?>> {

	void removeFromNetwork();

	T getNetwork();

	void createNetworkFromThis();

	void setNetwork(T aValueNetwork);

	TileEntity[] getConectedRecievers();

	TileEntity[] getConnectedCables();

	CONDUCTORTYPE getCableType();

	double getMaxTransfer();
}
