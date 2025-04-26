package voltaic.registers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import voltaic.api.radiation.CapabilityRadiationRecipient;
import voltaic.api.radiation.util.IRadiationRecipient;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.jetbrains.annotations.Nullable;

import voltaic.Voltaic;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.misc.ILocationStorage;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class VoltaicCapabilities {

    public static final double DEFAULT_VOLTAGE = 120.0;
    public static final String LOCATION_KEY = "location";

    public static final BlockCapability<ICapabilityElectrodynamic, @Nullable Direction> CAPABILITY_ELECTRODYNAMIC_BLOCK = BlockCapability.createSided(Voltaic.rl("electrodynamicblock"), ICapabilityElectrodynamic.class);

    public static final ItemCapability<ILocationStorage, Void> CAPABILITY_LOCATIONSTORAGE_ITEM = ItemCapability.createVoid(Voltaic.rl("locationstorageitem"), ILocationStorage.class);

    public static final ItemCapability<IGasHandlerItem, Void> CAPABILITY_GASHANDLER_ITEM = ItemCapability.createVoid(Voltaic.rl("gashandleritem"), IGasHandlerItem.class);
    public static final BlockCapability<IGasHandler, @Nullable Direction> CAPABILITY_GASHANDLER_BLOCK = BlockCapability.createSided(Voltaic.rl("gashandlerblock"), IGasHandler.class);

    public static final EntityCapability<IRadiationRecipient, Void> CAPABILITY_RADIATIONRECIPIENT = EntityCapability.createVoid(Voltaic.rl("radiationrecipient"), IRadiationRecipient.class);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        BuiltInRegistries.ENTITY_TYPE.forEach(entry -> event.registerEntity(CAPABILITY_RADIATIONRECIPIENT, entry, (entity, context) -> new CapabilityRadiationRecipient()));

    }


}
