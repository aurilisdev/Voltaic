package voltaic.common.item.gear;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import voltaic.common.inventory.container.ContainerGuidebook;
import voltaic.common.item.ItemVoltaic;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class ItemGuidebook extends ItemVoltaic {

	private static final String LINK = "https://wiki.aurilis.dev";
	private static final IFormattableTextComponent CONTAINER_TITLE = new TranslationTextComponent("container.guidebook");

	public ItemGuidebook(Properties properties, Supplier<ItemGroup> creativeTab) {
		super(properties, creativeTab);
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltips, ITooltipFlag flag) {
		tooltips.add(VoltaicTextUtils.tooltip("info.guidebookuse").withStyle(TextFormatting.LIGHT_PURPLE));
		tooltips.add(VoltaicTextUtils.tooltip("guidebookname").withStyle(TextFormatting.LIGHT_PURPLE));
		super.appendHoverText(stack, context, tooltips, flag);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand handIn) {
		if (world.isClientSide) {
			if (player.isShiftKeyDown()) {
				player.sendMessage(VoltaicTextUtils.chatMessage("guidebookclick").withStyle(TextFormatting.BOLD, TextFormatting.RED).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LINK))), Util.NIL_UUID);
				return ActionResult.pass(player.getItemInHand(handIn));
			}
		} else if(!player.isShiftKeyDown()) {
			player.openMenu(getMenuProvider(world, player));
		}
		return super.use(world, player, handIn);
	}

	public INamedContainerProvider getMenuProvider(World world, PlayerEntity player) {
		return new SimpleNamedContainerProvider((id, inv, play) -> new ContainerGuidebook(id, player.inventory), CONTAINER_TITLE);
	}

}
