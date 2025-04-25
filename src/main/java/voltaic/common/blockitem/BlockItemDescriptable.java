package voltaic.common.blockitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlockItemDescriptable extends BlockItemVoltaic {

    private static final HashMap<Holder<Block>, ArrayList<MutableComponent>> DESCRIPTION_MAPPINGS = new HashMap<>();
    private final static HashMap<Block, ArrayList<MutableComponent>> PROCESSED_DESCRIPTION_MAPPINGS = new HashMap<>();

    private static boolean initialized = false;

    public BlockItemDescriptable(Block block, Properties properties, Holder<CreativeModeTab> creativeTab) {
        super(block, properties, creativeTab);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (!initialized) {
            BlockItemDescriptable.initialized = true;

            DESCRIPTION_MAPPINGS.forEach((supplier, set) -> {

                PROCESSED_DESCRIPTION_MAPPINGS.put(supplier.value(), set);

            });

        }
        ArrayList<MutableComponent> gotten = PROCESSED_DESCRIPTION_MAPPINGS.get(getBlock());
        if (gotten != null) {
            tooltip.addAll(gotten);
        }

        if (stack.hasTag() && stack.getTag().contains("joules")) {
            double joules = stack.getTag().getDouble("joules");
            if (joules > 0) {
                tooltip.add(VoltaicTextUtils.gui("machine.stored", ChatFormatter.getChatDisplayShort(joules, DisplayUnits.JOULES)));
            }
        }
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
    	return stack.hasTag() && stack.getTag().getDouble("joules") > 0 ? 1 : super.getMaxStackSize(stack);
    }

    public static void addDescription(Holder<Block> block, MutableComponent description) {

        ArrayList<MutableComponent> set = DESCRIPTION_MAPPINGS.getOrDefault(block, new ArrayList<>());

        set.add(description);

        DESCRIPTION_MAPPINGS.put(block, set);

    }

}
