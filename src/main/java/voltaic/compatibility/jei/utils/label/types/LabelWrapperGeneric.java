package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.network.chat.Component;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class LabelWrapperGeneric extends AbstractLabelWrapper {

	private final Component label;

	public LabelWrapperGeneric(Color color, int yPos, int xPos, boolean xIsEnd, Component label) {
		super(color, yPos, xPos, xIsEnd);
		this.label = label;
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return label;
	}

}
