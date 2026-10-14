package voltaic.compatibility.radiation;

import net.neoforged.fml.ModList;

/** Keeps optional mod classes out of the core radiation class loading path. */
public final class RadiationCompatibility {
    private RadiationCompatibility() {}

    public static void register() {
        if (ModList.get().isLoaded("ae2")) AE2Radiation.register();
        if (ModList.get().isLoaded("refinedstorage")) RefinedStorageRadiation.register();
    }
}
