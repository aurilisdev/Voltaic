package voltaic.common.blockitem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import voltaic.api.creativetab.CreativeTabSupplier;

public class BlockItemVoltaic extends BlockItem implements CreativeTabSupplier {

    @Nullable
    private final Holder<CreativeModeTab> creativeTab;

    public BlockItemVoltaic(Block block, Properties properties, @Nullable Holder<CreativeModeTab> creativeTab) {
	super(block, properties);
	this.creativeTab = creativeTab;
    }

    @Override
    public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {
	items.add(new ItemStack(this));
    }

    @Override
    public boolean isAllowedInCreativeTab(CreativeModeTab tab) {
	Holder<CreativeModeTab> creativeTab = this.creativeTab;
	if (creativeTab == null)
	    return false;

	return creativeTab.value() == tab;
    }

    @Override
    public boolean hasCreativeTab() {
	return creativeTab != null;
    }

}
