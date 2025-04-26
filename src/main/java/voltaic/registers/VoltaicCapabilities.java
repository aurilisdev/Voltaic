package voltaic.registers;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import voltaic.Voltaic;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.misc.ILocationStorage;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class VoltaicCapabilities {

    public static final double DEFAULT_VOLTAGE = 120.0;
    public static final String LOCATION_KEY = "location";

    public static final Capability<ICapabilityElectrodynamic> CAPABILITY_ELECTRODYNAMIC_BLOCK = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static final Capability<ILocationStorage> CAPABILITY_LOCATIONSTORAGE_ITEM = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final Capability<IGasHandler> CAPABILITY_GASHANDLER_BLOCK = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final Capability<IGasHandlerItem> CAPABILITY_GASHANDLER_ITEM = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<IRadiationRecipient> CAPABILITY_RADIATIONRECIPIENT = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<IRadiationManager> CAPABILITY_RADIATIONMANAGER = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(ICapabilityElectrodynamic.class);
		event.register(ILocationStorage.class);
		event.register(IGasHandler.class);
		event.register(IGasHandlerItem.class);
		event.register(IRadiationRecipient.class);
		event.register(IRadiationManager.class);
	}


}
