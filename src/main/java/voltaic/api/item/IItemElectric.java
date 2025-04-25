package voltaic.api.item;

import java.util.List;

import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface IItemElectric {

    public static final String JOULES_STORED = "joules";
    public static final String JOULES_CAPACITY = "maximumcapacity";
    public static final String VOLTAGE = "voltage";
    public static final String RECEIVE_LIMIT = "receivelimit";
    public static final String EXTRACT_LIMIT = "extractlimit";
    public static final String CURRENT_BATTERY = "currentbattery";

    default double getJoulesStored(ItemStack stack) {
    	return stack.getOrCreateTag().getDouble(JOULES_STORED);
    }

    public static void setEnergyStored(ItemStack stack, double amount) {
    	stack.getOrCreateTag().putDouble(JOULES_STORED, amount);
    }

    default double getMaximumCapacity(ItemStack item) {
    	CompoundTag tag = item.getOrCreateTag();
		if (!tag.contains(JOULES_CAPACITY)) {
			setMaximumCapacity(item, getElectricProperties().capacity);
		}
		return tag.getDouble(JOULES_CAPACITY);
    }

    static void setMaximumCapacity(ItemStack item, double amt) {
    	item.getOrCreateTag().putDouble(JOULES_CAPACITY, amt);
    }

    default double getReceiveLimit(ItemStack item) {
    	CompoundTag tag = item.getOrCreateTag();
		if (!tag.contains(RECEIVE_LIMIT)) {
			setReceiveLimit(item, getElectricProperties().receive.getJoules());
		}
		return tag.getDouble(RECEIVE_LIMIT);
    }

    static void setReceiveLimit(ItemStack stack, double amount) {
    	stack.getOrCreateTag().putDouble(RECEIVE_LIMIT, amount);
    }

    default double getExtractLimit(ItemStack item) {
    	CompoundTag tag = item.getOrCreateTag();
		if (!tag.contains(EXTRACT_LIMIT)) {
			setExtractLimit(item, getElectricProperties().extract.getJoules());
		}
		return tag.getDouble(EXTRACT_LIMIT);
    }

    static void setExtractLimit(ItemStack stack, double amount) {
    	stack.getOrCreateTag().putDouble(EXTRACT_LIMIT, amount);
    }

    default boolean isEnergyStorageOnly() {
        return getElectricProperties().isEnergyStorageOnly;
    }

    default boolean cannotHaveBatterySwapped() {
        return getElectricProperties().cannotHaveBatterySwapped;
    }

    default TransferPack extractPower(ItemStack stack, double amount, boolean debug) {
        if (getJoulesStored(stack) <= 0) {
            return TransferPack.EMPTY;
        }
        double current = getJoulesStored(stack);
        double extracted = Math.min(current, Math.min(getExtractLimit(stack), amount));
        if (!debug) {
            setEnergyStored(stack, current - extracted);
        }
        return TransferPack.joulesVoltage(extracted, getElectricProperties().extract.getVoltage());
    }

    default TransferPack receivePower(ItemStack stack, TransferPack amount, boolean debug) {

        double current = getJoulesStored(stack);
        double received = Math.min(amount.getJoules(), getMaximumCapacity(stack) - current);
        if (!debug) {
            if (amount.getVoltage() == getElectricProperties().receive.getVoltage() || amount.getVoltage() == -1) {
                setEnergyStored(stack, current + received);
            }
            if (amount.getVoltage() > getElectricProperties().receive.getVoltage()) {
                overVoltage(amount);
                return TransferPack.EMPTY;
            }
        }
        return TransferPack.joulesVoltage(received, amount.getVoltage());
    }

    default void overVoltage(TransferPack attempt) {
    }

    /**
     * finds the first battery of matching voltage for this item and swaps out the battery in the item with a new one
     *
     * @param tool
     * @param player
     */
    default void swapBatteryPackFirstItem(ItemStack tool, Player player) {
        IItemElectric electricItem = (IItemElectric) tool.getItem();

        if (electricItem.isEnergyStorageOnly() || electricItem.cannotHaveBatterySwapped()) {
            return;
        }

        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack playerItem = inv.getItem(i).copy();

            // Only allow batteries that have the same receive and extract voltage
            if (!playerItem.isEmpty() && playerItem.getItem() instanceof IItemElectric electric && electric.isEnergyStorageOnly() && electric.getJoulesStored(playerItem) > 0 && electric.getElectricProperties().extract.getVoltage() == electricItem.getElectricProperties().extract.getVoltage() && electric.getElectricProperties().receive.getVoltage() == electricItem.getElectricProperties().receive.getVoltage()) {
                ItemStack currBattery = electricItem.getCurrentBattery(tool);
                if (currBattery.isEmpty()) {
                    return;
                }
                double joulesStored = electricItem.getJoulesStored(tool);
                electricItem.setCurrentBattery(tool, playerItem);
                IItemElectric.setEnergyStored(tool, electric.getJoulesStored(playerItem));
                IItemElectric.setEnergyStored(currBattery, joulesStored);
                inv.setItem(i, ItemStack.EMPTY);
                for (int j = 0; j < inv.items.size(); j++) {
                    ItemStack item = inv.getItem(j);
                    if (item.isEmpty()) {
                        inv.setItem(j, currBattery);
                        player.level().playSound(null, player.getOnPos(), VoltaicSounds.SOUND_BATTERY_SWAP.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                        return;
                    }
                }
                return;
            }

        }

    }

    static boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

        if (action == null || action == ClickAction.PRIMARY || other.isEmpty()) {
            return false;
        }

        if (((IItemElectric) stack.getItem()).cannotHaveBatterySwapped()) {
            return false;
        }

        if (!(other.getItem() instanceof IItemElectric) || (other.getItem() instanceof IItemElectric electric && !electric.isEnergyStorageOnly())) {
            return false;
        }

        IItemElectric thisElectric = (IItemElectric) stack.getItem();
        IItemElectric otherElectric = (IItemElectric) other.getItem();

        if (otherElectric.getJoulesStored(other) == 0 || otherElectric.getElectricProperties().receive.getVoltage() != thisElectric.getElectricProperties().receive.getVoltage() || thisElectric.getElectricProperties().extract.getVoltage() != otherElectric.getElectricProperties().extract.getVoltage()) {
            return false;
        }

        ItemStack currBattery = thisElectric.getCurrentBattery(stack);

        double joulesStored = thisElectric.getJoulesStored(stack);

        IItemElectric.setEnergyStored(currBattery, joulesStored);

        access.set(currBattery);

        IItemElectric.setEnergyStored(stack, otherElectric.getJoulesStored(other));

        thisElectric.setCurrentBattery(stack, other);

        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), VoltaicSounds.SOUND_BATTERY_SWAP.get(), SoundSource.PLAYERS, 0.25F, 1.0F, false);

        return true;

    }

    default ItemStack getCurrentBattery(ItemStack tool) {

    	if (((IItemElectric) tool.getItem()).getDefaultStorageBattery() == Items.AIR) {
			return ItemStack.EMPTY;
		}
		CompoundTag tag = tool.getOrCreateTag();
		if (!tag.contains(CURRENT_BATTERY)) {
			setCurrentBattery(tool, new ItemStack(getDefaultStorageBattery()));
		}
		return ItemStack.of(tag.getCompound(CURRENT_BATTERY));
    }

    // It is assumed you are setting a battery with this method
    default void setCurrentBattery(ItemStack tool, ItemStack battery) {

    	CompoundTag tag = tool.getOrCreateTag();
		tag.remove(CURRENT_BATTERY);
		if (battery.getItem() instanceof IItemElectric electric) {
			// If the battery is an electric item, use its capacity
			IItemElectric.setMaximumCapacity(tool, electric.getMaximumCapacity(battery));
		} else {
			// Failsafe, use the capacity of the current item instead (previous behaviour)
			IItemElectric.setMaximumCapacity(tool, getMaximumCapacity(tool));
		}
		tag.put(CURRENT_BATTERY, battery.save(new CompoundTag()));
    }

    ElectricItemProperties getElectricProperties();

    Item getDefaultStorageBattery();

    static void addBatteryTooltip(ItemStack stack, Level context, List<Component> tooltip) {
        tooltip.add(VoltaicTextUtils.tooltip("currbattery", ((IItemElectric) stack.getItem()).getCurrentBattery(stack).getDisplayName().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
    }


}