package voltaic.api.radiation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import voltaic.Voltaic;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.common.settings.VoltaicConfig;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicCapabilities;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME)
public class RadiationSystem {

    @SubscribeEvent
    public static void tickServer(LevelTickEvent.Pre event) {

	Level level = event.getLevel();

	if (level.isClientSide())
	    return;

	if (VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse()) {
	    wipeAllSources(level);
	    return;
	}

	IRadiationManager manager = level.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);

	manager.tick(level);

    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
	if (VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse() || event.getEntity().level().isClientSide()
		|| !(event.getEntity() instanceof LivingEntity))
	    return;
	IRadiationRecipient capability = event.getEntity()
		.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
	if (capability == null)
	    return;
	capability.tick((LivingEntity) event.getEntity());

    }

    public static void addRadiationSource(Level world, SimpleRadiationSource source) {
	if (VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse())
	    return;
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	manager.addRadiationSource(source, world);

    }

    public static void removeRadiationSource(Level world, BlockPos pos, boolean shouldLinger) {
	if (VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse())
	    return;
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	manager.removeRadiationSource(pos, shouldLinger, world);
    }

    public static List<BlockPos> getRadiationSources(Level world) {
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	HashSet<BlockPos> sources = new HashSet<>(manager.getPermanentLocations(world));
	sources.addAll(manager.getTemporaryLocations(world));
	sources.addAll(manager.getFadingLocations(world));
	return new ArrayList<>(sources);
    }

    public static void addDisipation(Level world, double amount, AABB volume) {
	if (VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse())
	    return;
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	manager.setLocalizedDisipation(amount, volume, world);
    }

    public static void removeDisipation(Level world, AABB volume) {
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	manager.removeLocalizedDisipation(volume, world);
    }

    public static void wipeAllSources(Level world) {
	IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
	manager.wipeAllSources(world);
    }
}
