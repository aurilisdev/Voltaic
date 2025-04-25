package voltaic.common.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public class ItemDescriptable extends ItemVoltaic {

	private Component[] tooltips;

	public ItemDescriptable(Properties properties, RegistryObject<CreativeModeTab> creativeTab, Component... tooltips) {
		super(properties, creativeTab);
		this.tooltips = tooltips;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level context, List<Component> tooltips, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltips, flag);
		if (tooltips != null) {
			for (Component tooltip : this.tooltips) {
				tooltips.add(tooltip);
			}
		}
	}

}
