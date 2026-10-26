package voltaic.compatibility.mekanism;

import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import voltaic.Voltaic;

@EventBusSubscriber(modid = Voltaic.ID)
public final class MekanismHazmatTooltip {

    @SubscribeEvent
    public static void addIntegrity(ItemTooltipEvent event) {
	if (!MekanismHazmatIntegrity.isHazmat(event.getItemStack())) {
	    return;
	}
	double integrity = MekanismHazmatIntegrity.getIntegrity(event.getItemStack());
	ChatFormatting colour = integrity > 0.5 ? ChatFormatting.GREEN
		: integrity > 0.25 ? ChatFormatting.GOLD : ChatFormatting.RED;
	event.getToolTip().add(Component
		.literal(String.format(Locale.ROOT, "Hazmat integrity: %.2f%%", integrity * 100.0)).withStyle(colour));
	event.getToolTip().add(Component.literal("Integrity affects Voltaic radiation shielding.")
		.withStyle(ChatFormatting.DARK_GRAY));
    }
}
