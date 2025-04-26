package voltaic.prefab.screen.component.types.guitab;

import voltaic.api.screen.component.TextPropertySupplier;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenComponentTemperature extends ScreenComponentGuiTab {

	// this could be condensed to a ScreenComponentGuiTab class, but is left for future expansion purposes
	public ScreenComponentTemperature(TextPropertySupplier infoHandler, int x, int y) {
		super(GuiInfoTabTextures.REGULAR, IconType.TEMPERATURE, infoHandler, x, y);
	}

}