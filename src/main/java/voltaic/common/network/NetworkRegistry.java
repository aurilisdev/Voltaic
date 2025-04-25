package voltaic.common.network;

import java.util.ConcurrentModificationException;
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
	private static final HashSet<UUID> TO_REMOVE = new HashSet<>();

	public static void register(ITickableNetwork network) {
		NETWORKS.put(network.getId(), network);
	}

	public static void deregister(ITickableNetwork network) {
		if (NETWORKS.containsKey(network.getId())) {
			TO_REMOVE.add(network.getId());
		}
	}

	@SubscribeEvent
	public static void update(ServerTickEvent event) {
		if(event.phase == Phase.START) {
			return;
		}
		try {
			for(UUID id : TO_REMOVE) {
				NETWORKS.remove(id);
			}
			TO_REMOVE.clear();
            for (ITickableNetwork net : NETWORKS.values()) {
                if (net.getSize() == 0) {
                    deregister(net);
                } else {
                    net.tick();
                }
            }
		} catch (ConcurrentModificationException exception) {
			exception.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void unloadServer(ServerStoppedEvent event) {
		try {
			NETWORKS.clear();
			TO_REMOVE.clear();
		} catch (ConcurrentModificationException exception) {
			exception.printStackTrace();
		}
	}

}