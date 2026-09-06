package voltaic.api.network.cable;

import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.tile.types.GenericRefreshingConnectTile;

public interface IAbstractCable<CONDUCTORTYPE, NETWORK extends AbstractNetwork<? extends GenericRefreshingConnectTile<?, ?, ?>, ?, ?, ?>> {

    void removeFromNetwork();

    NETWORK getNetwork();

    NETWORK createNetworkFromThis();

    void setNetwork(NETWORK aValueNetwork);

    BlockEntity[] getConectedRecievers();

    BlockEntity[] getConnectedCables();

    CONDUCTORTYPE getCableType();

    double getMaxTransfer();
}
