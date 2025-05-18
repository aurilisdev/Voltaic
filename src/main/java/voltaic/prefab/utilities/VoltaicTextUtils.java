package voltaic.prefab.utilities;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import voltaic.Voltaic;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;

public class VoltaicTextUtils {

	public static final String GUI_BASE = "gui";
	public static final String TOOLTIP_BASE = "tooltip";
	public static final String JEI_BASE = "jei";
	public static final String GUIDEBOOK_BASE = "guidebook";
	public static final String MESSAGE_BASE = "chat";
	public static final String JEI_INFO_ITEM = "info.item";
	public static final String JEI_INFO_FLUID = "info.fluid";
	public static final String BLOCK_BASE = "block";
	public static final String GAS_BASE = "gas";
	public static final String ADVANCEMENT_BASE = "advancement";
	public static final String DIMENSION = "dimension";
	public static final String CREATIVE_TAB = "creativetab";
	
	private static final IFormattableTextComponent EMPTY = new StringTextComponent("");

	public static IFormattableTextComponent tooltip(String key, Object... additional) {
		return translated(TOOLTIP_BASE, key, additional);
	}

	public static IFormattableTextComponent guidebook(String key, Object... additional) {
		return translated(GUIDEBOOK_BASE, key, additional);
	}

	public static IFormattableTextComponent gui(String key, Object... additional) {
		return translated(GUI_BASE, key, additional);
	}

	public static IFormattableTextComponent chatMessage(String key, Object... additional) {
		return translated(MESSAGE_BASE, key, additional);
	}

	public static IFormattableTextComponent jeiTranslated(String key, Object... additional) {
		return new TranslationTextComponent(JEI_BASE + "." + key, additional);
	}

	public static IFormattableTextComponent jeiItemTranslated(String key, Object... additional) {
		return jeiTranslated(JEI_INFO_ITEM + "." + key, additional);
	}

	public static IFormattableTextComponent jeiFluidTranslated(String key, Object... additional) {
		return jeiTranslated(JEI_INFO_FLUID + "." + key, additional);
	}

	public static IFormattableTextComponent block(String key, Object... additional) {
		return translated(BLOCK_BASE, key, additional);
	}

	public static IFormattableTextComponent gas(String key, Object... additional) {
		return translated(GAS_BASE, key, additional);
	}

	public static IFormattableTextComponent advancement(String key, Object... additional) {
		return translated(ADVANCEMENT_BASE, key, additional);
	}

	public static IFormattableTextComponent dimension(String key, Object... additional) {
		return translated(DIMENSION, key, additional);
	}

	public static IFormattableTextComponent dimension(RegistryKey<World> level, Object... additional) {
		return dimension(level.location().getPath(), additional);
	}

	public static IFormattableTextComponent creativeTab(String key, Object... additional) {
		return translated(CREATIVE_TAB, key, additional);
	}

	public static IFormattableTextComponent translated(String base, String key, Object... additional) {
		return new TranslationTextComponent(base + "." + Voltaic.ID + "." + key, additional);
	}

	public static boolean guiExists(String key) {
		return translationExists(GUI_BASE, key);
	}

	public static boolean tooltipExists(String key) {
		return translationExists(TOOLTIP_BASE, key);
	}

	public static boolean dimensionExistst(String key) {
		return translationExists(DIMENSION, key);
	}

	public static boolean dimensionExists(RegistryKey<World> level) {
		return dimensionExistst(level.location().getPath());
	}

	public static boolean translationExists(String base, String key) {
		return I18n.exists(base + "." + Voltaic.ID + "." + key);
	}

	public static IFormattableTextComponent voltageTooltip(int voltage) {
		return tooltip("machine.voltage", ChatFormatter.getChatDisplayShort(voltage, DisplayUnits.VOLTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY);
	}

	public static IFormattableTextComponent ratio(IFormattableTextComponent numerator, IFormattableTextComponent denominator) {
		return numerator.copy().append(" / ").append(denominator);
	}
	
	public static IFormattableTextComponent empty() {
		return new StringTextComponent("");
	}

}
