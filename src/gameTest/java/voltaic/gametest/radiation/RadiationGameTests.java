package voltaic.gametest.radiation;

import voltaic.api.radiation.RadiationEmitterHandler;
import voltaic.api.radiation.RadiationManager;
import voltaic.api.radiation.PortableRadiationHandler;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.LocalInventoryRadiation;


import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import voltaic.api.radiation.util.IRadiationManager.EmitterId;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmissionAccumulator;
import voltaic.registers.VoltaicAttachmentTypes;

@PrefixGameTestTemplate(false)
public final class RadiationGameTests {
    static ItemStack box(int count) {
        ItemStack box = new ItemStack(Items.SHULKER_BOX);
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(Items.PAPER, count))));
        return box;
    }

    static double amount(GameTestHelper helper, BlockPos pos) {
        return helper.getLevel().getData(VoltaicAttachmentTypes.RADIATION_MANAGER)
                .getTemporarySources(helper.getLevel()).stream()
                .filter(source -> source.position.equals(pos)).mapToDouble(source -> source.radiation).sum();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void nestedQuantities(GameTestHelper helper) {
        var contents = new ItemEmissionAccumulator(helper.getLevel()).add(box(64), 1000000L).build(256);
        helper.assertTrue(contents.amount() == 64000000000.0, "Nested quantities must not truncate to int");
        helper.assertTrue(contents.range() == 256, "Extreme quantities must reach the range cap");
        var huge = new ItemEmissionAccumulator(helper.getLevel()).add(Items.PAPER, Long.MAX_VALUE).build(256);
        helper.assertTrue(Double.isFinite(huge.amount()) && huge.amount() > Integer.MAX_VALUE,
                "Long quantities must remain positive and finite");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void nestedWorkAndRepeatedStoresAreBounded(GameTestHelper helper) {
        Object id = new Object();
        var repeated = new ItemEmissionAccumulator(helper.getLevel());
        repeated.readStorage(id, first -> {
            first.add(Items.PAPER, 64);
            first.readStorage(id, cycle -> cycle.add(Items.PAPER, 64));
        });
        helper.assertTrue(repeated.build(256).amount() == 64000, "A backing reference must break cycles");
        var many = new java.util.ArrayList<ItemStack>();
        for (int i = 0; i < 256; i++) many.add(new ItemStack(Items.PAPER));
        ItemStack inner = new ItemStack(Items.SHULKER_BOX);
        inner.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(many));
        var bounded = new ItemEmissionAccumulator(helper.getLevel());
        for (int i = 0; i < 16; i++) bounded.add(inner);
        helper.assertTrue(bounded.build(256).amount() > 0 && bounded.build(256).amount() <= 1024000,
                "Nested calculations must stop after their shared work budget");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void identifiedSourceLifecycle(GameTestHelper helper) {
        var level = helper.getLevel();
        var manager = new RadiationManager();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        var first = EmitterId.entity(UUID.randomUUID());
        var second = EmitterId.entity(UUID.randomUUID());
        for (int i = 0; i < 100; i++) {
            manager.refreshEmitter(first, new SimpleRadiationSource(1000, 1, 5, true, 1, pos, false, false), level);
        }
        manager.refreshEmitter(second, new SimpleRadiationSource(2000, 1, 5, true, 1, pos, false, false), level);
        double total = manager.getTemporarySources(level).stream().filter(s -> s.position.equals(pos))
                .mapToDouble(s -> s.radiation).sum();
        helper.assertTrue(total == 3000, "Refresh must replace; colocated emitters must coexist");
        manager.clearEmitter(first, level);
        helper.assertTrue(manager.getReachOfSource(level, pos) == 5, "Clearing one must retain the other");
        manager.tick(level);
        helper.assertTrue(manager.getTemporarySources(level).stream().anyMatch(s -> s.position.equals(pos)),
                "Expiry one must survive the first application");
        manager.tick(level);
        helper.assertTrue(manager.getTemporarySources(level).stream().noneMatch(s -> s.position.equals(pos)),
                "Expiry one must end after two applications");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 70)
    public static void containerChangesAndDoubleChestHalves(GameTestHelper helper) {
        BlockPos left = new BlockPos(2, 1, 2);
        BlockPos right = new BlockPos(3, 1, 2);
        helper.setBlock(left, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.LEFT));
        helper.setBlock(right, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.RIGHT));
        var first = (Container) helper.getBlockEntity(left);
        var second = (Container) helper.getBlockEntity(right);
        first.setItem(0, box(64));
        second.setItem(0, new ItemStack(Items.PAPER, 32));
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(amount(helper, helper.absolutePos(left)) == 64000, "Read the local half once");
            helper.assertTrue(amount(helper, helper.absolutePos(right)) == 32000, "Read the other half once");
            first.clearContent();
            second.setItem(0, new ItemStack(Items.PAPER, 16));
        });
        helper.runAfterDelay(46, () -> {
            helper.assertTrue(amount(helper, helper.absolutePos(left)) == 0, "Empty contents must clear");
            helper.assertTrue(amount(helper, helper.absolutePos(right)) == 16000, "Extraction must update");
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void independentFrames(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        var first = new ItemFrame(helper.getLevel(), pos, Direction.NORTH);
        var second = new ItemFrame(helper.getLevel(), pos, Direction.NORTH);
        first.setItem(box(32));
        second.setItem(box(64));
        RadiationEmitterHandler.frameTick(new EntityTickEvent.Post(first));
        RadiationEmitterHandler.frameTick(new EntityTickEvent.Post(second));
        helper.assertTrue(amount(helper, first.blockPosition()) == 96000, "Two frame UUIDs must not overwrite");
        first.setItem(ItemStack.EMPTY);
        RadiationEmitterHandler.frameTick(new EntityTickEvent.Post(first));
        helper.assertTrue(amount(helper, second.blockPosition()) == 64000, "Clearing one frame must retain the other");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty")
    public static void cachedCarrierStillReceivesEveryDose(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getInventory().setItem(0, box(64));
        class Recipient implements IRadiationRecipient {
            double dose;
            public void recieveRadiation(LivingEntity entity, double amount, double strength) { dose += amount; }
            public RadioactiveObject getRecievedRadiation(LivingEntity entity) { return RadioactiveObject.ZERO; }
            public void tick(LivingEntity entity) {}
        }
        var recipient = new Recipient();
        for (int tick = 0; tick < 20; tick++) PortableRadiationHandler.exposeCarrier(player, recipient);
        helper.assertTrue(recipient.dose == 1280000, "Caching must not divide carried dose by twenty");
        player.getInventory().clearContent();
        PortableRadiationHandler.exposeCarrier(player, recipient);
        helper.assertTrue(recipient.dose == 1280000, "Removal must invalidate the carried cache immediately");
        helper.succeed();
    }

    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 10)
    public static void droppedContentsEmitAndCleanUp(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        var item = new ItemEntity(helper.getLevel(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, box(64));
        item.setNoGravity(true);
        helper.getLevel().addFreshEntity(item);
        helper.runAfterDelay(3, () -> {
            helper.assertTrue(amount(helper, item.blockPosition()) == 64000, "Dropped box must emit once");
            BlockPos oldPos = item.blockPosition();
            item.discard();
            helper.assertTrue(amount(helper, oldPos) == 0, "Removal must clear the identified drop");
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "voltaic", template = "empty", timeoutTicks = 50)
    public static void capabilityViewRequiresLocalOptIn(GameTestHelper helper) {
        BlockPos local = new BlockPos(2, 1, 2);
        BlockPos remote = new BlockPos(4, 1, 2);
        helper.setBlock(local.below(), Blocks.STONE);
        helper.setBlock(remote.below(), Blocks.STONE);
        helper.setBlock(local, Blocks.OAK_SIGN);
        helper.setBlock(remote, Blocks.SPRUCE_SIGN);
        helper.assertTrue(helper.getBlockEntity(local).getBlockState().is(LocalInventoryRadiation.LOCAL_INVENTORIES),
                "The test data pack must opt the local fixture into capability reading");
        for (BlockPos pos : List.of(local, remote)) {
            var blockEntity = helper.getBlockEntity(pos);
            var slots = helper.getLevel().getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                    helper.absolutePos(pos), null);
            ((net.neoforged.neoforge.items.ItemStackHandler) slots).setStackInSlot(0, new ItemStack(Items.PAPER, 64));
        }
        helper.runAfterDelay(23, () -> {
            helper.assertTrue(amount(helper, helper.absolutePos(local)) == 64000,
                    "All faces expose the same local slots; expected 64000, got " + amount(helper, helper.absolutePos(local)));
            helper.assertTrue(amount(helper, helper.absolutePos(remote)) == 0,
                    "An unapproved capability must not be assumed to be local stock");
            helper.succeed();
        });
    }
}
