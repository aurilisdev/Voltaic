package voltaic.prefab.tile.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.network.cable.IRefreshableCable;
import voltaic.common.block.connect.EnumConnectType;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.utilities.Scheduler;

public abstract class GenericRefreshingConnectTile<CABLETYPE, CONDUCTOR extends GenericRefreshingConnectTile<CABLETYPE, CONDUCTOR, NETWORK>, NETWORK extends AbstractNetwork<CONDUCTOR, CABLETYPE, ?, NETWORK>>
	extends GenericConnectTile implements IRefreshableCable<CABLETYPE, NETWORK> {

    private final EnumConnectType[] previousConnections = { EnumConnectType.NONE, EnumConnectType.NONE,
	    EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE };
    protected final BlockEntity[] recieverConnections = new BlockEntity[6];
    protected final BlockEntity[] cableConnections = new BlockEntity[6];
    protected final HashSet<CONDUCTOR> connectionSet = new HashSet<>();

    @Nullable
    private NETWORK network;

    public boolean isQueued = false;

    public GenericRefreshingConnectTile(BlockEntityType<?> tile, BlockPos pos, BlockState state) {
	super(tile, pos, state);
    }

    public Pair<List<UpdatedReceiver>, List<UpdatedConductor<CONDUCTOR>>> updateAdjacent(Level level,
	    Direction[] dirs) {
	boolean flag = false;
	int ordinal;
	EnumConnectType prevConnection, connection;

	List<UpdatedReceiver> updatedRecievers = new ArrayList<>();
	List<UpdatedConductor<CONDUCTOR>> updatedConductors = new ArrayList<>();

	for (Direction dir : dirs) {
	    ordinal = dir.ordinal();
	    connection = connectionsArr[ordinal];
	    prevConnection = previousConnections[ordinal];

	    BlockEntity entity = level.getBlockEntity(worldPosition.relative(dir));
	    BlockEntity previousEntity = switch (prevConnection) {
	    case WIRE -> cableConnections[ordinal];
	    case INVENTORY -> recieverConnections[ordinal];
	    default -> null;
	    };
	    BlockEntity currentEntity = connection == EnumConnectType.NONE ? null : entity;

	    if (prevConnection == connection && previousEntity == currentEntity) {
		continue;
	    }

	    if (prevConnection == EnumConnectType.WIRE && previousEntity != null) {
		updatedConductors.add(new UpdatedConductor<>((CONDUCTOR) previousEntity, true));
	    } else if (prevConnection == EnumConnectType.INVENTORY && previousEntity != null) {
		updatedRecievers.add(new UpdatedReceiver(previousEntity, true, dir));
	    }

	    if (connection == EnumConnectType.WIRE && currentEntity != null) {
		updatedConductors.add(new UpdatedConductor<>((CONDUCTOR) currentEntity, false));
	    } else if (connection == EnumConnectType.INVENTORY && currentEntity != null) {
		updatedRecievers.add(new UpdatedReceiver(currentEntity, false, dir));
	    }

	    cableConnections[ordinal] = null;
	    recieverConnections[ordinal] = null;

	    if (connection == EnumConnectType.WIRE) {
		cableConnections[ordinal] = currentEntity;
	    } else if (connection == EnumConnectType.INVENTORY) {
		recieverConnections[ordinal] = currentEntity;
	    }

	    previousConnections[ordinal] = connection;
	    flag = true;
	}
	if (flag) {
	    connectionSet.clear();
	    for (BlockEntity entity : cableConnections) {
		if (entity != null) {
		    connectionSet.add((CONDUCTOR) entity);
		}
	    }
	}

	return Pair.of(updatedRecievers, updatedConductors);
    }

    @Override
    public NETWORK getNetwork() {
	NETWORK network = this.network;
	if (network == null) {
	    network = createNetworkFromThis();
	}
	return network;
    }

    @Override
    public NETWORK createNetworkFromThis() {
	NETWORK pNetwork = network = createNetworkFromConductors(Sets.newHashSet((CONDUCTOR) this));
	pNetwork.refreshNewNetwork();
	return pNetwork;
    }

    @Override
    public void setNetwork(NETWORK network) {
	NETWORK pNetwork = this.network;
	if (pNetwork != null && !pNetwork.equals(network)) {
	    removeFromNetwork();
	}
	this.network = network;
    }

    @Override
    public void updateNetwork(Direction... dirs) {
	if (isRemoved())
	    return;
	Level level = this.level;
	if (level == null) {
	    if (!isQueued) {
		isQueued = true;
		Scheduler.schedule(1, () -> updateNetwork(dirs));
	    }
	    return;
	}
	isQueued = false;
	if (level.isClientSide)
	    return;
	Pair<List<UpdatedReceiver>, List<UpdatedConductor<CONDUCTOR>>> changed = updateAdjacent(level, dirs);
	List<UpdatedConductor<CONDUCTOR>> conductors = changed.getSecond();

	if (conductors.isEmpty()) {
	    if (network == null) {
		network = createNetworkFromThis();
	    }
	} else {
	    HashSet<NETWORK> adjacentNetworks = new HashSet<>();
	    for (UpdatedConductor<CONDUCTOR> wire : conductors) {
		CONDUCTOR conductor = wire.conductor();
		if (wire.removed() || conductor.isRemoved())
		    continue;
		adjacentNetworks.add(conductor.getNetwork());
	    }

	    NETWORK currentNetwork = network;
	    if (currentNetwork == null && adjacentNetworks.size() == 1) {
		currentNetwork = network = adjacentNetworks.iterator().next();
		currentNetwork.updateConductor((CONDUCTOR) this, false);
	    } else {
		if (currentNetwork == null) {
		    currentNetwork = network = createNetworkFromThis();
		}
		if (adjacentNetworks.isEmpty()) {
		    currentNetwork.updateConductors(conductors);
		} else {
		    adjacentNetworks.add(currentNetwork);
		    NETWORK newNetwork = network = createNetworkFromNetworks(adjacentNetworks);
		    newNetwork.refreshNewNetwork();
		}
	    }
	}

	List<UpdatedReceiver> receivers = changed.getFirst();
	NETWORK currentNetwork = network;
	if (!receivers.isEmpty() && currentNetwork != null) {
	    currentNetwork.updateRecievers(receivers);
	}
    }

    @Override
    public void removeFromNetwork() {
	if (network != null) {
	    network.removeFromNetwork((CONDUCTOR) this);
	}
    }

    @Override
    public BlockEntity[] getConectedRecievers() {
	return recieverConnections;
    }

    @Override
    public BlockEntity[] getConnectedCables() {
	return cableConnections;
    }

    @Override
    public void setRemoved() {
	super.setRemoved();
	Level level = this.level;
	if (level == null)
	    return;

	if (!level.isClientSide && network != null) {
	    network.split(level, (CONDUCTOR) this);
	}

    }

    @Override
    public void onChunkUnloaded() {
	Level level = this.level;
	if (level == null)
	    return;

	if (!level.isClientSide && network != null) {
	    network.split(level, (CONDUCTOR) this);
	}
	super.onChunkUnloaded();
    }

    public abstract NETWORK createNetworkFromConductors(Set<CONDUCTOR> conductors);

    public abstract NETWORK createNetworkFromNetworks(Set<NETWORK> networks);

    @Override
    public void onLoad() {
	super.onLoad();
	updateNetwork(Direction.values());
    }

    public static record UpdatedReceiver(BlockEntity reciever, boolean removed, Direction dir) {
    }

    public static record UpdatedConductor<CONDUCTOR>(CONDUCTOR conductor, boolean removed) {
    }

}
