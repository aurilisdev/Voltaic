package voltaic.api.radiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicCapabilities;

public class RadiationManager implements IRadiationManager {
    public static final double MIN_APPLIED_RADIATION = 0.1;
    private static final int EXPOSURE_CACHE_INTERVAL = 10;
    private static final int ENTITY_CACHE_INTERVAL = 10;
    private static final int CACHE_ENTRY_EXPIRY_TICKS = 40;
    /*
     * Recalculate immediately if an entity moves more than half a block (sqrt(0.25)
     * = 0.5).
     */
    private static final double EXPOSURE_CACHE_MOVE_DISTANCE_SQ = 0.25;

    private final Map<ExposureKey, CachedExposure> exposureCache = new HashMap<>();
    private final Map<Long, CachedEntities> entityCache = new HashMap<>();

    @Override
    public List<SimpleRadiationSource> getPermanentSources(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).values());
    }

    @Override
    public List<TemporaryRadiationSource> getTemporarySources(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).values());
    }

    @Override
    public List<FadingRadiationSource> getFadingSources(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).values());
    }

    @Override
    public List<BlockPos> getPermanentLocations(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES).keySet());
    }

    @Override
    public List<BlockPos> getTemporaryLocations(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).keySet());
    }

    @Override
    public List<BlockPos> getFadingLocations(Level world) {
	return new ArrayList<>(world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).keySet());
    }

    @Override
    public void addRadiationSource(SimpleRadiationSource source, Level world) {
	if (source.isTemporary()) {
	    HashMap<BlockPos, TemporaryRadiationSource> sources = world
		    .getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
	    if (source.shouldCombine()) {

		TemporaryRadiationSource existing = sources.getOrDefault(source.getSourceLocation(),
			TemporaryRadiationSource.NONE);
		TemporaryRadiationSource combined = new TemporaryRadiationSource(source.ticks() + existing.ticks,
			Math.max(source.getRadiationStrength(), existing.strength),
			source.getRadiationAmount() + existing.radiation,
			existing.leaveFading || source.shouldLeaveLingeringSource(),
			Math.max(source.distance(), existing.distance), source.getSourceLocation());
		sources.put(source.getSourceLocation(), combined);
	    } else {
		sources.put(source.getSourceLocation(),
			new TemporaryRadiationSource(source.ticks(), source.strength(), source.amount(),
				source.shouldLeaveLingeringSource(), source.distance(), source.getSourceLocation()));
	    }
	    world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, sources);
	} else {
	    HashMap<BlockPos, SimpleRadiationSource> sources = world
		    .getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);

	    if (source.shouldCombine()) {
		SimpleRadiationSource existing = sources.getOrDefault(source.getSourceLocation(),
			SimpleRadiationSource.NONE);
		sources.put(source.getSourceLocation(),
			new SimpleRadiationSource(existing.getRadiationAmount() + source.getRadiationAmount(),
				Math.max(existing.getRadiationStrength(), source.getRadiationStrength()),
				Math.max(existing.getDistanceSpread(), source.getDistanceSpread()), false,
				existing.getPersistanceTicks() + source.getPersistanceTicks(),
				source.getSourceLocation(), existing.shouldLinger() || source.shouldLinger(),
				existing.shouldCombine() || source.shouldCombine()));
	    } else {
		sources.put(source.getSourceLocation(), source);
	    }
	    world.setData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES, sources);
	}
    }

    @Override
    public int getReachOfSource(Level world, BlockPos pos) {
	return Math.max(
		world.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES).getOrDefault(pos,
			TemporaryRadiationSource.NONE).distance,
		Math.max(
			world.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES)
				.getOrDefault(pos, SimpleRadiationSource.NONE).getDistanceSpread(),
			world.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES).getOrDefault(pos,
				FadingRadiationSource.NONE).distance));
    }

    @Override
    public void setDisipation(double radiationDisipation, Level world) {
	world.setData(VoltaicAttachmentTypes.DEFAULT_DISSIPATION, radiationDisipation);
    }

    @Override
    public void setLocalizedDisipation(double disipation, AABB area, Level world) {
	HashMap<AABB, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
	values.put(area, disipation + values.getOrDefault(area, 0.0));
	world.setData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS, values);
    }

    @Override
    public void removeLocalizedDisipation(AABB area, Level world) {
	HashMap<AABB, Double> values = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);
	values.remove(area);
	world.setData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS, values);
    }

    @Override
    public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, Level world) {
	HashMap<BlockPos, SimpleRadiationSource> sources = world
		.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
	SimpleRadiationSource source = sources.remove(pos);
	world.setData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES, sources);
	if (source == null) {
	    return false;
	}
	if (shouldLeaveFadingSource) {
	    HashMap<BlockPos, FadingRadiationSource> fadingSources = world
		    .getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
	    fadingSources.put(pos, new FadingRadiationSource(source.getDistanceSpread(), source.getRadiationStrength(),
		    source.getRadiationAmount(), pos));
	    world.setData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES, fadingSources);
	}
	return true;
    }

    @Override
    public void wipeAllSources(Level level) {
	level.removeData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);
	level.removeData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
	level.removeData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
    }

    @Override
    public void tick(Level world) {
	BlockPos position;

	HashMap<BlockPos, SimpleRadiationSource> permanentSources = world
		.getData(VoltaicAttachmentTypes.PERMANENT_RADIATION_SOURCES);

	HashMap<BlockPos, FadingRadiationSource> fadingSources = world
		.getData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES);
	FadingRadiationSource fadingSource;

	HashMap<BlockPos, TemporaryRadiationSource> temporarySources = world
		.getData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES);
	TemporaryRadiationSource temporarySource;
	ServerLevel serverLevel = (ServerLevel) world;

	/* Permanent sources */

	for (Map.Entry<BlockPos, SimpleRadiationSource> entry : permanentSources.entrySet()) {

	    SimpleRadiationSource source = entry.getValue();

	    applySourceToNearbyEntities(serverLevel, entry.getKey(), source.getBoundingBox(),
		    source.getRadiationAmount(), source.getRadiationStrength());
	}

	/* Temporary sources */

	for (Map.Entry<BlockPos, TemporaryRadiationSource> entry : temporarySources.entrySet()) {

	    TemporaryRadiationSource source = entry.getValue();

	    applySourceToNearbyEntities(serverLevel, entry.getKey(), source.boundingBox, source.radiation,
		    source.strength);
	}

	/* Fading sources */

	for (Map.Entry<BlockPos, FadingRadiationSource> entry : fadingSources.entrySet()) {

	    FadingRadiationSource source = entry.getValue();

	    applySourceToNearbyEntities(serverLevel, entry.getKey(), source.boundingBox, source.radiation,
		    source.strength);
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

	world.setData(VoltaicAttachmentTypes.TEMPORARY_RADIATION_SOURCES, temporarySources);

	/* Fading Radiation Sources */

	Iterator<Map.Entry<BlockPos, FadingRadiationSource>> iteratorFading = fadingSources.entrySet().iterator();
	Map.Entry<BlockPos, FadingRadiationSource> entryFading;

	double defaultRadiationDisipation = world.getData(VoltaicAttachmentTypes.DEFAULT_DISSIPATION);
	HashMap<AABB, Double> localizedDissipations = world.getData(VoltaicAttachmentTypes.LOCALIZED_DISSIPATIONS);

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

	world.setData(VoltaicAttachmentTypes.FADING_RADIATION_SOURCES, fadingSources);
	cleanCaches(world.getGameTime());
    }

    private void cleanCaches(long gameTime) {

	if (gameTime % 100L != 0L) {
	    return;
	}

	exposureCache.entrySet().removeIf(entry -> gameTime - entry.getValue().lastSeenTick > CACHE_ENTRY_EXPIRY_TICKS);

	entityCache.entrySet().removeIf(entry -> gameTime - entry.getValue().lastSeenTick > CACHE_ENTRY_EXPIRY_TICKS);
    }

    private void applySourceToNearbyEntities(ServerLevel world, BlockPos sourcePos, AABB sourceBounds,
	    double radiationAmount, double strength) {

	Vec3 source = Vec3.atCenterOf(sourcePos);

	List<LivingEntity> entities = getCachedEntities(world, sourcePos, sourceBounds);
	for (LivingEntity living : entities) {
	    if (!living.isAlive() || living.level() != world || !living.getBoundingBox().intersects(sourceBounds)) {
		continue;
	    }
	    IRadiationRecipient capability = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);

	    if (capability == null) {
		continue;
	    }

	    applyRadiationFromSource(world, living, capability, sourcePos, source, radiationAmount, strength);
	}
    }

    @SuppressWarnings("null")
    private void applyRadiationFromSource(Level world, LivingEntity living, IRadiationRecipient capability,
	    BlockPos sourcePos, Vec3 source, double radiationAmount, double strength) {

	double closestDistanceSq = Math.max(1.0, distanceToAabbSqr(source, living.getBoundingBox()));

	/*
	 * Even completely unshielded radiation would be too weak.
	 */
	if (radiationAmount / closestDistanceSq < MIN_APPLIED_RADIATION) {
	    return;
	}

	long gameTime = world.getGameTime();
	Vec3 entityCenter = living.getBoundingBox().getCenter();

	ExposureKey key = new ExposureKey(sourcePos.asLong(), living.getId(), Double.doubleToLongBits(strength));

	CachedExposure cached = exposureCache.get(key);

	boolean moved = cached != null
		&& cached.entityCenter.distanceToSqr(entityCenter) > EXPOSURE_CACHE_MOVE_DISTANCE_SQ;

	boolean refresh = cached == null || gameTime >= cached.nextRefreshTick || moved;

	if (refresh) {

	    boolean newEntry = cached == null;

	    if (newEntry) {
		cached = new CachedExposure();
		exposureCache.put(key, cached);
	    }

	    cached.entityCenter = entityCenter;
	    cached.rayFactors = calculateRadiationFactors(world, living, source, strength);

	    if (newEntry) {
		/*
		 * Spread newly created cache entries over the next five ticks so that several
		 * radiation sources do not all refresh on the same tick.
		 */
		int offset = Math.floorMod(Long.hashCode(sourcePos.asLong()) + living.getId(), EXPOSURE_CACHE_INTERVAL);

		cached.nextRefreshTick = gameTime + 1L + offset;
	    } else {
		cached.nextRefreshTick = gameTime + EXPOSURE_CACHE_INTERVAL;
	    }
	}

	cached.lastSeenTick = gameTime;

	double[] factors = cached.rayFactors;

	if (factors == null || factors.length == 0) {
	    return;
	}

	double amountPerRay = radiationAmount / factors.length;
	double minimumPerRay = MIN_APPLIED_RADIATION / factors.length;

	double totalApplied = 0.0;

	for (double factor : factors) {

	    double applied = amountPerRay * factor;

	    if (applied >= minimumPerRay) {
		totalApplied += applied;
	    }
	}

	if (totalApplied < MIN_APPLIED_RADIATION) {
	    return;
	}

	capability.recieveRadiation(living, totalApplied, strength);
    }

    private static double[] calculateRadiationFactors(Level world, LivingEntity living, Vec3 source, double strength) {

	List<Vec3> samples = getRadiationSamplePoints(living, source, world.getGameTime());

	double[] factors = new double[samples.size()];

	for (int i = 0; i < samples.size(); i++) {

	    /*
	     * An amount of 1 and minimum of 0 makes the result:
	     *
	     * combined transmission / distance squared
	     *
	     * It is independent of the source's current radiation amount.
	     */
	    factors[i] = getAppliedRadiation(world, source, samples.get(i), 1.0, strength, 0.0);
	}

	return factors;
    }

    private static List<Vec3> getRadiationSamplePoints(Entity entity, Vec3 source, long gameTime) {

	AABB box = entity.getBoundingBox();
	Vec3 center = box.getCenter();

	double widthX = box.maxX - box.minX;
	double widthY = box.maxY - box.minY;
	double widthZ = box.maxZ - box.minZ;

	double entitySize = Math.max(widthX, Math.max(widthY, widthZ));
	entitySize = Math.max(entitySize, 0.1);

	double relativeDistanceSq = source.distanceToSqr(center) / (entitySize * entitySize);

	double x0 = Mth.lerp(0.1, box.minX, box.maxX);
	double x1 = Mth.lerp(0.9, box.minX, box.maxX);

	double y0 = Mth.lerp(0.1, box.minY, box.maxY);
	double y1 = Mth.lerp(0.9, box.minY, box.maxY);

	double z0 = Mth.lerp(0.1, box.minZ, box.maxZ);
	double z1 = Mth.lerp(0.9, box.minZ, box.maxZ);

	/*
	 * Nearby sources have noticeably different paths to each part of the entity, so
	 * retain all nine samples.
	 */
	if (relativeDistanceSq < 25.0) {
	    return List.of(center,

		    new Vec3(x0, y0, z0), new Vec3(x0, y0, z1), new Vec3(x0, y1, z0), new Vec3(x0, y1, z1),

		    new Vec3(x1, y0, z0), new Vec3(x1, y0, z1), new Vec3(x1, y1, z0), new Vec3(x1, y1, z1));
	}

	Vec3[] facingCorners = getSourceFacingCorners(source, center, x0, x1, y0, y1, z0, z1);

	/*
	 * At medium range, sample the centre and the four corners of the side facing
	 * the radiation source.
	 */
	if (relativeDistanceSq < 225.0) {
	    return List.of(center, facingCorners[0], facingCorners[1], facingCorners[2], facingCorners[3]);
	}

	/*
	 * At long range, rays to the different corners are nearly parallel. Use the
	 * centre and one rotating source-facing corner so no permanent blind spot is
	 * introduced.
	 */
	long sampleCycle = gameTime / 5L;
	int cornerIndex = Math.floorMod(entity.getId() + (int) sampleCycle, facingCorners.length);

	Vec3 selectedCorner = facingCorners[cornerIndex];

	return List.of(center, selectedCorner);
    }

    private static Vec3[] getSourceFacingCorners(Vec3 source, Vec3 center, double x0, double x1, double y0, double y1,
	    double z0, double z1) {

	double deltaX = source.x - center.x;
	double deltaY = source.y - center.y;
	double deltaZ = source.z - center.z;

	double absX = Math.abs(deltaX);
	double absY = Math.abs(deltaY);
	double absZ = Math.abs(deltaZ);

	/*
	 * Select the dominant source-facing side of the entity's bounding box.
	 */
	if (absX >= absY && absX >= absZ) {

	    double x = deltaX < 0.0 ? x0 : x1;

	    return new Vec3[] { new Vec3(x, y0, z0), new Vec3(x, y0, z1), new Vec3(x, y1, z0), new Vec3(x, y1, z1) };
	}

	if (absY >= absX && absY >= absZ) {

	    double y = deltaY < 0.0 ? y0 : y1;

	    return new Vec3[] { new Vec3(x0, y, z0), new Vec3(x0, y, z1), new Vec3(x1, y, z0), new Vec3(x1, y, z1) };
	}

	double z = deltaZ < 0.0 ? z0 : z1;

	return new Vec3[] { new Vec3(x0, y0, z), new Vec3(x0, y1, z), new Vec3(x1, y0, z), new Vec3(x1, y1, z) };
    }

    private static double distanceToAabbSqr(Vec3 point, AABB box) {

	double deltaX = Math.max(Math.max(box.minX - point.x, 0.0), point.x - box.maxX);

	double deltaY = Math.max(Math.max(box.minY - point.y, 0.0), point.y - box.maxY);

	double deltaZ = Math.max(Math.max(box.minZ - point.z, 0.0), point.z - box.maxZ);

	return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    private static double initialBoundaryDistance(double start, double delta, int blockCoordinate, int step) {

	if (step == 0) {
	    return Double.POSITIVE_INFINITY;
	}

	double nextBoundary = step > 0 ? blockCoordinate + 1.0 : blockCoordinate;

	return (nextBoundary - start) / delta;
    }

    public static double getAppliedRadiation(Level world, Vec3 source, Vec3 entity, double amount, double strength,
	    double minimumPerRay) {

	double distanceSq = Math.max(1.0, entity.distanceToSqr(source));

	/*
	 * Radiation cannot become stronger while travelling through blocks. Avoid ray
	 * tracing when even completely unshielded radiation would be negligible.
	 */
	if (amount / distanceSq < minimumPerRay) {
	    return 0.0;
	}

	int x = Mth.floor(source.x);
	int y = Mth.floor(source.y);
	int z = Mth.floor(source.z);

	int endX = Mth.floor(entity.x);
	int endY = Mth.floor(entity.y);
	int endZ = Mth.floor(entity.z);

	/*
	 * The source and destination are inside the same block. The source block is
	 * deliberately not counted as shielding.
	 */
	if (x == endX && y == endY && z == endZ) {
	    return amount / distanceSq;
	}

	double deltaX = entity.x - source.x;
	double deltaY = entity.y - source.y;
	double deltaZ = entity.z - source.z;

	int stepX = Double.compare(deltaX, 0.0);
	int stepY = Double.compare(deltaY, 0.0);
	int stepZ = Double.compare(deltaZ, 0.0);

	double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaX);
	double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaY);
	double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaZ);

	double tMaxX = initialBoundaryDistance(source.x, deltaX, x, stepX);
	double tMaxY = initialBoundaryDistance(source.y, deltaY, y, stepY);
	double tMaxZ = initialBoundaryDistance(source.z, deltaZ, z, stepZ);
	BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

	LevelChunk currentChunk = null;
	int currentChunkX = Integer.MIN_VALUE;
	int currentChunkZ = Integer.MIN_VALUE;

	while (x != endX || y != endY || z != endZ) {

	    double nextT = Math.min(x == endX ? Double.POSITIVE_INFINITY : tMaxX, Math
		    .min(y == endY ? Double.POSITIVE_INFINITY : tMaxY, z == endZ ? Double.POSITIVE_INFINITY : tMaxZ));

	    if (x != endX && tMaxX <= nextT) {
		x += stepX;
		tMaxX += tDeltaX;
	    }

	    if (y != endY && tMaxY <= nextT) {
		y += stepY;
		tMaxY += tDeltaY;
	    }

	    if (z != endZ && tMaxZ <= nextT) {
		z += stepZ;
		tMaxZ += tDeltaZ;
	    }

	    mutablePos.set(x, y, z);

	    int chunkX = x >> 4;
	    int chunkZ = z >> 4;

	    if (chunkX != currentChunkX || chunkZ != currentChunkZ) {

		currentChunk = ((ServerLevel) world).getChunkSource().getChunkNow(chunkX, chunkZ);
		currentChunkX = chunkX;
		currentChunkZ = chunkZ;
	    }

	    /*
	     * Do not load or generate terrain for radiation checks. Unloaded chunks
	     * contribute no shielding.
	     */
	    if (currentChunk == null) {
		continue;
	    }

	    BlockState state = currentChunk.getBlockState(mutablePos);

	    if (state.isAir()) {
		continue;
	    }

	    RadiationShielding shielding = RadiationShieldingRegister.getValue(state.getBlock());

	    if (shielding == RadiationShielding.NONE) {
		shielding = RadiationShielding.getDefault(world, mutablePos, state);
	    }

	    if (shielding.level() < strength) {
		continue;
	    }

	    double transmission = getAdjustedTransmission(state, shielding);

	    amount *= transmission;

	    if (amount / distanceSq < minimumPerRay) {
		return 0.0;
	    }
	}
	return amount / distanceSq;
    }

    private static double getAdjustedTransmission(BlockState state, RadiationShielding shielding) {

	double transmission = shielding.transmission();

	if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
	    transmission = 1.0 - ((1.0 - transmission) * 0.20);
	}

	if (state.hasProperty(TrapDoorBlock.OPEN) && state.getValue(TrapDoorBlock.OPEN)) {
	    transmission = 1.0 - ((1.0 - transmission) * 0.20);
	}

	return Mth.clamp(transmission, 0.0, 1.0);
    }

    public static double getAppliedRadiation(Level world, Vec3 source, Vec3 entity, double amount, double strength) {

	return getAppliedRadiation(world, source, entity, amount, strength, MIN_APPLIED_RADIATION);
    }

    public static double getAppliedRadiation(Level world, BlockPos source, BlockPos entity, double amount,
	    double strength) {

	return getAppliedRadiation(world, Vec3.atCenterOf(source), Vec3.atCenterOf(entity), amount, strength,
		MIN_APPLIED_RADIATION);
    }

    @SuppressWarnings("null")
    private List<LivingEntity> getCachedEntities(ServerLevel world, BlockPos sourcePos, AABB sourceBounds) {

	long gameTime = world.getGameTime();
	long key = sourcePos.asLong();

	CachedEntities cached = entityCache.get(key);

	boolean refresh = cached == null || gameTime >= cached.nextRefreshTick || !sourceBounds.equals(cached.bounds);

	if (refresh) {

	    if (cached == null) {
		cached = new CachedEntities();
		entityCache.put(key, cached);
	    }

	    cached.bounds = sourceBounds;
	    cached.entities = world.getEntitiesOfClass(LivingEntity.class, sourceBounds, LivingEntity::isAlive);

	    int offset = Math.floorMod(Long.hashCode(key), ENTITY_CACHE_INTERVAL);

	    cached.nextRefreshTick = gameTime + ENTITY_CACHE_INTERVAL + offset;
	}

	cached.lastSeenTick = gameTime;

	return cached.entities;
    }

    private record ExposureKey(long sourcePos, int entityId, long strengthBits) {
    }

    private static class CachedExposure {
	private Vec3 entityCenter;
	private double[] rayFactors;
	private long nextRefreshTick;
	private long lastSeenTick;
    }

    private static final class CachedEntities {
	private AABB bounds;
	private List<LivingEntity> entities;
	private long nextRefreshTick;
	private long lastSeenTick;
    }
}
