package voltaic.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.Voltaic;
import voltaic.api.radiation.CapabilityRadiationRecipient;
import voltaic.api.radiation.RadiationManager;
import voltaic.common.command.CommandWipeRadiationSources;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.registers.VoltaicCapabilities;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(RadioactiveItemRegister.INSTANCE);
		event.addListener(RadioactiveFluidRegister.INSTANCE);
		event.addListener(RadioactiveGasRegister.INSTANCE);
		event.addListener(RadiationShieldingRegister.INSTANCE);
	}

	@SubscribeEvent
	public static void serverStartedHandler(ServerStartedEvent event) {
		RadioactiveItemRegister.INSTANCE.generateTagValues();
		RadioactiveFluidRegister.INSTANCE.generateTagValues();
		RadioactiveGasRegister.INSTANCE.generateTagValues();
		RadiationShieldingRegister.INSTANCE.generateTagValues();
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandWipeRadiationSources.register(event.getDispatcher());
	}
	
	@SubscribeEvent
	public static void registerEntityCaps(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if(entity instanceof LivingEntity living && !entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).isPresent()) {
			event.addCapability(Voltaic.rl("radiationrecipient"), new CapabilityRadiationRecipient());
		}
	}
	
	@SubscribeEvent
	public static void registerLevelCaps(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		if(world != null && !world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).isPresent()) {
			event.addCapability(Voltaic.rl("radiationmanager"), new RadiationManager());
		}
	}

}
