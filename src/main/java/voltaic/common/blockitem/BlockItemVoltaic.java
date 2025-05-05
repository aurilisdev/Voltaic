package voltaic.common.blockitem;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockItemVoltaic extends BlockItem {

	private final Supplier<ItemGroup> creativeTab;

	public BlockItemVoltaic(Block block, Properties properties, Supplier<ItemGroup> creativeTab) {
		super(block, properties);
		this.creativeTab = creativeTab;
	}
	
	@Override
	protected boolean allowdedIn(ItemGroup category) {
		return creativeTab != null && category == creativeTab.get();
	}
	
	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if(allowdedIn(group)) {
			this.getBlock().fillItemCategory(group, items);
		}
	}

}
