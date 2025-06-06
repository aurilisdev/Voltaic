package voltaic.prefab.tile.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import voltaic.api.network.cable.IRefreshableCable;
import voltaic.common.block.connect.EnumConnectType;
import voltaic.prefab.network.AbstractNetwork;
import voltaic.prefab.utilities.Scheduler;

public abstract class GenericRefreshingConnectTile<CABLETYPE, CONDUCTOR extends GenericRefreshingConnectTile<CABLETYPE, CONDUCTOR, NETWORK>, NETWORK extends AbstractNetwork<CONDUCTOR, CABLETYPE, ?, NETWORK>> extends GenericConnectTile implements IRefreshableCable<CABLETYPE, NETWORK> {

    private final EnumConnectType[] previousConnections = {EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE, EnumConnectType.NONE};
    protected final TileEntity[] recieverConnections = new TileEntity[6];
    protected final TileEntity[] prevRecieverConnections = new TileEntity[6];
    protected final TileEntity[] cableConnections = new TileEntity[6];
    protected final TileEntity[] prevCableConnections = new TileEntity[6];
    protected final HashSet<CONDUCTOR> connectionSet = new HashSet<>();

    private NETWORK network;

    public boolean isQueued = false;
    
    private boolean loaded = false;

    public GenericRefreshingConnectTile(TileEntityType<?> tile) {
        super(tile);
    }

    public Pair<List<UpdatedReceiver>, List<UpdatedConductor<CONDUCTOR>>> updateAdjacent(Direction[] dirs) {

        boolean flag = false;
        int ordinal;
        EnumConnectType prevConnection, connection;

        List<UpdatedReceiver> updatedRecievers = new ArrayList<>();
        List<UpdatedConductor<CONDUCTOR>> updatedConductors = new ArrayList<>();

        for (Direction dir : dirs) {

            ordinal = dir.ordinal();
            connection = connectionsArr[ordinal];
            prevConnection = previousConnections[ordinal];

            if (prevConnection == connection) {
                continue;
            }
            
            TileEntity entity = level.getBlockEntity(worldPosition.relative(dir));

            if (connection == EnumConnectType.NONE && prevConnection == EnumConnectType.WIRE) {
                updatedConductors.add(new UpdatedConductor<>((CONDUCTOR) prevCableConnections[ordinal], true));
            } else if (connection == EnumConnectType.WIRE && prevConnection == EnumConnectType.NONE) {
                updatedConductors.add(new UpdatedConductor<>((CONDUCTOR) entity, false));
            } else if (connection == EnumConnectType.NONE && prevConnection == EnumConnectType.INVENTORY) {
                updatedRecievers.add(new UpdatedReceiver(prevRecieverConnections[ordinal], true, dir));
            } else if (connection == EnumConnectType.INVENTORY && prevConnection == EnumConnectType.NONE) {
                updatedRecievers.add(new UpdatedReceiver(entity, false, dir));
            }

            prevCableConnections[ordinal] = cableConnections[ordinal];
            prevRecieverConnections[ordinal] = recieverConnections[ordinal];
            recieverConnections[ordinal] = null;
            cableConnections[ordinal] = null;

            if (connection == EnumConnectType.WIRE) {
                cableConnections[ordinal] = entity;
            } else if (connection == EnumConnectType.INVENTORY) {
                recieverConnections[ordinal] = entity;
            }
            previousConnections[ordinal] = connection;
            flag = true;

        }

        if (flag) {
            connectionSet.clear();
            for (TileEntity entity : cableConnections) {
                if (entity == null) {
                    continue;
                }
                connectionSet.add((CONDUCTOR) entity);
            }
        }

        return Pair.of(updatedRecievers, updatedConductors);
    }

    @Override
    public NETWORK getNetwork() {
    	if(network == null) {
    		createNetworkFromThis();	
    	}
        return network;
    }

    @Override
    public void createNetworkFromThis() {
        network = createInstanceConductor(Sets.newHashSet((CONDUCTOR) this));
        network.refreshNewNetwork();
    }

    @Override
    public void setNetwork(NETWORK network) {
        if (this.network == null) {
            this.network = network;
        } else if (!this.network.equals(network)) {
            removeFromNetwork();
            this.network = network;
        }
    }

    @Override
    public void updateNetwork(Direction[] dirs) {
        if (isRemoved()) {
            return;
        }
        if (level == null) {
            if (!isQueued) {
                isQueued = true;
                Scheduler.schedule(20, () -> updateNetwork(dirs));
            } else {
            	return;
            }
        }

        isQueued = false;

        if(level.isClientSide) {
            return;
        }
        //locked = true;
        
        Pair<List<UpdatedReceiver>, List<UpdatedConductor<CONDUCTOR>>> changed = updateAdjacent(dirs);
        
        //locked = false;

        if (changed.getSecond().isEmpty()) {

            if (network == null) {
                createNetworkFromThis();
            }

            if (!changed.getFirst().isEmpty()) {

                network.updateRecievers(changed.getFirst());

            }

        } else {

            HashSet<NETWORK> adjacentNetworks = new HashSet<>();

            for (UpdatedConductor<CONDUCTOR> wire : changed.getSecond()) {

                if (wire.conductor() == null || wire.conductor().isRemoved() || wire.conductor().getNetwork() == null) {
                    continue;
                }

                adjacentNetworks.add(wire.conductor().getNetwork());

            }

            if (adjacentNetworks.isEmpty()) {

                if (network == null) {
                    createNetworkFromThis();
                }

                network.updateConductors(changed.getSecond());

            } else {

                List<NETWORK> networks = new ArrayList<>(adjacentNetworks);

                if (networks.size() > 1) {

                    if (network == null) {

                        createNetworkFromThis();

                    }

                    adjacentNetworks.add(network);

                    NETWORK newNetwork = createInstance(adjacentNetworks);

                    network = newNetwork;

                    newNetwork.refreshNewNetwork();


                } else {

                    if (network == null) {

                        network = networks.get(0);

                        network.updateConductor((CONDUCTOR) this, false);

                    } else {

                        adjacentNetworks.add(network);

                        NETWORK newNetwork = createInstance(adjacentNetworks);

                        network = newNetwork;

                        newNetwork.refreshNewNetwork();

                    }

                }

            }

        }
    }

    @Override
    public void removeFromNetwork() {
        if (network != null) {
            network.removeFromNetwork((CONDUCTOR) this);
        }
    }

    @Override
    public TileEntity[] getConectedRecievers() {
        return recieverConnections;
    }

    @Override
    public TileEntity[] getConnectedCables() {
        return cableConnections;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide && network != null) {
            getNetwork().split((CONDUCTOR) this);
        }

    }

    @Override
    public void onChunkUnloaded() {
        if (!level.isClientSide && network != null) {
            getNetwork().split((CONDUCTOR) this);
        }
        super.onChunkUnloaded();
    }

    public abstract NETWORK createInstanceConductor(Set<CONDUCTOR> conductors);

    public abstract NETWORK createInstance(Set<NETWORK> networks);


    @Override
    public void onLoad() {
        super.onLoad();
        Scheduler.schedule(1, () -> {
        	updateNetwork(Direction.values());
        });   
    }

    public static class UpdatedReceiver {
    	
    	private final TileEntity reciever;
    	private final boolean removed;
    	private final Direction dir;
    	
    	public UpdatedReceiver(TileEntity reciever, boolean removed, Direction dir) {
    		this.reciever = reciever;
    		this.removed = removed;
    		this.dir = dir;
    	}
    	
    	public TileEntity reciever() {
    		return reciever;
    	}
    	
    	public boolean removed() {
    		return removed;
    	}
    	
    	public Direction dir() {
    		return dir;
    	}

    }

    public static class UpdatedConductor<CONDUCTOR> {
    	
    	private final CONDUCTOR conductor;
    	private final boolean removed;
    	
    	public UpdatedConductor(CONDUCTOR conductor, boolean removed) {
    		this.conductor = conductor;
    		this.removed = removed;
    	}
    	
    	public CONDUCTOR conductor() {
    		return conductor;
    	}
    	
    	public boolean removed() {
    		return removed;
    	}

    }

}
