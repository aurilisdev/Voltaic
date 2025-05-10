package voltaic.api.radiation;

import voltaic.Voltaic;
import voltaic.api.radiation.util.BlockPosVolume;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.FORGE)
public class RadiationSystem {

	@SubscribeEvent
	public static void tickServer(WorldTickEvent event) {
		
		if(event.phase == Phase.END) {
			return;
		}

		World level = event.world;

		if(level.isClientSide()) {
			return;
		}

		IRadiationManager manager = level.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}

		manager.tick(level);


	}

	@SubscribeEvent
	public static void entityTick(PlayerTickEvent event) {
		
		if(event.player == null || event.player.level.isClientSide()) {
			return;
		}
		IRadiationRecipient capability = event.player.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
		if(capability == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
			return;
		}
		capability.tick((LivingEntity) event.player);

	}

	public static void addRadiationSource(World world, SimpleRadiationSource source) {
		if(source == null) {
			throw new UnsupportedOperationException("source cannot be null");
		}
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}
		manager.addRadiationSource(source, world);

	}

	public static void removeRadiationSource(World world, BlockPos pos, boolean shouldLinger) {
		if(pos == null) {
			throw new UnsupportedOperationException("position cannot be null");
		}
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}
		manager.removeRadiationSource(pos, shouldLinger, world);
	}

	public static List<BlockPos> getRadiationSources(World world) {
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return Collections.emptyList();
		}
		HashSet<BlockPos> sources = new HashSet<>();
		sources.addAll(manager.getPermanentLocations(world));
		sources.addAll(manager.getTemporaryLocations(world));
		sources.addAll(manager.getFadingLocations(world));
		return new ArrayList<>(sources);
	}

	public static void addDisipation(World world, double amount, BlockPosVolume volume) {
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}
		manager.setLocalizedDisipation(amount, volume, world);
	}

	public static void removeDisipation(World world, BlockPosVolume volume) {
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}
		manager.removeLocalizedDisipation(volume, world);
	}

	public static void wipeAllSources(World world) {
		IRadiationManager manager = world.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER).orElse(CapabilityUtils.EMPTY_MANAGER);
		
		if(manager == CapabilityUtils.EMPTY_MANAGER) {
			return;
		}
		manager.wipeAllSources(world);
	}


	/*
	public static ThreadLocal<HashMap<Player, Double>> radiationMap = ThreadLocal.withInitial(HashMap::new);

	private static double getRadiationModifier(World world, Location source, Location end) {
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

	public static double getRadiation(World world, Location source, Location end, double strength) {
		double distance = 1 + source.distance(end);
		return strength / (getRadiationModifier(world, source, end) * distance * distance);
	}

	public static void applyRadiation(LivingEntity entity, Location source, double strength) {
		int protection = 1;
		boolean isPlayer = entity instanceof Player;
		if (isPlayer) {
			PlayerEntity player = (Player) entity;
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
		if (entity instanceof PlayerEntity pl && (pl.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof ItemGeigerCounter || pl.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof ItemGeigerCounter)) {
			double already = radiationMap.get().containsKey(entity) ? radiationMap.get().get(entity) : 0;
			radiation = getRadiation(entity.level(), source, end, strength);
			radiationMap.get().put((Player) entity, already + radiation);
		}
		if (!(entity instanceof PlayerEntity pl && pl.isCreative()) && protection < 5 && radiationMap.get().getOrDefault(entity, 11.0) > 4) {
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

	public static void emitRadiationFromLocation(World level, Location source, double radius, double strength) {
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
