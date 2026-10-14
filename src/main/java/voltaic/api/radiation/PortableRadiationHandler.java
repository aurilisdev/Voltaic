package voltaic.api.radiation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import voltaic.Voltaic;
import voltaic.api.radiation.util.IRadiationManager.EmitterId;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.common.settings.VoltaicConfig;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmission;
import voltaic.registers.VoltaicAttachmentTypes;

/** Shared stored item rules for drops and carriers, including vanilla and optional storage items. */
@EventBusSubscriber(modid = Voltaic.ID)
public final class PortableRadiationHandler {
    private static final int REFRESH_INTERVAL = 20;
    private static final Map<Entity, CachedContents> CACHE = new WeakHashMap<>();

    private record CachedContents(long tick, List<ItemStack> snapshot, ItemEmission emission) {
        boolean matches(List<ItemStack> stacks) {
            if (stacks.size() != snapshot.size()) return false;
            for (int slot = 0; slot < stacks.size(); slot++) {
                if (!ItemStack.matches(stacks.get(slot), snapshot.get(slot))) return false;
            }
            return true;
        }
    }

    private PortableRadiationHandler() {}

    static ItemEmission contents(Entity entity, List<ItemStack> stacks) {
        long tick = entity.level().getGameTime();
        CachedContents cached = CACHE.get(entity);
        if (cached != null && tick >= cached.tick() && tick - cached.tick() < REFRESH_INTERVAL
                && cached.matches(stacks)) return cached.emission();
        entity.level().getProfiler().push("voltaicRadiationContents");
        try {
            ItemEmission emission = RadiationUtils.calculateItemEmission(entity.level(), stacks);
            List<ItemStack> snapshot = stacks.stream().map(ItemStack::copy).toList();
            CACHE.put(entity, new CachedContents(tick, snapshot, emission));
            return emission;
        } finally {
            entity.level().getProfiler().pop();
        }
    }

    /** Called before the recipient rolls its current tick's dose into the display values. */
    public static void exposeCarrier(LivingEntity entity, IRadiationRecipient recipient) {
        List<ItemStack> stacks = new ArrayList<>();
        if (entity instanceof Player player) {
            var inventory = player.getInventory();
            // Includes hotbar, main slots, armour and offhand once; never the open menu's slots.
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                stacks.add(inventory.getItem(slot));
            }
        } else {
            entity.getAllSlots().forEach(stacks::add);
        }
        ItemEmission emission = contents(entity, stacks);
        if (emission.isRadioactive()) {
            // Cache the calculation, not the exposure: full dose on every server tick.
            recipient.recieveRadiation(entity, emission.amount(), emission.strength());
        }
    }

    @SubscribeEvent
    public static void itemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item) || item.level().isClientSide()
                || VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse()) return;
        var manager = item.level().getData(VoltaicAttachmentTypes.RADIATION_MANAGER);
        EmitterId id = EmitterId.entity(item.getUUID());
        ItemEmission emission = contents(item, List.of(item.getItem()));
        if (item.isRemoved() || !emission.isRadioactive()) {
            manager.clearEmitter(id, item.level());
            return;
        }
        manager.refreshEmitter(id, new SimpleRadiationSource(emission.amount(), emission.strength(),
                emission.range(), true, 1, item.blockPosition(), false, false), item.level());
    }

    @SubscribeEvent
    public static void entityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel().isClientSide()) return;
        CACHE.remove(entity);
        if (entity instanceof ItemEntity) {
            event.getLevel().getData(VoltaicAttachmentTypes.RADIATION_MANAGER)
                    .clearEmitter(EmitterId.entity(entity.getUUID()), event.getLevel());
        }
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        CACHE.keySet().removeIf(entity -> entity.level() == event.getLevel());
    }

    @SubscribeEvent
    public static void datapackSync(OnDatapackSyncEvent event) {
        clearCache();
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
