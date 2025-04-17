package voltaic.compatibility.jei.utils.label.types;

import voltaic.common.recipe.VoltaicRecipe;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;

public class PowerLabelWrapperElectroRecipe extends AbstractLabelWrapper {

	private final int voltage;

	public PowerLabelWrapperElectroRecipe(int xPos, int yPos, int voltage) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, false);
		this.voltage = voltage;
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return VoltaicTextUtils.jeiTranslated("guilabel.power", voltage, ((VoltaicRecipe) recipe).getUsagePerTick() * 20.0 / 1000.0);
	}
}
