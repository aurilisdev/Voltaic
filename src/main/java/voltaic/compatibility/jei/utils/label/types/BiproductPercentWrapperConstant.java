package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.network.chat.Component;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class BiproductPercentWrapperConstant extends AbstractLabelWrapper {

	private final double percentage;

	public BiproductPercentWrapperConstant(int xPos, int yPos, double percentage) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, false);
		this.percentage = percentage;
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return ChatFormatter.getChatDisplayShort(percentage * 100, DisplayUnits.PERCENTAGE);
	}

}
