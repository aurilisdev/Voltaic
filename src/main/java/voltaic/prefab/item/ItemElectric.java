package voltaic.prefab.item;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.item.IItemElectric;
import voltaic.common.item.ItemVoltaic;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemElectric extends ItemVoltaic implements IItemElectric {

	private final ElectricItemProperties properties;
	private final Function<Item, Item> getBatteryItem;

	public ItemElectric(ElectricItemProperties properties, Supplier<CreativeModeTab> creativeTab, Function<Item, Item> getBatteryItem) {
		super(properties, creativeTab);
		this.properties = properties;
		this.getBatteryItem = getBatteryItem;
	}

	@Override
	public void addCreativeModeItems(CreativeModeTab group, List<ItemStack> items) {

		ItemStack empty = new ItemStack(this);
		//IItemElectric.setEnergyStored(empty, 0);
		items.add(empty);
		ItemStack charged = new ItemStack(this);
		IItemElectric.setEnergyStored(charged, getMaximumCapacity(charged));
		items.add(charged);

	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return (int) Math.round(13.0f * getJoulesStored(stack) / getMaximumCapacity(stack));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getJoulesStored(stack) < getMaximumCapacity(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(VoltaicTextUtils.tooltip("item.electric.info", VoltaicTextUtils.ratio(ChatFormatter.getChatDisplayShort(getJoulesStored(stack), DisplayUnits.JOULES), ChatFormatter.getChatDisplayShort(getMaximumCapacity(stack), DisplayUnits.JOULES)).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(VoltaicTextUtils.tooltip("item.electric.voltage", ChatFormatter.getChatDisplayShort(properties.receive.getVoltage(), DisplayUnits.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		if (getDefaultStorageBattery() != Items.AIR) {
			IItemElectric.addBatteryTooltip(stack, context, tooltip);
		}
	}

	@Override
	public ElectricItemProperties getElectricProperties() {
		return properties;
	}

	@Override
	public Item getDefaultStorageBattery() {
		return getBatteryItem.apply(this);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

		if (!IItemElectric.overrideOtherStackedOnMe(stack, other, slot, action, player, access)) {
			return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
		}

		return true;

	}

}
