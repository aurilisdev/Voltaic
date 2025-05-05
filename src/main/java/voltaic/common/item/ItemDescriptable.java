package voltaic.common.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemDescriptable extends ItemVoltaic {

	private ITextComponent[] tooltips;

	public ItemDescriptable(Properties properties, Supplier<ItemGroup> creativeTab, ITextComponent... tooltips) {
		super(properties, creativeTab);
		this.tooltips = tooltips;
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltips, ITooltipFlag flag) {
		super.appendHoverText(stack, context, tooltips, flag);
		if (tooltips != null) {
			for (ITextComponent tooltip : this.tooltips) {
				tooltips.add(tooltip);
			}
		}
	}

}
