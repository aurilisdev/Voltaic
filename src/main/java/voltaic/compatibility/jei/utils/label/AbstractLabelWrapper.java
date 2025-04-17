package voltaic.compatibility.jei.utils.label;

import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;

public abstract class AbstractLabelWrapper {

	private Color color;
	private int yPos;
	private int xPos;
	private boolean xIsEnd;

	public AbstractLabelWrapper(Color color, int yPos, int xPos, boolean xIsEnd) {
		this.color = color;
		this.yPos = yPos;
		this.xPos = xPos;
		this.xIsEnd = xIsEnd;
	}

	public Color getColor() {
		return color;
	}

	public int getYPos() {
		return yPos;
	}

	public int getXPos() {
		return xPos;
	}

	public boolean xIsEnd() {
		return xIsEnd;
	}

	public abstract Component getComponent(AbstractRecipeCategory<?> category, Object recipe);
}
