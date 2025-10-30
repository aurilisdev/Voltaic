package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.network.chat.Component;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class TimeLabelWrapperElectroRecipe extends AbstractLabelWrapper {

	public TimeLabelWrapperElectroRecipe(int xPos, int yPos) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, true);
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return ChatFormatter.getChatDisplayShort(((VoltaicRecipe) recipe).getTicks() / 20.0, DisplayUnits.TIME_SECONDS);
	}

}
