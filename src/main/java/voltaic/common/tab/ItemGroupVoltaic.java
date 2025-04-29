package voltaic.common.tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import voltaic.registers.VoltaicItems;

public class ItemGroupVoltaic extends CreativeModeTab {

	public ItemGroupVoltaic(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(VoltaicItems.ITEM_WRENCH.get());
	}
}
