package voltaic.prefab.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.item.IItemElectric;
import voltaic.common.item.ItemVoltaic;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class ItemElectric extends ItemVoltaic implements IItemElectric {

	private final ElectricItemProperties properties;

	public ItemElectric(ElectricItemProperties properties, Supplier<ItemGroup> creativeTab) {
		super(properties, creativeTab);
		this.properties = properties;
	}
	
	@Override
	public void fillItemCategory(ItemGroup category, NonNullList<ItemStack> items) {
		if(allowdedIn(category)) {
			ItemStack empty = new ItemStack(this);
			//IItemElectric.setEnergyStored(empty, 0);
			items.add(empty);
			ItemStack charged = new ItemStack(this);
			IItemElectric.setEnergyStored(charged, getMaximumCapacity(charged));
			items.add(charged);
		}
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return Math.round(13.0f * getJoulesStored(stack) / getMaximumCapacity(stack));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getJoulesStored(stack) < getMaximumCapacity(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(VoltaicTextUtils.tooltip("item.electric.info", VoltaicTextUtils.ratio(ChatFormatter.getChatDisplayShort(getJoulesStored(stack), DisplayUnits.JOULES), ChatFormatter.getChatDisplayShort(getMaximumCapacity(stack), DisplayUnits.JOULES)).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
		tooltip.add(VoltaicTextUtils.tooltip("item.electric.voltage", ChatFormatter.getChatDisplayShort(properties.receive.getVoltage(), DisplayUnits.VOLTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ElectricItemProperties getElectricProperties() {
		return properties;
	}

}
