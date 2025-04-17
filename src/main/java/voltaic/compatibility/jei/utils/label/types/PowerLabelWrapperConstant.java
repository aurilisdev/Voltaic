package voltaic.compatibility.jei.utils.label.types;

import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;

public class PowerLabelWrapperConstant extends AbstractLabelWrapper {

	private final int voltage;
	private final double wattage;

	public PowerLabelWrapperConstant(int xPos, int yPos, double joulesPerTick, int voltage) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, false);
		this.voltage = voltage;
		wattage = joulesPerTick * 20.0 / 1000.0;
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {

		return VoltaicTextUtils.jeiTranslated("guilabel.power", voltage, wattage);

	}

}
