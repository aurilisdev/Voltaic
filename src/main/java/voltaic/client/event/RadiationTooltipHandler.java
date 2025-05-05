package voltaic.client.event;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.Voltaic;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.prefab.utilities.VoltaicTextUtils;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.FORGE)
public class RadiationTooltipHandler {

    @SubscribeEvent
    public static void renderTooltip(ItemTooltipEvent event) {

        if(Screen.hasShiftDown()) {
            ItemStack stack = event.getItemStack();
            if(stack.isEmpty()) {
                return;
            }
            if(stack.getItem() instanceof BlockItem) {
            	BlockItem blockItem = (BlockItem) stack.getItem();
                RadiationShielding shielding = RadiationShieldingRegister.getValue(blockItem.getBlock());
                if(shielding.amount() <= 0) {
                    return;
                }
                event.getToolTip().add(VoltaicTextUtils.tooltip("radiationshieldingamount", ChatFormatter.getChatDisplayShort(shielding.amount(), DisplayUnits.RAD).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
                //event.getTooltipElements().add(Either.left(NuclearTextUtils.tooltip("radiationshieldinglevel", Component.literal(shielding.level() + "").withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY)));
            }
        } else if(Screen.hasControlDown() && !event.getItemStack().isEmpty()) {
            event.getToolTip().add(ChatFormatter.getChatDisplayShort(RadioactiveItemRegister.getValue(event.getItemStack().getItem()).amount(), DisplayUnits.RAD).withStyle(TextFormatting.YELLOW));
        }




    }

}
