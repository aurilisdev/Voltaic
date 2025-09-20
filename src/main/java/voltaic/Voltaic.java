package voltaic;

import java.util.Random;
import java.util.function.Consumer;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import voltaic.common.reloadlistener.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import voltaic.client.VoltaicClientRegister;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.packet.types.client.PacketResetGuidebookPages;
import voltaic.common.settings.VoltaicConfig;
import voltaic.common.settings.VoltaicConstants;
import voltaic.common.tags.VoltaicTags;
import voltaic.prefab.configuration.ConfigurationHandler;
import voltaic.registers.UnifiedVoltaicRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(Voltaic.ID)
@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public final class Voltaic {

    public static Logger LOGGER = LogManager.getLogger(Voltaic.ID);

    public static final Random RANDOM = new Random();

    public static final String ID = "voltaic";
    public static final String NAME = "Voltaic";

    //public static final String MEKANISM_ID = "mekanism";

    private static final String ELECTRODYNAMICS_MOD_ID = "electrodynamics";

    public Voltaic(IEventBus bus, ModContainer container) {
        var config = new VoltaicConfig();
        ConfigurationHandler.load(VoltaicConstants.class);
        // MUST GO BEFORE BLOCKS!!!!
        VoltaicBlockStates.init();
        UnifiedVoltaicRegister.register(bus);

        bus.addListener((FMLLoadCompleteEvent event) -> {
            ConfigurationHandler.fillFromLegacyConfig(config);
        });

        container.registerConfig(ModConfig.Type.COMMON, config.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(getGuidebookListener());
        VoltaicTags.init();
        RadioactiveItemRegister.INSTANCE = new RadioactiveItemRegister().subscribeAsSyncable();
        RadioactiveFluidRegister.INSTANCE = new RadioactiveFluidRegister().subscribeAsSyncable();
        RadioactiveGasRegister.INSTANCE = new RadioactiveGasRegister().subscribeAsSyncable();
        RadiationShieldingRegister.INSTANCE = new RadiationShieldingRegister().subscribeAsSyncable();
        RadioactiveBlockRegister.INSTANCE = new RadioactiveBlockRegister().subscribeAsSyncable();
        // CraftingHelper.register(ConfigCondition.Serializer.INSTANCE); // Probably wrong location after update from 1.18.2 to
        // 1.19.2

        // RegisterFluidToGasMapEvent map = new RegisterFluidToGasMapEvent();
        // MinecraftForge.EVENT_BUS.post(map);
        // ElectrodynamicsGases.MAPPED_GASSES.putAll(map.fluidToGasMap);

    }

    // I wonder how long this bug has been there
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(VoltaicClientRegister::setup);
    }

    // Don't really have a better place to put this for now
    private static Consumer<OnDatapackSyncEvent> getGuidebookListener() {
        return event -> {
            ServerPlayer player = event.getPlayer();
            if (player == null) {
                PacketDistributor.sendToAllPlayers(PacketResetGuidebookPages.PACKET);
            } else {
                PacketDistributor.sendToPlayer(player, PacketResetGuidebookPages.PACKET);
            }
        };
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static ResourceLocation vanillarl(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation forgerl(String path) {
        return ResourceLocation.fromNamespaceAndPath("neoforge", path);
    }

    public static ResourceLocation commonrl(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static boolean isElectroLoaded() {
        return ModList.get().isLoaded(ELECTRODYNAMICS_MOD_ID);
    }
}
