package voltaic.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME, value = { Dist.CLIENT })
public class VoltaicClientEvents {

    @SubscribeEvent
    public static void wipeRenderHashes(ClientPlayerNetworkEvent.LoggingOut event) {
        ScreenGuidebook.setInitNotHappened();
    }

}
