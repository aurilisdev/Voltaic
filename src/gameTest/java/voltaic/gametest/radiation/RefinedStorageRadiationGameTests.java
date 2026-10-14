package voltaic.gametest.radiation;



import java.util.List;
import java.util.UUID;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.StorageBlockEntity;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.storage.AbstractDiskContainerBlockEntity;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import voltaic.prefab.utilities.RadiationUtils;

/** Registered only when RS 2 is installed in the test run. */
@PrefixGameTestTemplate(false)
public final class RefinedStorageRadiationGameTests {
    private static ItemStack disk(GameTestHelper helper, long count) {
        var repository = RefinedStorageApi.INSTANCE.getStorageRepository(helper.getLevel());
        var storage = StorageTypes.ITEM.create(65536L, repository::markAsChanged);
        storage.insert(new ItemResource(Items.PAPER), count, Action.EXECUTE, Actor.EMPTY);
        UUID id = UUID.randomUUID();
        repository.set(id, storage);
        var disk = new ItemStack(com.refinedmods.refinedstorage.common.content.Items.INSTANCE
                .getItemStorageDisk(ItemStorageVariant.SIXTY_FOUR_K));
        disk.set(DataComponents.INSTANCE.getStorageReference(), id);
        return disk;
    }

    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 70)
    public static void offlineDiskDriveAndMovedDisk(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getDiskDrive());
        var drive = (AbstractDiskContainerBlockEntity<?>) helper.getBlockEntity(pos);
        ItemStack disk = disk(helper, 1000);
        drive.getDiskInventory().setItem(0, disk);
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 1000000,
                    "Disk must emit from an unpowered drive");
            drive.getDiskInventory().setItem(0, ItemStack.EMPTY);
        });
        helper.runAfterDelay(46, () -> {
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 0,
                    "Drive must stop emitting after disk removal");
            var emission = RadiationUtils.calculateItemEmission(helper.getLevel(), List.of(disk));
            helper.assertTrue(emission.amount() == 1000000, "Portable disk must retain its radiation");
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void repeatedBackingReferencesAreCountedOnce(GameTestHelper helper) {
        ItemStack disk = disk(helper, 64);
        var emission = RadiationUtils.calculateItemEmission(helper.getLevel(), List.of(disk, disk.copy()));
        helper.assertTrue(emission.amount() == 64000, "Repeated disk references must not duplicate stored stock");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 50)
    public static void offlineStorageBlockHasItsOwnEmission(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE
                .getItemStorageBlock(ItemStorageVariant.ONE_K));
        var block = (StorageBlockEntity) helper.getBlockEntity(pos);
        var repository = RefinedStorageApi.INSTANCE.getStorageRepository(helper.getLevel());
        var storage = repository.get(block.getStorageId()).orElseThrow();
        storage.insert(new ItemResource(Items.PAPER), 1000, Action.EXECUTE, Actor.EMPTY);
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(RadiationGameTests.amount(helper, helper.absolutePos(pos)) == 1000000,
                    "Storage block must read its physical repository contents without power");
            helper.succeed();
        });
    }
}
