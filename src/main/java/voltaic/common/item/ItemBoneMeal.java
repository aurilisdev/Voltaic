package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBoneMeal extends BoneMealItem {

	private final Supplier<ItemGroup> creativeTab;

	public ItemBoneMeal(Properties properties, Supplier<ItemGroup> creativeTab) {
		super(properties);
		this.creativeTab = creativeTab;
	}

	@Override
	protected boolean allowdedIn(ItemGroup category) {
		return creativeTab != null && creativeTab.get() == category;
	}

	@Override
	public void fillItemCategory(ItemGroup category, NonNullList<ItemStack> items) {
		if (this.allowdedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
