package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemVoltaic extends Item {

	private final Supplier<CreativeModeTab> creativeTab;

	public ItemVoltaic(Properties properties, Supplier<CreativeModeTab> creativeTab) {
		super(properties);
		this.creativeTab = creativeTab;
	}

	@Override
	protected boolean allowedIn(CreativeModeTab category) {
		return creativeTab != null && category == creativeTab.get();
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (this.allowedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
