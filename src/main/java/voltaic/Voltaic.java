package voltaic;

import java.util.Random;
import java.util.function.Consumer;

import net.neoforged.fml.ModList;
import voltaic.api.electricity.formatting.MeasurementUnits;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import voltaic.client.VoltaicClientRegister;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.packet.types.client.PacketResetGuidebookPages;
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

import javax.annotation.Nullable;

@Mod(Voltaic.ID)
@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class Voltaic {

    public static Logger LOGGER = LogManager.getLogger(Voltaic.ID);

    public static final Random RANDOM = new Random();

    public static final String ID = "voltaic";
    public static final String NAME = "Voltaic";

    public static final String MEKANISM_ID = "mekanism";

    private static final String ELECTRODYNAMICS_MOD_ID = "electrodynamics";

    @Nullable
    private static Boolean ELECTRODYNAMICS_LOADED = null;

    public Voltaic(IEventBus bus) {
        ELECTRODYNAMICS_LOADED = ModList.get().isLoaded(ELECTRODYNAMICS_MOD_ID);
        MeasurementUnits.init();
        ConfigurationHandler.registerConfig(VoltaicConstants.class);
        // MUST GO BEFORE BLOCKS!!!!
        VoltaicBlockStates.init();
        UnifiedVoltaicRegister.register(bus);

    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {

        NeoForge.EVENT_BUS.addListener(getGuidebookListener());
        VoltaicTags.init();
        RadioactiveItemRegister.INSTANCE = new RadioactiveItemRegister().subscribeAsSyncable();
        RadioactiveFluidRegister.INSTANCE = new RadioactiveFluidRegister().subscribeAsSyncable();
        RadioactiveGasRegister.INSTANCE = new RadioactiveGasRegister().subscribeAsSyncable();
        RadiationShieldingRegister.INSTANCE = new RadiationShieldingRegister().subscribeAsSyncable();
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
        event.enqueueWork(() -> {
            VoltaicClientRegister.setup();
        });
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

    public static final ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static final ResourceLocation vanillarl(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static final ResourceLocation forgerl(String path) {
        return ResourceLocation.fromNamespaceAndPath("neoforge", path);
    }

    public static final ResourceLocation commonrl(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    // This returns null to help us catch inappropriate references of this parameter
    // This will only be accurate after FMLCommonSetupEvent has fired, meaning if you
    // call this before that, you will have an inaccurate result.
    public static final Boolean isElectroLoaded() {
        return ELECTRODYNAMICS_LOADED;
    }

}
