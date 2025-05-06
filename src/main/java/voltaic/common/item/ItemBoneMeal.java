package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemBoneMeal extends BoneMealItem {

	private final Supplier<CreativeModeTab> creativeTab;

	public ItemBoneMeal(Properties properties, Supplier<CreativeModeTab> creativeTab) {
		super(properties);
		this.creativeTab = creativeTab;
	}

	@Override
	protected boolean allowedIn(CreativeModeTab category) {
		return creativeTab != null && (category == creativeTab.get() || category == CreativeModeTab.TAB_SEARCH);
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (this.allowedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
