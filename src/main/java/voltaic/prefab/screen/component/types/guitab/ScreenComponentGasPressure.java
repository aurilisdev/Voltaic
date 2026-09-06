package voltaic.prefab.screen.component.types.guitab;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
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

public class ScreenComponentGasPressure extends ScreenComponentGuiTab {

    public ScreenComponentGasPressure(TextPropertySupplier infoHandler, int x, int y) {
	super(GuiInfoTabTextures.REGULAR, IconType.PRESSURE_GAUGE, infoHandler, x, y);
    }

    public ScreenComponentGasPressure(int x, int y) {
	super(GuiInfoTabTextures.REGULAR, IconType.PRESSURE_GAUGE, AbstractScreenComponentInfo.EMPTY, x, y);
    }

    @Override
    protected List<? extends FormattedCharSequence> getInfo(List<? extends FormattedCharSequence> list) {
	if (infoHandler == EMPTY)
	    return getMaxPressureInfo();
	return super.getInfo(list);
    }

    private List<? extends FormattedCharSequence> getMaxPressureInfo() {

	List<FormattedCharSequence> tooltips = new ArrayList<>();

	GenericTile generic = (GenericTile) ((GenericContainerBlockEntity<?>) ((GenericScreen<?>) requireScreen())
		.getMenu()).getSafeHost().orElse(null);
	if (generic == null)
	    return tooltips;

	IComponentGasHandler handler = generic.<IComponentGasHandler>getComponent(IComponentType.GasHandler)
		.orElse(null);
	if (handler == null)
	    return tooltips;

	int index = 1;
	for (PropertyGasTank tank : handler.getInputTanks()) {
	    tooltips.add(VoltaicTextUtils
		    .tooltip("tankmaxin", index,
			    ChatFormatter.getChatDisplayShort(tank.getMaxPressure(), DisplayUnits.PRESSURE_ATM))
		    .withStyle(ChatFormatting.GRAY).getVisualOrderText());
	    index++;
	}
	index = 1;
	for (PropertyGasTank tank : handler.getOutputTanks()) {
	    tooltips.add(VoltaicTextUtils
		    .tooltip("tankmaxout", index,
			    ChatFormatter.getChatDisplayShort(tank.getMaxPressure(), DisplayUnits.PRESSURE_ATM))
		    .withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
	    index++;
	}
	return tooltips;

    }

}
