package voltaic.api.radiation;

import voltaic.Voltaic;
import voltaic.api.radiation.util.BlockPosVolume;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME)
public class RadiationSystem {

	@SubscribeEvent
	public static void tickServer(LevelTickEvent.Pre event) {

		Level level = event.getLevel();

		if(level.isClientSide()) {
			return;
		}

		IRadiationManager manager = level.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);

		manager.tick(level);


	}

	@SubscribeEvent
	public static void entityTick(EntityTickEvent.Post event) {
		if(event.getEntity().level().isClientSide() || !(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		IRadiationRecipient capability = event.getEntity().getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
		if(capability == null) {
			return;
		}
		capability.tick((LivingEntity) event.getEntity());

	}

	public static void addRadiationSource(Level world, SimpleRadiationSource source) {
		if(source == null) {
			throw new UnsupportedOperationException("source cannot be null");
		}
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		manager.addRadiationSource(source, world);

	}

	public static void removeRadiationSource(Level world, BlockPos pos, boolean shouldLinger) {
		if(pos == null) {
			throw new UnsupportedOperationException("position cannot be null");
		}
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		manager.removeRadiationSource(pos, shouldLinger, world);
	}

	public static List<BlockPos> getRadiationSources(Level world) {
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		HashSet<BlockPos> sources = new HashSet<>();
		sources.addAll(manager.getPermanentLocations(world));
		sources.addAll(manager.getTemporaryLocations(world));
		sources.addAll(manager.getFadingLocations(world));
		return new ArrayList<>(sources);
	}

	public static void addDisipation(Level world, double amount, BlockPosVolume volume) {
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		manager.setLocalizedDisipation(amount, volume, world);
	}

	public static void removeDisipation(Level world, BlockPosVolume volume) {
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		manager.removeLocalizedDisipation(volume, world);
	}

	public static void wipeAllSources(Level world) {
		IRadiationManager manager = world.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
		manager.wipeAllSources(world);
	}


	/*
	public static ThreadLocal<HashMap<Player, Double>> radiationMap = ThreadLocal.withInitial(HashMap::new);

	private static double getRadiationModifier(Level world, Location source, Location end) {
		double distance = 1 + source.distance(end);
		Location clone = new Location(end);
		double modifier = 1;
		Location newSource = new Location(source);
		clone.add(-source.x(), -source.y(), -source.z()).normalize().mul(0.33f);
		int checks = (int) distance * 3;
		BlockPos curr = newSource.toBlockPos();
		double lastHard = 0;
		while (checks > 0) {
			newSource.add(clone);
			double hard = lastHard;
			BlockPos next = newSource.toBlockPos();
			if (!curr.equals(next)) {
				curr = next;
				BlockState state = world.getBlockState(curr);
				lastHard = hard = (state.getBlock() == NuclearScienceBlocks.blocklead ? 20000 : state.getDestroySpeed(world, curr)) / (world.getFluidState(curr).isEmpty() ? 1 : 50.0);
			}
			modifier += hard / 4.5f;
			checks--;
		}
		return modifier;
	}

	public static double getRadiation(Level world, Location source, Location end, double strength) {
		double distance = 1 + source.distance(end);
		return strength / (getRadiationModifier(world, source, end) * distance * distance);
	}

	public static void applyRadiation(LivingEntity entity, Location source, double strength) {
		int protection = 1;
		boolean isPlayer = entity instanceof Player;
		if (isPlayer) {
			Player player = (Player) entity;
			if (!player.isCreative()) {
				for (int i = 0; i < player.getInventory().armor.size(); i++) {
					ItemStack next = player.getInventory().armor.get(i);
					if (next.getItem() instanceof ItemHazmatArmor) {
						protection++;
						float damage = (float) (strength * 2.15f) / 2169.9975f;
						if (Math.random() < damage) {
							int integerDamage = Math.round(damage);
							if (next.getDamageValue() > next.getMaxDamage() || next.hurthurt(integerDamage, entity.level().random, player instanceof ServerPlayer s ? s : null)) {
								player.getInventory().armor.set(i, ItemStack.EMPTY);
							}
						}
					}
				}
			}
		}
		Location end = new Location(entity.position().add(0, entity.getEyeHeight() / 2.0, 0));
		double radiation = 0;
		if (entity instanceof Player pl && (pl.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof ItemGeigerCounter || pl.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof ItemGeigerCounter)) {
			double already = radiationMap.get().containsKey(entity) ? radiationMap.get().get(entity) : 0;
			radiation = getRadiation(entity.level(), source, end, strength);
			radiationMap.get().put((Player) entity, already + radiation);
		}
		if (!(entity instanceof Player pl && pl.isCreative()) && protection < 5 && radiationMap.get().getOrDefault(entity, 11.0) > 4) {
			if (radiation == 0) {
				radiation = getRadiation(entity.level(), source, end, strength);
			}
			double distance = 1 + source.distance(end);
			double modifier = strength / (radiation * distance * distance);
			int amplitude = (int) Math.max(0, Math.min(strength / modifier / (distance * 4000.0), 9));
			int time = (int) (strength / modifier / ((amplitude + 1) * distance));
			if (amplitude == 0 && time <= 40) {
				return;
			}
			entity.addEffect(new MobEffectInstance(NuclearScienceEffects.RADIATION.get(), time, Math.min(40, amplitude), false, true));
		}
	}

	public static void emitRadiationFromLocation(Level level, Location source, double radius, double strength) {
		AABB bb = AABB.ofSize(new Vec3(source.x(), source.y(), source.z()), radius * 2, radius * 2, radius * 2);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, bb);
		for (LivingEntity living : list) {
			RadiationSystem.applyRadiation(living, source, strength);
		}
	}

	@SubscribeEvent
	public static void onTick(ServerTickEvent.Pre event) {
		radiationMap.get().clear();
	}

	private static int tick = 0;

	@SubscribeEvent
	public static void onTickC(ClientTickEvent.Post event) {
		tick++;
		if (tick % 20 == 0) {
			for (Map.Entry<Player, Double> en : ((HashMap<Player, Double>) radiationMap.get().clone()).entrySet()) {
				radiationMap.get().put(en.getKey(), en.getValue() * 0.3);
			}
			tick = 0;
		}
	}

	 */
}
