package voltaic.common.packet;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import voltaic.Voltaic;
import voltaic.common.packet.types.client.*;
import voltaic.common.packet.types.server.PacketSendUpdatePropertiesServer;

public class NetworkHandler {

	private static final String PROTOCOL_VERSION = "1";
	private static int disc = 0;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Voltaic.ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void init() {
        // CLIENT

    	CHANNEL.registerMessage(disc++, PacketResetGuidebookPages.class, PacketResetGuidebookPages::encode, PacketResetGuidebookPages::decode, PacketResetGuidebookPages::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    	CHANNEL.registerMessage(disc++, PacketSpawnSmokeParticle.class, PacketSpawnSmokeParticle::encode, PacketSpawnSmokeParticle::decode, PacketSpawnSmokeParticle::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    	CHANNEL.registerMessage(disc++, PacketSetClientRadioactiveItems.class, PacketSetClientRadioactiveItems::encode, PacketSetClientRadioactiveItems::decode, PacketSetClientRadioactiveItems::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(disc++, PacketSetClientRadioactiveFluids.class, PacketSetClientRadioactiveFluids::encode, PacketSetClientRadioactiveFluids::decode, PacketSetClientRadioactiveFluids::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(disc++, PacketSetClientRadiationShielding.class, PacketSetClientRadiationShielding::encode, PacketSetClientRadiationShielding::decode, PacketSetClientRadiationShielding::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // SERVER

        CHANNEL.registerMessage(disc++, PacketSendUpdatePropertiesServer.class, PacketSendUpdatePropertiesServer::encode, PacketSendUpdatePropertiesServer::decode, PacketSendUpdatePropertiesServer::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

    }

    public static ResourceLocation id(String name) {
        return Voltaic.rl(name);
    }


}
