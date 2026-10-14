package voltaic.api.radiation;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.Voltaic;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/**
 * A capability alone cannot tell us whether stock is local or a remote network
 * view. Physical capability inventories opt in through a block tag. Native
 * Container slots and specialised adapters always take precedence.
 */
public final class LocalInventoryRadiation {
    public static final TagKey<Block> LOCAL_INVENTORIES = TagKey.create(Registries.BLOCK,
            Voltaic.rl("radiation/local_item_inventories"));

    private LocalInventoryRadiation() {}

    public static boolean read(BlockEntity blockEntity, ItemEmissionAccumulator contents) {
        if (!blockEntity.getBlockState().is(LOCAL_INVENTORIES)) return false;
        var level = blockEntity.getLevel();
        if (level == null) return true;
        IItemHandler inventory = level.getCapability(Capabilities.ItemHandler.BLOCK,
                blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        if (inventory == null) {
            // Select one view. Sided views can overlap and must never be added together.
            for (Direction side : Direction.values()) {
                inventory = level.getCapability(Capabilities.ItemHandler.BLOCK,
                        blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, side);
                if (inventory != null) break;
            }
        }
        if (inventory != null) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                contents.add(inventory.getStackInSlot(slot));
            }
        }
        return true;
    }
}
