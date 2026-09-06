package voltaic.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import voltaic.Voltaic;
import voltaic.api.multiblock.assemblybased.CommandScanMultiblock;
import voltaic.common.command.CommandWipeRadiationSources;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveBlockRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEventHandler {

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
	event.addListener(RadioactiveItemRegister.getInstance());
	event.addListener(RadioactiveFluidRegister.getInstance());
	event.addListener(RadioactiveGasRegister.getInstance());
	event.addListener(RadiationShieldingRegister.getInstance());
	event.addListener(RadioactiveBlockRegister.getInstance());
    }

    @SubscribeEvent
    public static void serverStartedHandler(ServerStartedEvent event) {
	RadioactiveItemRegister.getInstance().generateTagValues();
	RadioactiveFluidRegister.getInstance().generateTagValues();
	RadioactiveGasRegister.getInstance().generateTagValues();
	RadiationShieldingRegister.getInstance().generateTagValues();
	RadioactiveBlockRegister.getInstance().generateTagValues();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
	CommandScanMultiblock.register(event.getDispatcher());
	CommandWipeRadiationSources.register(event.getDispatcher());
    }

}
