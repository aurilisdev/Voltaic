package voltaic.common.blockitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class BlockItemDescriptable extends BlockItemVoltaic {

    private static final HashMap<Holder<Block>, ArrayList<MutableComponent>> DESCRIPTION_MAPPINGS = new HashMap<>();
    private final static HashMap<Block, ArrayList<MutableComponent>> PROCESSED_DESCRIPTION_MAPPINGS = new HashMap<>();

    private static boolean initialized = false;

    public BlockItemDescriptable(Block block, Properties properties, @Nullable Holder<CreativeModeTab> creativeTab) {
	super(block, properties, creativeTab);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
	super.appendHoverText(stack, context, tooltip, flagIn);

	if (!initialized) {
	    BlockItemDescriptable.initialized = true;
	    DESCRIPTION_MAPPINGS.forEach((supplier, set) -> PROCESSED_DESCRIPTION_MAPPINGS.put(supplier.value(), set));
	}

	ArrayList<MutableComponent> descriptions = PROCESSED_DESCRIPTION_MAPPINGS.get(getBlock());
	if (descriptions != null) {
	    tooltip.addAll(descriptions);
	}

	CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
	if (blockEntityData != null) {
	    double joules = blockEntityData.copyTag().getDouble("joules");
	    if (joules > 0) {
		tooltip.add(VoltaicTextUtils.gui("machine.stored",
			ChatFormatter.getChatDisplayShort(joules, DisplayUnits.JOULES)));
	    }
	}
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
	CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);

	if (blockEntityData != null && blockEntityData.copyTag().getDouble("joules") > 0)
	    return 1;

	return super.getMaxStackSize(stack);
    }

    public static void addDescription(Holder<Block> block, MutableComponent description) {

	ArrayList<MutableComponent> set = DESCRIPTION_MAPPINGS.getOrDefault(block, new ArrayList<>());

	set.add(description);

	DESCRIPTION_MAPPINGS.put(block, set);

    }

}
