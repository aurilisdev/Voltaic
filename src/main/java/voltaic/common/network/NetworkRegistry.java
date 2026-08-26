package voltaic.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.Voltaic;
import voltaic.api.network.ITickableNetwork;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.FORGE)
public class NetworkRegistry {
    private static final HashMap<UUID, ITickableNetwork> NETWORKS = new HashMap<>();
    private static final HashMap<UUID, ITickableNetwork> PENDING_ADDITIONS = new HashMap<>();
    private static final HashSet<UUID> PENDING_REMOVALS = new HashSet<>();

    public static void register(ITickableNetwork network) {
	PENDING_ADDITIONS.put(network.getId(), network);
    }

    public static void deregister(ITickableNetwork network) {
	UUID id = network.getId();

	if (PENDING_ADDITIONS.remove(id) != null) {
	    return;
	}

	if (NETWORKS.containsKey(id)) {
	    PENDING_REMOVALS.add(id);
	}
    }

    @SubscribeEvent
    public static void update(ServerTickEvent event) {
	if (event.phase == Phase.START) {
	    return;
	}
	for (UUID id : PENDING_REMOVALS) {
	    NETWORKS.remove(id);
	}
	PENDING_REMOVALS.clear();

	NETWORKS.putAll(PENDING_ADDITIONS);
	PENDING_ADDITIONS.clear();

	for (ITickableNetwork network : NETWORKS.values()) {
	    if (PENDING_REMOVALS.contains(network.getId())) {
		continue;
	    }

	    if (network.getSize() == 0) {
		deregister(network);
	    } else {
		network.tick();
	    }
	}
    }

    @SubscribeEvent
    public static void unloadServer(ServerStoppedEvent event) {
	NETWORKS.clear();
	PENDING_ADDITIONS.clear();
	PENDING_REMOVALS.clear();
    }
}