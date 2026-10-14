package voltaic.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import voltaic.api.creativetab.CreativeTabSupplier;

public class ItemVoltaic extends Item implements CreativeTabSupplier {

    private final @Nullable Holder<CreativeModeTab> creativeTab;

    public ItemVoltaic(Properties properties, @Nullable Holder<CreativeModeTab> creativeTab) {
	super(properties);
	this.creativeTab = creativeTab;
    }

    @Override
    public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {
	items.add(new ItemStack(this));
    }

    @Override
    public boolean isAllowedInCreativeTab(CreativeModeTab tab) {
	return creativeTab != null && creativeTab.value() == tab;
    }

    @Override
    public boolean hasCreativeTab() {
	return true;
    }

}
