package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.util.text.ITextComponent;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class TimeLabelWrapperConstant extends AbstractLabelWrapper {

	private final int ticks;

	public TimeLabelWrapperConstant(int xPos, int yPos, int processingTicks) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, true);
		ticks = processingTicks;
	}

	@Override
	public ITextComponent getComponent(AbstractRecipeCategory<?> category, Object recipe) {

		return ChatFormatter.getChatDisplayShort(ticks / 20.0, DisplayUnits.TIME_SECONDS);

	}

}
