package voltaic.common.packet;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import voltaic.Voltaic;
import voltaic.common.packet.types.client.PacketResetGuidebookPages;
import voltaic.common.packet.types.client.PacketSetClientRadiationShielding;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveBlocks;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveFluids;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveGases;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveItems;
import voltaic.common.packet.types.client.PacketSpawnSmokeParticle;
import voltaic.common.packet.types.client.PacketUpdateCariedItemClient;
import voltaic.common.packet.types.server.PacketSendUpdatePropertiesServer;
import voltaic.common.packet.types.server.PacketSwapBattery;
import voltaic.common.packet.types.server.PacketUpdateCarriedItemServer;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

    public static HashMap<String, String> playerInformation = new HashMap<>();
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registry = event.registrar(Voltaic.ID).versioned(PROTOCOL_VERSION).optional();

        // CLIENT

        registry.playToClient(PacketResetGuidebookPages.TYPE, PacketResetGuidebookPages.CODEC, PacketResetGuidebookPages::handle);
        registry.playToClient(PacketSpawnSmokeParticle.TYPE, PacketSpawnSmokeParticle.CODEC, PacketSpawnSmokeParticle::handle);
        registry.playToClient(PacketSetClientRadioactiveItems.TYPE, PacketSetClientRadioactiveItems.CODEC, PacketSetClientRadioactiveItems::handle);
        registry.playToClient(PacketSetClientRadioactiveFluids.TYPE, PacketSetClientRadioactiveFluids.CODEC, PacketSetClientRadioactiveFluids::handle);
        registry.playToClient(PacketSetClientRadioactiveGases.TYPE, PacketSetClientRadioactiveGases.CODEC, PacketSetClientRadioactiveGases::handle);
        registry.playToClient(PacketSetClientRadiationShielding.TYPE, PacketSetClientRadiationShielding.CODEC, PacketSetClientRadiationShielding::handle);
        registry.playToClient(PacketUpdateCariedItemClient.TYPE, PacketUpdateCariedItemClient.CODEC, PacketUpdateCariedItemClient::handle);
        registry.playToClient(PacketSetClientRadioactiveBlocks.TYPE, PacketSetClientRadioactiveBlocks.CODEC, PacketSetClientRadioactiveBlocks::handle);

        // SERVER

        registry.playToServer(PacketSendUpdatePropertiesServer.TYPE, PacketSendUpdatePropertiesServer.CODEC, PacketSendUpdatePropertiesServer::handle);
        registry.playToServer(PacketSwapBattery.TYPE, PacketSwapBattery.CODEC, PacketSwapBattery::handle);
        registry.playToServer(PacketUpdateCarriedItemServer.TYPE, PacketUpdateCarriedItemServer.CODEC, PacketUpdateCarriedItemServer::handle);

    }

    public static ResourceLocation id(String name) {
        return Voltaic.rl(name);
    }


}
