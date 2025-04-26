package voltaic;

import java.util.Random;
import java.util.function.Consumer;

import voltaic.api.electricity.formatting.MeasurementUnits;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import voltaic.client.VoltaicClientRegister;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.condition.ConfigCondition;
import voltaic.common.packet.NetworkHandler;
import voltaic.common.packet.types.client.PacketResetGuidebookPages;
import voltaic.common.settings.VoltaicConstants;
import voltaic.common.tags.VoltaicTags;
import voltaic.prefab.configuration.ConfigurationHandler;
import voltaic.registers.UnifiedVoltaicRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

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

    public Voltaic() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ELECTRODYNAMICS_LOADED = ModList.get().isLoaded(ELECTRODYNAMICS_MOD_ID);
        MeasurementUnits.init();
        ConfigurationHandler.registerConfig(VoltaicConstants.class);
        // MUST GO BEFORE BLOCKS!!!!
        VoltaicBlockStates.init();
        UnifiedVoltaicRegister.register(bus);

    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(getGuidebookListener());
        NetworkHandler.init();
        VoltaicTags.init();
        RadioactiveItemRegister.INSTANCE = new RadioactiveItemRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
        RadioactiveFluidRegister.INSTANCE = new RadioactiveFluidRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
        RadioactiveGasRegister.INSTANCE = new RadioactiveGasRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
        RadiationShieldingRegister.INSTANCE = new RadiationShieldingRegister().subscribeAsSyncable(NetworkHandler.CHANNEL);
        // CraftingHelper.register(ConfigCondition.Serializer.INSTANCE); // Probably wrong location after update from 1.18.2 to
        // 1.19.2

        // RegisterFluidToGasMapEvent map = new RegisterFluidToGasMapEvent();
        // MinecraftForge.EVENT_BUS.post(map);
        // ElectrodynamicsGases.MAPPED_GASSES.putAll(map.fluidToGasMap);

    }
    
    @SubscribeEvent
	public static void registerConditions(RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
		}
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
			PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
			NetworkHandler.CHANNEL.send(target, new PacketResetGuidebookPages());
		};

    }

    public static final ResourceLocation rl(String path) {
        return new ResourceLocation(ID, path);
    }

    public static final ResourceLocation vanillarl(String path) {
        return new ResourceLocation(path);
    }

    public static final ResourceLocation forgerl(String path) {
        return new ResourceLocation("neoforge", path);
    }

    // This returns null to help us catch inappropriate references of this parameter
    // This will only be accurate after FMLCommonSetupEvent has fired, meaning if you
    // call this before that, you will have an inaccurate result.
    public static final Boolean isElectroLoaded() {
        return ELECTRODYNAMICS_LOADED;
    }

}
