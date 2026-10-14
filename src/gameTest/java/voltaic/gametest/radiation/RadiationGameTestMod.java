package voltaic.gametest.radiation;



import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** Separate test mod so its data pack augments core tags instead of shadowing resource paths. */
@Mod("voltaic_radiation_tests")
public final class RadiationGameTestMod {
    public RadiationGameTestMod(IEventBus bus) {
        bus.addListener(RadiationGameTestRegistration::register);
        bus.addListener(RadiationGameTestRegistration::registerCapabilities);
    }
}
