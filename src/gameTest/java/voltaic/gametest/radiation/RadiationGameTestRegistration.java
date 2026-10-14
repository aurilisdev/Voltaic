package voltaic.gametest.radiation;



import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.Map;
import java.util.WeakHashMap;

public final class RadiationGameTestRegistration {
    static final Map<BlockEntity, ItemStackHandler> LOCAL_SLOTS = new WeakHashMap<>();

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityType.SIGN,
                (sign, side) -> LOCAL_SLOTS.computeIfAbsent(sign, ignored -> new ItemStackHandler(1)));
    }

    public static void register(RegisterGameTestsEvent event) {
        event.register(RadiationGameTests.class);
        if (ModList.get().isLoaded("ae2")) event.register(AE2RadiationGameTests.class);
        if (ModList.get().isLoaded("refinedstorage")) event.register(RefinedStorageRadiationGameTests.class);
    }
}
