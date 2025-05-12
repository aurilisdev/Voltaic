package voltaic.common.tab;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import voltaic.registers.VoltaicItems;

public class ItemGroupVoltaic extends ItemGroup {

	public ItemGroupVoltaic(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(VoltaicItems.ITEM_WRENCH.get());
	}
}
