package voltaic.common.item.subtype;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.api.ISubtype;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.ItemUtils;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.registers.VoltaicDataComponentTypes;

public enum SubtypeItemUpgrade implements ISubtype {

    basiccapacity(2, VoltaicTextUtils.tooltip("upgrade.basiccapacity"), "electrodynamics"),
    basicspeed(3, VoltaicTextUtils.tooltip("upgrade.basicspeed"), "electrodynamics", "assemblyline", "blastcraft"),
    advancedcapacity(4, VoltaicTextUtils.tooltip("upgrade.advancedcapacity"), "electrodynamics"),
    advancedspeed(3, VoltaicTextUtils.tooltip("upgrade.advancedspeed"), "electrodynamics", "assemblyline",
	    "blastcraft"),

    iteminput((holder, upgrade, processorNumber) -> {
	ComponentInventory inventory = holder.requireComponent(IComponentType.Inventory);
	if (!inventory.hasInputRoom())
	    return;

	int timer = upgrade.getOrDefault(VoltaicDataComponentTypes.TIMER, 0);
	if (timer < 4) {
	    upgrade.set(VoltaicDataComponentTypes.TIMER, timer + 1);
	    return;
	}

	upgrade.set(VoltaicDataComponentTypes.TIMER, 0);
	List<Direction> directions = NBTUtils.readDirectionList(upgrade);

	if (directions.isEmpty())
	    return;

	if (upgrade.getOrDefault(VoltaicDataComponentTypes.SMART, false)) {
	    int directionIndex = 0;

	    for (int slot : inventory.getInputSlotsForProcessor(processorNumber)) {
		Direction direction = getDirection(directions, directionIndex++);
		inputSmartMode(getBlockEntity(holder, direction), inventory, slot, processorNumber, direction);
	    }
	} else {
	    for (Direction direction : directions) {
		inputDefaultMode(getBlockEntity(holder, direction), inventory, direction, processorNumber);
	    }
	}
    }, 1, VoltaicTextUtils.tooltip("upgrade.iteminput"), "electrodynamics", "assemblyline", "blastcraft"),

    itemoutput((holder, upgrade, processorNumber) -> {
	ComponentInventory inventory = holder.requireComponent(IComponentType.Inventory);
	if (!inventory.hasInputRoom())
	    return;

	int timer = upgrade.getOrDefault(VoltaicDataComponentTypes.TIMER, 0);
	if (timer < 4) {
	    upgrade.set(VoltaicDataComponentTypes.TIMER, timer + 1);
	    return;
	}

	upgrade.set(VoltaicDataComponentTypes.TIMER, 0);
	List<Direction> directions = NBTUtils.readDirectionList(upgrade);

	if (directions.isEmpty())
	    return;

	if (upgrade.getOrDefault(VoltaicDataComponentTypes.SMART, false)) {
	    int directionIndex = 0;

	    for (int i = 0; i < inventory.outputs(); i++) {
		Direction direction = getDirection(directions, directionIndex++);
		outputSmartMode(getBlockEntity(holder, direction), inventory, i + inventory.getOutputStartIndex(),
			direction);
	    }

	    for (int i = 0; i < inventory.biproducts(); i++) {
		Direction direction = getDirection(directions, directionIndex++);
		outputSmartMode(getBlockEntity(holder, direction), inventory,
			i + inventory.getItemBiproductStartIndex(), direction);
	    }
	} else {
	    for (Direction direction : directions) {
		outputDefaultMode(getBlockEntity(holder, direction), inventory, direction, processorNumber);
	    }
	}
    }, 1, VoltaicTextUtils.tooltip("upgrade.itemoutput"), "electrodynamics", "assemblyline", "blastcraft"),

    improvedsolarcell(1, VoltaicTextUtils.tooltip("upgrade.improvedsolarcell"), "electrodynamics"),
    stator(1, VoltaicTextUtils.tooltip("upgrade.stator"), "electrodynamics"),
    range(12, VoltaicTextUtils.tooltip("upgrade.range"), "electrodynamics", "assemblyline", "ballistix"),
    experience(1, VoltaicTextUtils.tooltip("upgrade.experience"), "electrodynamics", "assemblyline", "blastcraft"),
    itemvoid(1, VoltaicTextUtils.tooltip("upgrade.itemvoid"), "electrodynamics"),
    silktouch(1, VoltaicTextUtils.tooltip("upgrade.silktouch"), "electrodynamics"),
    fortune(3, VoltaicTextUtils.tooltip("upgrade.fortune"), "electrodynamics"),
    unbreaking(3, VoltaicTextUtils.tooltip("upgrade.unbreaking"), "electrodynamics");

    private static Direction getDirection(List<Direction> directions, int index) {
	return index < directions.size() ? directions.get(index) : Direction.DOWN;
    }

    public final TriConsumer<GenericTile, ItemStack, Integer> applyUpgrade;
    public final int maxSize;
    // does it have an appliable effect?
    public final boolean isEmpty;

    public final MutableComponent name;
    public final String[] modIds;

    SubtypeItemUpgrade(TriConsumer<GenericTile, ItemStack, Integer> applyUpgrade, int maxSize, MutableComponent name,
	    String... modIds) {
	this.applyUpgrade = applyUpgrade;
	this.maxSize = maxSize;
	isEmpty = false;
	this.name = name;
	this.modIds = modIds;
    }

    SubtypeItemUpgrade(int maxStackSize, MutableComponent name, String... modIds) {
	applyUpgrade = (holder, upgrade, index) -> {};
	maxSize = maxStackSize;
	isEmpty = true;
	this.name = name;
	this.modIds = modIds;
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

    private static void inputSmartMode(@Nullable BlockEntity entity, ComponentInventory inv, int slot, int procNumber,
	    Direction dir) {
	if (entity == null)
	    return;
	Level level = entity.getLevel();
	if (level == null)
	    return;
	IItemHandler item = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(),
		entity.getBlockState(), entity, dir.getOpposite());

	if (item == null)
	    return;

	removeItemFromHandler(item, inv, slot);

    }

    private static void inputDefaultMode(@Nullable BlockEntity entity, ComponentInventory inv, Direction dir,
	    int procNumber) {
	if (entity == null)
	    return;

	Level level = entity.getLevel();
	if (level == null)
	    return;

	IItemHandler item = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(),
		entity.getBlockState(), entity, dir.getOpposite());
	if (item == null)
	    return;

	for (int slot : inv.getInputSlotsForProcessor(procNumber)) {
	    removeItemFromHandler(item, inv, slot);
	}
    }

    public static void removeItemFromHandler(IItemHandler handler, ComponentInventory inv, int slot) {
	for (int i = 0; i < handler.getSlots(); i++) {
	    ItemStack stack = handler.getStackInSlot(i);
	    if (!stack.isEmpty()) {
		ItemStack slotItem = inv.getItem(slot);
		boolean canPlace = inv.canPlaceItem(slot, stack);
		if (!canPlace) {
		    continue;
		}
		if (slotItem.isEmpty()) {
		    int taken = stack.getCount() < inv.getMaxStackSize() ? stack.getCount() : inv.getMaxStackSize();
		    ItemStack removed = handler.extractItem(i, taken, false);
		    inv.setItem(slot, removed.copy());
		    inv.setChanged(slot);
		} else if (ItemUtils.testItems(stack.getItem(), slotItem.getItem())) {
		    int cap = slotItem.getMaxStackSize() < inv.getMaxStackSize() ? slotItem.getMaxStackSize()
			    : inv.getMaxStackSize();
		    int canTake = cap - slotItem.getCount();
		    inv.getItem(slot).grow(handler.extractItem(i, canTake, false).getCount());
		    inv.setChanged(slot);
		}
	    }
	}
    }

    private static void outputSmartMode(@Nullable BlockEntity entity, ComponentInventory inv, int index,
	    Direction dir) {
	if (entity == null)
	    return;
	Level level = entity.getLevel();
	if (level == null)
	    return;
	IItemHandler item = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(),
		entity.getBlockState(), entity, dir.getOpposite());

	if (item == null)
	    return;
	addItemToHandler(item, inv, index);
    }

    private static void outputDefaultMode(@Nullable BlockEntity entity, ComponentInventory inv, Direction dir,
	    int procNumber) {
	if (entity == null)
	    return;
	Level level = entity.getLevel();
	if (level == null)
	    return;
	IItemHandler item = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(),
		entity.getBlockState(), entity, dir.getOpposite());

	if (item == null)
	    return;
	for (int i = 0; i < inv.outputs(); i++) {
	    addItemToHandler(item, inv, i + inv.getOutputStartIndex());
	}
	for (int i = 0; i < inv.biproducts(); i++) {
	    addItemToHandler(item, inv, i + inv.getItemBiproductStartIndex());
	}
    }

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
	Level level = holder.getLevel();
	if (level == null)
	    return null;

	BlockState state = level.getBlockState(pos);
	if (!state.hasBlockEntity())
	    return null;

	return level.getBlockEntity(holder.getBlockPos().relative(dir));

    }
}
