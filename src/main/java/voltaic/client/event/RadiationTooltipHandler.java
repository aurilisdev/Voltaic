package voltaic.client.event;

import com.mojang.datafixers.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import voltaic.Voltaic;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.prefab.utilities.VoltaicTextUtils;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.GAME)
public class RadiationTooltipHandler {

    @SubscribeEvent
    public static void renderTooltip(RenderTooltipEvent.GatherComponents event) {
	if (Screen.hasShiftDown()) {
	    ItemStack stack = event.getItemStack();
	    if (stack.isEmpty()) {
		return;
	    }
	    if (stack.getItem() instanceof BlockItem blockItem) {

		RadiationShielding shielding = RadiationShieldingRegister.getValue(blockItem.getBlock());

		if (shielding == RadiationShielding.NONE) {
		    BlockState state = blockItem.getBlock().defaultBlockState();
		    shielding = RadiationShielding.getDefault(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, state);
		}

		if (shielding == RadiationShielding.NONE || shielding.transmission() >= 1.0) {
		    return;
		}

		double blocked = (1.0 - shielding.transmission()) * 100.0;

		event.getTooltipElements()
			.add(Either
				.left(VoltaicTextUtils
					.tooltip("radiationshieldingblocked",
						Component.literal(String.format("%.1f%%", blocked))
							.withStyle(ChatFormatting.GRAY))
					.withStyle(ChatFormatting.DARK_GRAY)));
	    }
	} else if (Screen.hasControlDown() && !event.getItemStack().isEmpty()) {
	    event.getTooltipElements()
		    .add(Either.left(ChatFormatter.getChatDisplayShort(
			    RadioactiveItemRegister.getValue(event.getItemStack().getItem()).amount(), DisplayUnits.RAD)
			    .withStyle(ChatFormatting.YELLOW)));
	}

    }

}
