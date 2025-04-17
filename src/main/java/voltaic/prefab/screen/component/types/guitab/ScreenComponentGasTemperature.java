package voltaic.prefab.screen.component.types.guitab;

import java.util.ArrayList;
import java.util.List;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.gas.PropertyGasTank;
import voltaic.api.screen.component.TextPropertySupplier;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;

public class ScreenComponentGasTemperature extends ScreenComponentGuiTab {

	public ScreenComponentGasTemperature(TextPropertySupplier infoHandler, int x, int y) {
		super(GuiInfoTabTextures.REGULAR, IconType.THERMOMETER, infoHandler, x, y);
	}

	public ScreenComponentGasTemperature(int x, int y) {
		super(GuiInfoTabTextures.REGULAR, IconType.THERMOMETER, AbstractScreenComponentInfo.EMPTY, x, y);
	}

	@Override
	protected List<? extends FormattedCharSequence> getInfo(List<? extends FormattedCharSequence> list) {
		if (infoHandler == EMPTY) {
			return getMaxPressureInfo();
		}
		return super.getInfo(list);
	}

	private List<? extends FormattedCharSequence> getMaxPressureInfo() {

		List<FormattedCharSequence> tooltips = new ArrayList<>();

		GenericTile generic = (GenericTile) ((GenericContainerBlockEntity<?>) ((GenericScreen<?>) gui).getMenu()).getSafeHost();

		if (generic == null) {
			return tooltips;
		}

		IComponentGasHandler handler = generic.getComponent(IComponentType.GasHandler);

		int index = 1;

		for (PropertyGasTank tank : handler.getInputTanks()) {

			tooltips.add(VoltaicTextUtils.tooltip("tankmaxin", index, ChatFormatter.getChatDisplayShort(tank.getMaxTemperature(), DisplayUnits.TEMPERATURE_KELVIN)).withStyle(ChatFormatting.GRAY).getVisualOrderText());

			index++;
		}

		index = 1;

		for (PropertyGasTank tank : handler.getOutputTanks()) {

			tooltips.add(VoltaicTextUtils.tooltip("tankmaxout", index, ChatFormatter.getChatDisplayShort(tank.getMaxTemperature(), DisplayUnits.TEMPERATURE_KELVIN)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

			index++;

		}

		return tooltips;

	}

}
