package voltaic.compatibility.jei.utils.label.types;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;

public class TemperatureLabelWrapper extends AbstractLabelWrapper {

    public TemperatureLabelWrapper(Color color, int yPos, int xPos, boolean xIsEnd) {
	super(color, yPos, xPos, xIsEnd);
    }

    @Override
    public @Nullable Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {
	return null;
    }

}
