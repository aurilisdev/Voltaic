package voltaic.common.item;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemDescriptable extends ItemVoltaic {

    private final Component[] tooltips;

    public ItemDescriptable(Properties properties, Holder<CreativeModeTab> creativeTab, Component... tooltips) {
	super(properties, creativeTab);
	this.tooltips = tooltips;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
	super.appendHoverText(stack, context, tooltips, flag);
	Collections.addAll(tooltips, this.tooltips);
    }

}
