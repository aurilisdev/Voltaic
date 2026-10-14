package voltaic.compatibility.radiation;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.blockentity.storage.MEChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.me.cells.BasicCellInventory;
import voltaic.api.radiation.RadiationEmitterHandler;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/** Loaded only with AE2 present. Reads finite physical cells, never grid storage. */
public final class AE2Radiation {
    private AE2Radiation() {}

    public static void register() {
        RadiationEmitterHandler.registerReader((blockEntity, contents) -> {
            if (!(blockEntity instanceof IChestOrDrive host)) return false;
            for (int slot = 0; slot < host.getCellCount(); slot++) {
                // The original live inventory includes changes not yet saved to the cell item.
                var cell = host.getOriginalCellInventory(slot);
                // A newly loaded, unpowered ME chest may not have initialised its handler yet.
                if (cell == null && blockEntity instanceof MEChestBlockEntity chest) {
                    cell = StorageCells.getCellInventory(chest.getCell(), null);
                }
                if (cell == null && blockEntity instanceof DriveBlockEntity drive) {
                    cell = StorageCells.getCellInventory(drive.getInternalInventory().getStackInSlot(slot), null);
                }
                if (cell instanceof BasicCellInventory finite) read(finite, contents);
            }
            if (blockEntity instanceof MEChestBlockEntity chest) {
                // The chest's physical input buffer is separate from its installed cell.
                contents.add(chest.getInternalInventory().getStackInSlot(0));
            }
            return true;
        });
        RadiationUtils.registerItemReader((stack, level, contents) -> {
            if (!(stack.getItem() instanceof IBasicCellItem)) return false;
            var cell = StorageCells.getCellInventory(stack, null);
            if (cell instanceof BasicCellInventory finite) read(finite, contents);
            return true;
        });
    }

    private static void read(BasicCellInventory cell, ItemEmissionAccumulator contents) {
        contents.readStorage(cell, output -> {
            for (var entry : cell.getAvailableStacks()) {
                if (!output.hasRemainingWork()) break;
                if (entry.getKey() instanceof AEItemKey item) {
                    output.add(item.getReadOnlyStack(), entry.getLongValue());
                }
            }
        });
    }
}
