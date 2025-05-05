package voltaic.compatibility.jei.utils.label.types;

import net.minecraft.util.text.ITextComponent;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class LabelWrapperGeneric extends AbstractLabelWrapper {

	private final ITextComponent label;

	public LabelWrapperGeneric(Color color, int yPos, int xPos, boolean xIsEnd, ITextComponent label) {
		super(color, yPos, xPos, xIsEnd);
		this.label = label;
	}

	@Override
	public ITextComponent getComponent(AbstractRecipeCategory<?> category, Object recipe) {
		return label;
	}

}
