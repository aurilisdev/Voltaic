package voltaic.api.radiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.settings.VoltaicConstants;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.prefab.utilities.CodecUtils;
import voltaic.registers.VoltaicCapabilities;

public class RadiationManager implements IRadiationManager, ICapabilitySerializable<CompoundTag> {
    public static final double MIN_APPLIED_RADIATION = 0.1;

    private final LazyOptional<IRadiationManager> lazyOptional = LazyOptional.of(() -> this);

    private final HashMap<BlockPos, SimpleRadiationSource> permanentSources = new HashMap<>();
    private final HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource> temporarySources = new HashMap<>();
    private final HashMap<BlockPos, IRadiationManager.FadingRadiationSource> fadingSources = new HashMap<>();
    private final HashMap<AABB, Double> localizedDissipations = new HashMap<>();
    private double defaultRadiationDisipation = VoltaicConstants.BACKROUND_RADIATION_DISSIPATION;

    public RadiationManager() {

    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
	if (cap == VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER) {
	    return lazyOptional.cast();
	}
	return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
	CompoundTag total = new CompoundTag();

	// Permanent

	CompoundTag permanent = new CompoundTag();
	int size = permanentSources.size();
	permanent.putInt("size", size);
	int i = 0;
	for (Map.Entry<BlockPos, SimpleRadiationSource> entry : permanentSources.entrySet()) {
	    CompoundTag store = new CompoundTag();
	    BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result()
		    .ifPresent(tag -> store.put("pos", tag));
	    SimpleRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result()
		    .ifPresent(tag -> store.put("radiation", tag));
	    permanent.put(i + "", store);
	    i++;
	}

	total.put("permanentradiationsources", permanent);

	// Temporary

	CompoundTag temporary = new CompoundTag();
	size = temporarySources.size();
	temporary.putInt("size", size);
	i = 0;
	for (Map.Entry<BlockPos, IRadiationManager.TemporaryRadiationSource> entry : temporarySources.entrySet()) {
	    CompoundTag store = new CompoundTag();
	    BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result()
		    .ifPresent(tag -> store.put("pos", tag));
	    IRadiationManager.TemporaryRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result()
		    .ifPresent(tag -> store.put("radiation", tag));
	    temporary.put(i + "", store);
	    i++;
	}

	total.put("temporaryradiationsources", temporary);

	// Fading

	CompoundTag fading = new CompoundTag();
	size = fadingSources.size();
	fading.putInt("size", size);
	i = 0;
	for (Map.Entry<BlockPos, IRadiationManager.FadingRadiationSource> entry : fadingSources.entrySet()) {
	    CompoundTag store = new CompoundTag();
	    BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result()
		    .ifPresent(tag -> store.put("pos", tag));
	    IRadiationManager.FadingRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result()
		    .ifPresent(tag -> store.put("radiation", tag));
	    fading.put(i + "", store);
	    i++;
	}

	total.put("fadingradiationsources", fading);

	// Localized

	CompoundTag localized = new CompoundTag();
	size = localizedDissipations.size();
	localized.putInt("size", size);
	i = 0;
	for (Map.Entry<AABB, Double> entry : localizedDissipations.entrySet()) {
	    CompoundTag store = new CompoundTag();
	    CodecUtils.AABB_CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result()
		    .ifPresent(tag -> store.put("pos", tag));
	    store.putDouble("amount", entry.getValue());
	    localized.put(i + "", store);
	    i++;
	}

	total.put("localizeddissipations", localized);

	// Default Dissipation

	total.putDouble("defaultdissipation", defaultRadiationDisipation);

	return total;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
	if (VoltaicCapabilities.CAPABILITY_RADIATIONMANAGER == null || !nbt.contains("permanentradiationsources")) {
	    return;
	}

	// Permanent

	CompoundTag permanent = nbt.getCompound("permanentradiationsources");

	permanentSources.clear();

	int size = permanent.getInt("size");
	for (int i = 0; i < size; i++) {

	    CompoundTag stored = permanent.getCompound("" + i);
	    permanentSources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(),
		    SimpleRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation")))
			    .result().get());
	}

	// Temporary

	temporarySources.clear();

	CompoundTag temporary = nbt.getCompound("temporaryradiationsources");

	size = temporary.getInt("size");
	for (int i = 0; i < size; i++) {

	    CompoundTag stored = temporary.getCompound("" + i);
	    temporarySources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(),
		    IRadiationManager.TemporaryRadiationSource.CODEC
			    .parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).result().get());
	}

	// Fading

	fadingSources.clear();

	CompoundTag fading = nbt.getCompound("temporaryradiationsources");

	size = fading.getInt("size");
	for (int i = 0; i < size; i++) {

	    CompoundTag stored = fading.getCompound("" + i);
	    fadingSources.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(),
		    IRadiationManager.FadingRadiationSource.CODEC
			    .parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).result().get());
	}

	// Localized Dissipations

	localizedDissipations.clear();

	CompoundTag localized = nbt.getCompound("localizeddissipations");

	size = localized.getInt("size");
	for (int i = 0; i < size; i++) {

	    CompoundTag stored = localized.getCompound("" + i);
	    localizedDissipations.put(
		    CodecUtils.AABB_CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(),
		    stored.getDouble("amount"));
	}

	// Default Dissipation

	defaultRadiationDisipation = nbt.getDouble("defaultdissipation");

    }

    @Override
    public List<SimpleRadiationSource> getPermanentSources(Level world) {
	return new ArrayList<>(permanentSources.values());
    }

    @Override
    public List<TemporaryRadiationSource> getTemporarySources(Level world) {
	return new ArrayList<>(temporarySources.values());
    }

    @Override
    public List<FadingRadiationSource> getFadingSources(Level world) {
	return new ArrayList<>(fadingSources.values());
    }

    @Override
    public List<BlockPos> getPermanentLocations(Level world) {
	return new ArrayList<>(permanentSources.keySet());
    }

    @Override
    public List<BlockPos> getTemporaryLocations(Level world) {
	return new ArrayList<>(temporarySources.keySet());
    }

    @Override
    public List<BlockPos> getFadingLocations(Level world) {
	return new ArrayList<>(fadingSources.keySet());
    }

    @Override
    public void addRadiationSource(SimpleRadiationSource source, Level world) {
	if (source.isTemporary()) {

	    if (source.shouldCombine()) {
		TemporaryRadiationSource existing = temporarySources.getOrDefault(source.getSourceLocation(),
			TemporaryRadiationSource.NONE);
		TemporaryRadiationSource combined = new TemporaryRadiationSource(source.ticks() + existing.ticks,
			Math.max(source.getRadiationStrength(), existing.strength),
			source.getRadiationAmount() + existing.radiation,
			existing.leaveFading || source.shouldLeaveLingeringSource(),
			Math.max(source.distance(), existing.distance), source.getSourceLocation());
		temporarySources.put(source.getSourceLocation(), combined);
	    } else {
		temporarySources.put(source.getSourceLocation(),
			new TemporaryRadiationSource(source.ticks(), source.strength(), source.amount(),
				source.shouldLeaveLingeringSource(), source.distance(), source.getSourceLocation()));
	    }
	} else {

	    if (source.shouldCombine()) {
		SimpleRadiationSource existing = permanentSources.getOrDefault(source.getSourceLocation(),
			SimpleRadiationSource.NONE);
		permanentSources.put(source.getSourceLocation(),
			new SimpleRadiationSource(existing.getRadiationAmount() + source.getRadiationAmount(),
				Math.max(existing.getRadiationStrength(), source.getRadiationStrength()),
				Math.max(existing.getDistanceSpread(), source.getDistanceSpread()), false,
				existing.getPersistanceTicks() + source.getPersistanceTicks(),
				source.getSourceLocation(), existing.shouldLinger() || source.shouldLinger(),
				existing.shouldCombine() || source.shouldCombine()));
	    } else {
		permanentSources.put(source.getSourceLocation(), source);
	    }
	}
    }

    @Override
    public int getReachOfSource(Level world, BlockPos pos) {
	return Math.max(temporarySources.getOrDefault(pos, TemporaryRadiationSource.NONE).distance,
		Math.max(permanentSources.getOrDefault(pos, SimpleRadiationSource.NONE).getDistanceSpread(),
			fadingSources.getOrDefault(pos, FadingRadiationSource.NONE).distance));
    }

    @Override
    public void setDisipation(double radiationDisipation, Level world) {
	defaultRadiationDisipation = radiationDisipation;
    }

    @Override
    public void setLocalizedDisipation(double disipation, AABB area, Level world) {
	localizedDissipations.put(area, disipation + localizedDissipations.getOrDefault(area, 0.0));
    }

    @Override
    public void removeLocalizedDisipation(AABB area, Level world) {
	localizedDissipations.remove(area);
    }

    @Override
    public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, Level world) {
	SimpleRadiationSource source = permanentSources.remove(pos);
	if (source == null) {
	    return false;
	}
	if (shouldLeaveFadingSource) {
	    fadingSources.put(pos, new FadingRadiationSource(source.getDistanceSpread(), source.getRadiationStrength(),
		    source.getRadiationAmount(), pos));
	}
	return true;
    }

    @Override
    public void wipeAllSources(Level level) {
	permanentSources.clear();
	temporarySources.clear();
	fadingSources.clear();
    }

    @Override
    public void tick(Level world) {

	Iterator<Entity> entities = ((ServerLevel) world).getAllEntities().iterator();
	Entity entity;

	BlockPos position;
	IRadiationRecipient capability;

	SimpleRadiationSource permanentSource;

	FadingRadiationSource fadingSource;

	TemporaryRadiationSource temporarySource;

	while (entities.hasNext()) {

	    entity = entities.next();

	    if (!(entity instanceof LivingEntity living)) {
		continue;
	    }

	    if (!living.isAlive()) {
		continue;
	    }

	    capability = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT)
		    .orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);

	    if (capability == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
		continue;
	    }

	    /* Permanent Sources */

	    for (Map.Entry<BlockPos, SimpleRadiationSource> entryPerm : permanentSources.entrySet()) {

		position = entryPerm.getKey();
		permanentSource = entryPerm.getValue();

		if (!living.getBoundingBox().intersects(permanentSource.getBoundingBox())) {
		    continue;
		}

		applyRadiationFromSource(world, living, capability, position, permanentSource.getRadiationAmount(),
			permanentSource.getRadiationStrength());
	    }

	    /* Temporary Sources */

	    for (Map.Entry<BlockPos, TemporaryRadiationSource> entryTemp : temporarySources.entrySet()) {

		position = entryTemp.getKey();
		temporarySource = entryTemp.getValue();

		if (!living.getBoundingBox().intersects(temporarySource.boundingBox)) {
		    continue;
		}

		applyRadiationFromSource(world, living, capability, position, temporarySource.radiation,
			temporarySource.strength);
	    }

	    /* Fading Sources */

	    for (Map.Entry<BlockPos, FadingRadiationSource> entryFading : fadingSources.entrySet()) {

		position = entryFading.getKey();
		fadingSource = entryFading.getValue();

		if (!living.getBoundingBox().intersects(fadingSource.boundingBox)) {
		    continue;
		}

		applyRadiationFromSource(world, living, capability, position, fadingSource.radiation,
			fadingSource.strength);
	    }

	}

	/* Tick Temporary Radiation Sources */

	Iterator<Map.Entry<BlockPos, TemporaryRadiationSource>> iteratorTemp = temporarySources.entrySet().iterator();
	Map.Entry<BlockPos, TemporaryRadiationSource> entryTemp;

	while (iteratorTemp.hasNext()) {
	    entryTemp = iteratorTemp.next();
	    position = entryTemp.getKey();
	    temporarySource = entryTemp.getValue();

	    temporarySource.ticks = temporarySource.ticks - 1;

	    if (temporarySource.ticks < 0) {
		iteratorTemp.remove();
		if (temporarySource.leaveFading) {
		    FadingRadiationSource existing = fadingSources.getOrDefault(position, FadingRadiationSource.NONE);
		    fadingSources.put(position,
			    new FadingRadiationSource(Math.max(temporarySource.distance, existing.distance),
				    Math.max(temporarySource.strength, existing.strength),
				    Math.max(temporarySource.radiation, existing.radiation), position));
		}
	    }

	}

	/* Fading Radiation Sources */

	Iterator<Map.Entry<BlockPos, FadingRadiationSource>> iteratorFading = fadingSources.entrySet().iterator();
	Map.Entry<BlockPos, FadingRadiationSource> entryFading;

	while (iteratorFading.hasNext()) {

	    entryFading = iteratorFading.next();
	    fadingSource = entryFading.getValue();

	    for (Map.Entry<AABB, Double> localized : localizedDissipations.entrySet()) {
		if (localized.getKey().intersects(fadingSource.boundingBox)) {
		    fadingSource.radiation = fadingSource.radiation - localized.getValue();
		}
	    }

	    fadingSource.radiation = fadingSource.radiation - defaultRadiationDisipation;

	    if (fadingSource.radiation <= 0) {
		iteratorFading.remove();
	    }

	}

    }

    public static boolean isWithinRange(BlockPos start, BlockPos end, int distance) {
	if (Math.abs(end.getX() - start.getX()) > distance || Math.abs(end.getY() - start.getY()) > distance
		|| Math.abs(end.getZ() - start.getZ()) > distance) {
	    return false;
	}
	return true;
    }

    private static List<Vec3> getRadiationSamplePoints(Entity entity) {

	AABB box = entity.getBoundingBox();

	double x0 = Mth.lerp(0.1, box.minX, box.maxX);
	double x1 = Mth.lerp(1.0 - 0.1, box.minX, box.maxX);

	double y0 = Mth.lerp(0.1, box.minY, box.maxY);
	double y1 = Mth.lerp(1.0 - 0.1, box.minY, box.maxY);

	double z0 = Mth.lerp(0.1, box.minZ, box.maxZ);
	double z1 = Mth.lerp(1.0 - 0.1, box.minZ, box.maxZ);

	Vec3 center = box.getCenter();

	return List.of(center,

		new Vec3(x0, y0, z0), new Vec3(x0, y0, z1), new Vec3(x0, y1, z0), new Vec3(x0, y1, z1),

		new Vec3(x1, y0, z0), new Vec3(x1, y0, z1), new Vec3(x1, y1, z0), new Vec3(x1, y1, z1));
    }

    private static void applyRadiationFromSource(Level world, LivingEntity living, IRadiationRecipient capability,
	    BlockPos sourcePos, double radiationAmount, double strength) {

	List<Vec3> samples = getRadiationSamplePoints(living);

	Vec3 source = Vec3.atCenterOf(sourcePos);

	double amountPerSample = radiationAmount / samples.size();
	double totalApplied = 0.0;

	for (Vec3 sample : samples) {
	    totalApplied += getAppliedRadiation(world, source, sample, amountPerSample, strength);
	}

	if (totalApplied < MIN_APPLIED_RADIATION) {
	    return;
	}

	capability.recieveRadiation(living, totalApplied, strength);
    }

    public static List<Pair<BlockPos, BlockState>> raycastToBlockPos(Level world, Vec3 start, Vec3 end) {

	List<Pair<BlockPos, BlockState>> blocks = new ArrayList<>();

	double deltaX = end.x - start.x;
	double deltaY = end.y - start.y;
	double deltaZ = end.z - start.z;

	double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

	if (magnitude <= 0) {
	    return blocks;
	}

	int maxChecks = Math.max(1, (int) Math.ceil(magnitude));

	double incX = deltaX / maxChecks;
	double incY = deltaY / maxChecks;
	double incZ = deltaZ / maxChecks;

	BlockPos startBlock = BlockPos.containing(start);
	BlockPos lastChecked = null;

	for (int i = 0; i <= maxChecks; i++) {

	    BlockPos toCheck = BlockPos.containing(start.x + incX * i, start.y + incY * i, start.z + incZ * i);

	    if (toCheck.equals(lastChecked)) {
		continue;
	    }

	    lastChecked = toCheck;

	    // Do not count source block, but do count end block.
	    if (toCheck.equals(startBlock)) {
		continue;
	    }

	    blocks.add(new Pair<>(toCheck, world.getBlockState(toCheck)));
	}

	return blocks;
    }

    public static double getAppliedRadiation(Level world, Vec3 source, Vec3 entity, double amount, double strength) {

	List<Pair<BlockPos, BlockState>> pairs = raycastToBlockPos(world, source, entity);

	for (Pair<BlockPos, BlockState> pair : pairs) {

	    BlockPos pos = pair.getFirst();
	    BlockState state = pair.getSecond();

	    RadiationShielding shielding = RadiationShieldingRegister.getValue(state.getBlock());

	    if (shielding == RadiationShielding.NONE) {
		shielding = RadiationShielding.getDefault(world, pos, state);
	    }

	    if (shielding.level() < strength) {
		continue;
	    }

	    double transmission = shielding.transmission();

	    if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
		transmission = 1.0 - ((1.0 - transmission) * 0.20);
	    }

	    if (state.hasProperty(TrapDoorBlock.OPEN) && state.getValue(TrapDoorBlock.OPEN)) {
		transmission = 1.0 - ((1.0 - transmission) * 0.20);
	    }

	    amount *= transmission;

	    if (amount < MIN_APPLIED_RADIATION) {
		return 0;
	    }
	}

	double distanceSq = Math.max(1.0, entity.distanceToSqr(source));
	return amount / distanceSq;
    }

    public static double getAppliedRadiation(Level world, BlockPos source, BlockPos entity, double amount,
	    double strength) {

	return getAppliedRadiation(world, Vec3.atCenterOf(source), Vec3.atCenterOf(entity), amount, strength);
    }
}
