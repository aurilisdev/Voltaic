package voltaic.common.item.subtype;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.util.TriConsumer;

import voltaic.api.ISubtype;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.ItemUtils;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.registers.VoltaicDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public enum SubtypeItemUpgrade implements ISubtype {

    basiccapacity(2, VoltaicTextUtils.tooltip("upgrade.basiccapacity")),
    // box.currentCapacityMultiplier.set(Math.min(box.currentCapacityMultiplier.get()
    // * 1.5, Math.pow(1.5, 3)));
    // box.currentVoltageMultiplier.set(Math.min(box.currentVoltageMultiplier.get()
    // * 2, 2));

    basicspeed(3, VoltaicTextUtils.tooltip("upgrade.basicspeed")),
    // processor.operatingSpeed.set(Math.min(processor.operatingSpeed.get() * 1.5,
    // Math.pow(1.5, 3)));

    advancedcapacity(4, VoltaicTextUtils.tooltip("upgrade.advancedcapacity")),
    // box.currentCapacityMultiplier.set(Math.min(box.currentCapacityMultiplier.get()
    // * 2.25, Math.pow(2.25, 3)));
    // box.currentVoltageMultiplier.set(Math.min(box.currentVoltageMultiplier.get()
    // * 4, 4));

    advancedspeed(3, VoltaicTextUtils.tooltip("upgrade.advancedspeed")),
    // processor.operatingSpeed.set(Math.min(processor.operatingSpeed.get() * 2.25,
    // Math.pow(2.25, 3)));

    // the only way to optimize this one further is to increase the tick delay.
    // Currently, it's set to every 4 ticks
    iteminput((holder, upgrade, procNumber) -> {
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);

        if (!inv.hasInputRoom()) {
            return;
        }

        int tickNumber = upgrade.getOrDefault(VoltaicDataComponentTypes.TIMER, 0);

        if (tickNumber < 4) {
            upgrade.set(VoltaicDataComponentTypes.TIMER, tickNumber + 1);
            return;
        }

        upgrade.set(VoltaicDataComponentTypes.TIMER, 0);
        List<Direction> dirs = NBTUtils.readDirectionList(upgrade);

        if (dirs.size() == 0) {
            return;
        }

        if (upgrade.getOrDefault(VoltaicDataComponentTypes.SMART, false)) {

            int index = 0;
            Direction dir = Direction.DOWN;
            for (int slot : inv.getInputSlotsForProcessor(procNumber)) {
                if (index < dirs.size()) {
                    dir = dirs.get(index);
                }
                inputSmartMode(getBlockEntity(holder, dir), inv, slot, procNumber, dir);
                index++;
            }
        } else {
            for (Direction dir : dirs) {
                inputDefaultMode(getBlockEntity(holder, dir), inv, dir, procNumber);
            }
        }

    }, 1, VoltaicTextUtils.tooltip("upgrade.iteminput")),
    // I can't really optimize this one any more than it is
    itemoutput((holder, upgrade, index) -> {
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        if (!inv.hasItemsInOutput()) {
            return;
        }

        int tickNumber = upgrade.getOrDefault(VoltaicDataComponentTypes.TIMER, 0);

        if (tickNumber < 4) {
            upgrade.set(VoltaicDataComponentTypes.TIMER, tickNumber + 1);
            return;
        }

        upgrade.set(VoltaicDataComponentTypes.TIMER, 0);

        List<Direction> dirs = NBTUtils.readDirectionList(upgrade);

        if (dirs.size() <= 0) {
            return;
        }

        if (upgrade.getOrDefault(VoltaicDataComponentTypes.SMART, false)) {

            int size = 0;
            Direction dir = Direction.DOWN;

            for (int i = 0; i < inv.outputs(); i++) {

                if (size < dirs.size()) {
                    dir = dirs.get(size);
                }

                outputSmartMode(getBlockEntity(holder, dir), inv, i + inv.getOutputStartIndex(), dir);

                size++;
            }

            for (int i = 0; i < inv.biproducts(); i++) {

                if (size < dirs.size()) {
                    dir = dirs.get(size);
                }

                outputSmartMode(getBlockEntity(holder, dir), inv, i + inv.getItemBiproductStartIndex(), dir);

                size++;
            }

        } else {
            for (Direction dir : dirs) {
                outputDefaultMode(getBlockEntity(holder, dir), inv, dir);
            }
        }

    }, 1, VoltaicTextUtils.tooltip("upgrade.itemoutput")),
    improvedsolarcell(1, VoltaicTextUtils.tooltip("upgrade.improvedsolarcell")),
    // generator.setMultiplier(2.25);
    stator(1, VoltaicTextUtils.tooltip("upgrade.stator")),
    // generator.setMultiplier(2.25);
    range(12, VoltaicTextUtils.tooltip("upgrade.range")),
    experience(1, VoltaicTextUtils.tooltip("upgrade.experience")),
    itemvoid(1, VoltaicTextUtils.tooltip("upgrade.itemvoid")),
    silktouch(1, VoltaicTextUtils.tooltip("upgrade.silktouch")),
    fortune(3, VoltaicTextUtils.tooltip("upgrade.fortune")),
    unbreaking(3, VoltaicTextUtils.tooltip("upgrade.unbreaking"));

    public final TriConsumer<GenericTile, ItemStack, Integer> applyUpgrade;
    public final int maxSize;
    // does it have an appliable effect?
    public final boolean isEmpty;

    public final MutableComponent name;

    SubtypeItemUpgrade(TriConsumer<GenericTile, ItemStack, Integer> applyUpgrade, int maxSize, MutableComponent name) {
        this.applyUpgrade = applyUpgrade;
        this.maxSize = maxSize;
        isEmpty = false;
        this.name = name;
    }

    SubtypeItemUpgrade(int maxStackSize, MutableComponent name) {
        applyUpgrade = (holder, upgrade, index) -> {
        };
        maxSize = maxStackSize;
        isEmpty = true;
        this.name = name;
    }

    @Override
    public String tag() {
        return "upgrade" + name();
    }

    @Override
    public String forgeTag() {
        return "upgrade/" + name();
    }

    @Override
    public boolean isItem() {
        return true;
    }

    private static void inputSmartMode(BlockEntity entity, ComponentInventory inv, int slot, int procNumber, Direction dir) {

        if (entity == null) {
            return;
        }

        IItemHandler item = entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, dir.getOpposite());

        if (item == null) {
            return;
        }

        removeItemFromHandler(item, inv, slot);

    }

    private static void inputDefaultMode(BlockEntity entity, ComponentInventory inv, Direction dir, int procNumber) {

        if (entity == null) {
            return;
        }

        IItemHandler item = entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, dir.getOpposite());

        if (item == null) {
            return;
        }

        for (int slot : inv.getInputSlotsForProcessor(procNumber)) {
            removeItemFromHandler(item, inv, slot);
        }

    }

    public static void removeItemFromHandler(IItemHandler handler, ComponentInventory inv, int slot) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ItemStack slotItem = inv.getItem(slot);
                boolean canPlace = inv.canPlaceItem(i, stack);
                if (slotItem.isEmpty() && canPlace) {
                    int taken = stack.getCount() < inv.getMaxStackSize() ? stack.getCount() : inv.getMaxStackSize();
                    ItemStack removed = handler.extractItem(i, taken, false);
                    inv.setItem(slot, removed.copy());
                    inv.setChanged(slot);
                } else if (ItemUtils.testItems(stack.getItem(), slotItem.getItem()) && canPlace) {
                    int cap = slotItem.getMaxStackSize() < inv.getMaxStackSize() ? slotItem.getMaxStackSize() : inv.getMaxStackSize();
                    int canTake = cap - slotItem.getCount();
                    inv.getItem(slot).grow(handler.extractItem(i, canTake, false).getCount());
                    inv.setChanged(slot);
                }

            }

        }
    }

    private static void outputSmartMode(BlockEntity entity, ComponentInventory inv, int index, Direction dir) {
        if (entity == null) {
            return;
        }
        IItemHandler item = entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, dir.getOpposite());

        if (item == null) {
            return;
        }
        addItemToHandler(item, inv, index);
    }

    private static void outputDefaultMode(BlockEntity entity, ComponentInventory inv, Direction dir) {
        if (entity == null) {
            return;
        }
        IItemHandler item = entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, dir.getOpposite());

        if (item == null) {
            return;
        }
        for (int i = 0; i < inv.outputs(); i++) {
            addItemToHandler(item, inv, i + inv.getOutputStartIndex());
        }
        for (int i = 0; i < inv.biproducts(); i++) {
            addItemToHandler(item, inv, i + inv.getItemBiproductStartIndex());
        }
    }

    // returns if the itemstack changed or not
    private static void addItemToHandler(IItemHandler handler, ComponentInventory inv, int index) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack used = handler.insertItem(i, inv.getItem(index), false);
            inv.setItem(index, used);
            inv.setChanged(index);
            if (used.isEmpty()) {
                break;
            }
        }

    }

    @Nullable
    private static BlockEntity getBlockEntity(GenericTile holder, Direction dir) {
        BlockPos pos = holder.getBlockPos().relative(dir);
        BlockState state = holder.getLevel().getBlockState(pos);
        if (state.hasBlockEntity()) {
            return holder.getLevel().getBlockEntity(holder.getBlockPos().relative(dir));
        }
        return null;
    }
}
