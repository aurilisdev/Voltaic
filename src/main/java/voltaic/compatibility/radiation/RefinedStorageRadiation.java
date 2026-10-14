package voltaic.compatibility.radiation;

import com.refinedmods.refinedstorage.api.storage.limited.LimitedStorage;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.storage.StorageBlockEntity;
import com.refinedmods.refinedstorage.common.api.storage.StorageContainerItem;
import com.refinedmods.refinedstorage.common.storage.AbstractDiskContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import voltaic.api.radiation.RadiationEmitterHandler;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/** RS 2 physical disks and storage blocks, independent of the network's power state. */
public final class RefinedStorageRadiation {
    private RefinedStorageRadiation() {}

    public static void register() {
        RadiationEmitterHandler.registerReader((blockEntity, contents) -> {
            if (blockEntity instanceof AbstractDiskContainerBlockEntity<?> drive) {
                var disks = drive.getDiskInventory();
                for (int slot = 0; slot < disks.getContainerSize(); slot++) {
                    contents.add(disks.getItem(slot));
                }
                return true;
            }
            if (blockEntity instanceof StorageBlockEntity block) {
                var id = block.getStorageId();
                if (id != null) {
                    RefinedStorageApi.INSTANCE.getStorageRepository(blockEntity.getLevel())
                            .get(id).ifPresent(storage -> read(storage, contents));
                }
                return true;
            }
            return false;
        });
        RadiationUtils.registerItemReader((stack, level, contents) -> {
            if (!(stack.getItem() instanceof StorageContainerItem disk)) return false;
            if (level != null) {
                disk.resolve(RefinedStorageApi.INSTANCE.getStorageRepository(level), stack)
                        .ifPresent(storage -> read(storage, contents));
            }
            return true;
        });
    }

    private static void read(SerializableStorage storage, ItemEmissionAccumulator contents) {
        // Creative/unbounded storage and proxy views are not physical finite stock.
        if (!(storage instanceof LimitedStorage)) return;
        contents.readStorage(storage, output -> {
            for (var entry : storage.getAll()) {
                if (!output.hasRemainingWork()) break;
                if (entry.resource() instanceof ItemResource item) {
                    output.add(item.toItemStack(), entry.amount());
                }
            }
        });
    }
}
