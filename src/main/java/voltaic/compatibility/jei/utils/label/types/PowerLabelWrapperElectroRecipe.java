package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.util.text.ITextComponent;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

public class PowerLabelWrapperElectroRecipe extends AbstractLabelWrapper {

	private final int voltage;

	public PowerLabelWrapperElectroRecipe(int xPos, int yPos, int voltage) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, false);
		this.voltage = voltage;
	}

	@Override
	public ITextComponent getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return VoltaicTextUtils.jeiTranslated("guilabel.power", voltage, ((VoltaicRecipe) recipe).getUsagePerTick() * 20.0 / 1000.0);
	}
}
