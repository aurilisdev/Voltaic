package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemVoltaic extends Item {

	private final Supplier<ItemGroup> creativeTab;

	public ItemVoltaic(Properties properties, Supplier<ItemGroup> creativeTab) {
		super(properties);
		this.creativeTab = creativeTab;
	}

	@Override
	protected boolean allowdedIn(ItemGroup category) {
		return creativeTab != null && category == creativeTab.get();
	}

	@Override
	public void fillItemCategory(ItemGroup category, NonNullList<ItemStack> items) {
		if (this.allowdedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
