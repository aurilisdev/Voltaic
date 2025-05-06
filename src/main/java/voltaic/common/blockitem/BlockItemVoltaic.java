package voltaic.common.blockitem;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockItemVoltaic extends BlockItem {

	private final Supplier<CreativeModeTab> creativeTab;

	public BlockItemVoltaic(Block block, Properties properties, Supplier<CreativeModeTab> creativeTab) {
		super(block, properties);
		this.creativeTab = creativeTab;
	}
	
	@Override
	protected boolean allowedIn(CreativeModeTab category) {
		return creativeTab != null && (category == creativeTab.get() || category == CreativeModeTab.TAB_SEARCH);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if(allowedIn(group)) {
			this.getBlock().fillItemCategory(group, items);
		}
	}

}
