package voltaic.common.blockitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class BlockItemDescriptable extends BlockItemVoltaic {

    private static final HashMap<Supplier<Block>, ArrayList<IFormattableTextComponent>> DESCRIPTION_MAPPINGS = new HashMap<>();
    private final static HashMap<Block, ArrayList<IFormattableTextComponent>> PROCESSED_DESCRIPTION_MAPPINGS = new HashMap<>();

    private static boolean initialized = false;

    public BlockItemDescriptable(Block block, Properties properties, Supplier<ItemGroup> creativeTab) {
        super(block, properties, creativeTab);
    }

    @Override
    public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (!initialized) {
            BlockItemDescriptable.initialized = true;

            DESCRIPTION_MAPPINGS.forEach((supplier, set) -> {

                PROCESSED_DESCRIPTION_MAPPINGS.put(supplier.get(), set);

            });

        }
        ArrayList<IFormattableTextComponent> gotten = PROCESSED_DESCRIPTION_MAPPINGS.get(getBlock());
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
    public int getItemStackLimit(ItemStack stack) {
    	return stack.hasTag() && stack.getTag().getDouble("joules") > 0 ? 1 : super.getItemStackLimit(stack);
    }

    public static void addDescription(Supplier<Block> block, IFormattableTextComponent description) {

        ArrayList<IFormattableTextComponent> set = DESCRIPTION_MAPPINGS.getOrDefault(block, new ArrayList<>());

        set.add(description);

        DESCRIPTION_MAPPINGS.put(block, set);

    }

}
