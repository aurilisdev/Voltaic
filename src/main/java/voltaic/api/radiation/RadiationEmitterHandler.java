package voltaic.api.radiation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import voltaic.Voltaic;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationManager.EmitterId;
import voltaic.common.settings.VoltaicConfig;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmission;
import voltaic.registers.VoltaicAttachmentTypes;

/**
 * Supplies radiation values for item containers and item frames to the
 * {@link IRadiationManager}, which owns their lifetime and applies exposure.
 * <p>
 * <b>Item frames</b> refresh their emission on every entity tick (like dropped
 * items). An empty or removed frame clears its emission. Frames refresh with
 * {@value #FRAME_EXPIRY_TICKS} remaining tick(s): the manager ticks at the start
 * of the level tick, before entities, so a refresh is always seen by the next
 * manager tick, and a frame whose removal was missed stops emitting at most two
 * ticks later.
 * <p>
 * <b>Containers</b> are checked every {@value #CONTAINER_CHECK_INTERVAL} ticks,
 * staggered across loaded chunks. Only each block entity's own slots are read, so
 * both halves of a double chest are counted exactly once (by their own block
 * entity). Changes - including hopper transfers - are therefore noticed after a
 * delay of up to {@value #CONTAINER_CHECK_INTERVAL} ticks; instant updates would
 * require inventory change hooks. Unopened loot containers (those with a pending
 * loot table) and Voltaic machines (which handle their own emissions) are
 * skipped. An empty container clears its emission when checked; unloading a
 * chunk removes its emissions immediately, and they are rebuilt by the first
 * check after the chunk loads (emissions are not saved).
 * <p>
 * <b>Shielding:</b> the manager's ray tracer ignores the block a source sits in,
 * so the contents of a container are not shielded by the container itself.
 * Surrounding blocks (e.g. lead walls) still shield as usual. A purpose built
 * shielded container needs its own rule for how much radiation escapes.
 */
@EventBusSubscriber(modid = Voltaic.ID)
public class RadiationEmitterHandler {

    public static final int FRAME_EXPIRY_TICKS = 1;
    public static final int CONTAINER_CHECK_INTERVAL = 20;
    /* Covers the check interval plus one missed check before expiry. */
    private static final int CONTAINER_EXPIRY_TICKS = CONTAINER_CHECK_INTERVAL + 1;

    /* chunk positions per level, bucketed by the tick on which they are checked */
    private static final Map<ServerLevel, List<Set<Long>>> TRACKED_CHUNKS = new HashMap<>();

    private static IRadiationManager manager(Level level) {
	return level.getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
    }

    private static int bucketOf(long chunkKey) {
	return Math.floorMod(Long.hashCode(chunkKey), CONTAINER_CHECK_INTERVAL);
    }

    /* Frames */

    @SubscribeEvent
    public static void frameTick(EntityTickEvent.Post event) {
	if (!(event.getEntity() instanceof ItemFrame frame) || frame.level().isClientSide()
		|| VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse())
	    return;
	Level level = frame.level();
	EmitterId id = EmitterId.entity(frame.getUUID());
	ItemStack stack = frame.getItem();
	if (stack.isEmpty() || frame.isRemoved()) {
	    manager(level).clearEmitter(id, level);
	    return;
	}
	ItemEmission emission = RadiationUtils.calculateItemEmission(List.of(stack));
	if (!emission.isRadioactive()) {
	    manager(level).clearEmitter(id, level);
	    return;
	}
	manager(level).refreshEmitter(id, new SimpleRadiationSource(emission.amount(), emission.strength(),
		emission.range(), true, FRAME_EXPIRY_TICKS, frame.blockPosition(), false, false), level);
    }

    @SubscribeEvent
    public static void frameLeave(EntityLeaveLevelEvent event) {
	if (event.getEntity() instanceof ItemFrame frame && !frame.level().isClientSide()) {
	    manager(frame.level()).clearEmitter(EmitterId.entity(frame.getUUID()), frame.level());
	}
    }

    /* Containers */

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
	if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk))
	    return;
	long key = chunk.getPos().toLong();
	TRACKED_CHUNKS.computeIfAbsent(level, l -> {
	    List<Set<Long>> buckets = new java.util.ArrayList<>(CONTAINER_CHECK_INTERVAL);
	    for (int i = 0; i < CONTAINER_CHECK_INTERVAL; i++) {
		buckets.add(new HashSet<>());
	    }
	    return buckets;
	}).get(bucketOf(key)).add(key);
    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event) {
	if (!(event.getLevel() instanceof ServerLevel level))
	    return;
	ChunkPos pos = event.getChunk().getPos();
	List<Set<Long>> buckets = TRACKED_CHUNKS.get(level);
	if (buckets != null) {
	    buckets.get(bucketOf(pos.toLong())).remove(pos.toLong());
	}
	manager(level).clearEmittersInChunk(pos, level);
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event) {
	if (event.getLevel() instanceof ServerLevel level) {
	    TRACKED_CHUNKS.remove(level);
	}
    }

    @SubscribeEvent
    public static void containerScan(LevelTickEvent.Post event) {
	if (!(event.getLevel() instanceof ServerLevel level)
		|| VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse())
	    return;
	List<Set<Long>> buckets = TRACKED_CHUNKS.get(level);
	if (buckets == null)
	    return;
	int bucket = (int) Math.floorMod(level.getGameTime(), (long) CONTAINER_CHECK_INTERVAL);
	for (long key : buckets.get(bucket)) {
	    LevelChunk chunk = level.getChunkSource().getChunkNow(ChunkPos.getX(key), ChunkPos.getZ(key));
	    if (chunk == null)
		continue;
	    for (BlockEntity be : chunk.getBlockEntities().values()) {
		checkContainer(level, be);
	    }
	}
    }

    private static void checkContainer(ServerLevel level, BlockEntity be) {
	if (be.isRemoved() || !(be instanceof Container container) || be instanceof GenericTile)
	    return;
	BlockPos pos = be.getBlockPos();
	EmitterId id = EmitterId.container(pos);
	if (be instanceof RandomizableContainer randomizable && randomizable.getLootTable() != null) {
	    manager(level).clearEmitter(id, level);
	    return;
	}
	if (container.isEmpty()) {
	    manager(level).clearEmitter(id, level);
	    return;
	}
	List<ItemStack> items = new java.util.ArrayList<>(container.getContainerSize());
	for (int i = 0; i < container.getContainerSize(); i++) {
	    items.add(container.getItem(i));
	}
	ItemEmission emission = RadiationUtils.calculateItemEmission(items);
	if (!emission.isRadioactive()) {
	    manager(level).clearEmitter(id, level);
	    return;
	}
	manager(level).refreshEmitter(id, new SimpleRadiationSource(emission.amount(), emission.strength(),
		emission.range(), true, CONTAINER_EXPIRY_TICKS, pos, false, false), level);
    }
}
