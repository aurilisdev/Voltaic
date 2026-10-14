package voltaic.gametest.radiation;



import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;

/** Registered only when AE2 is installed in the test run. */
@PrefixGameTestTemplate(false)
public final class AE2RadiationGameTests {
    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 80)
    public static void offlineDriveAndLiveCellChanges(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, AEBlocks.DRIVE.block());
        var drive = (DriveBlockEntity) helper.getBlockEntity(pos);
        var stack = new ItemStack(AEItems.ITEM_CELL_1K.asItem());
        var cell = StorageCells.getCellInventory(stack, null);
        helper.assertTrue(cell != null, "Cell API must resolve the pinned AE2 version");
        cell.insert(AEItemKey.of(Items.PAPER), 64, Actionable.MODULATE, IActionSource.empty());
        cell.persist();
        drive.getInternalInventory().setItemDirect(0, stack);
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(!drive.isPowered(), "Fixture must be offline");
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 64000,
                    "Physical contents must emit without network power");
            var live = drive.getOriginalCellInventory(0);
            helper.assertTrue(live != null, "Drive must expose its live cell");
            live.insert(AEItemKey.of(Items.PAPER), 32, Actionable.MODULATE, IActionSource.empty());
        });
        helper.runAfterDelay(46, () -> {
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 96000,
                    "Unsaved live changes must be included");
            drive.getInternalInventory().setItemDirect(0, ItemStack.EMPTY);
        });
        helper.runAfterDelay(69, () -> {
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 0,
                    "Removing the cell must clear its drive emission");
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void portableCellUsesTheSameCalculation(GameTestHelper helper) {
        ItemStack stack = new ItemStack(AEItems.ITEM_CELL_1K.asItem());
        var cell = StorageCells.getCellInventory(stack, null);
        cell.insert(AEItemKey.of(Items.PAPER), 1000, Actionable.MODULATE, IActionSource.empty());
        cell.persist();
        var emission = new ItemEmissionAccumulator(helper.getLevel()).add(stack).build(256);
        helper.assertTrue(emission.amount() == 1000000 && emission.range() == 176,
                "Portable cell must read stored quantity, not a filter or network view");
        ItemStack creative = new ItemStack(AEItems.CREATIVE_CELL.asItem());
        helper.assertTrue(!RadiationUtils.calculateItemEmission(helper.getLevel(), java.util.List.of(creative))
                .isRadioactive(), "Creative cells must not invent physical stock");
        helper.succeed();
    }
}
