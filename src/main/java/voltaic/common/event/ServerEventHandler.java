package voltaic.common.event;

import voltaic.Voltaic;
import voltaic.api.multiblock.assemblybased.CommandScanMultiblock;
import voltaic.common.command.CommandWipeRadiationSources;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME)
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
		CommandScanMultiblock.register(event.getDispatcher());
		CommandWipeRadiationSources.register(event.getDispatcher());
	}

}
