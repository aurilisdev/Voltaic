package voltaic.compatibility.jei.utils.gui.types;

import mezz.jei.api.recipe.RecipeIngredientRole;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.compatibility.jei.utils.gui.ScreenObject;

public class ItemSlotObject extends ScreenObject {

    private RecipeIngredientRole role;
    private ScreenObject icon = null;

    public ItemSlotObject(ISlotTexture slotTexture, int x, int y, RecipeIngredientRole role) {
	super(slotTexture, x, y);
	this.role = role;
    }

    public ItemSlotObject(ISlotTexture slotTexture, ITexture iconTexture, int x, int y, RecipeIngredientRole role) {
	super(slotTexture, x, y);
	int slotXOffset = (slotTexture.imageWidth() - iconTexture.imageWidth()) / 2;
	int slotYOffset = (slotTexture.imageHeight() - iconTexture.imageHeight()) / 2;
	icon = new ScreenObject(iconTexture, x + slotXOffset, y + slotYOffset);
	this.role = role;
    }

    public ScreenObject getIcon() {
	return icon;
    }

    public int getItemXStart() {
	return x - ((ISlotTexture) texture).xOffset();
    }

    public int getItemYStart() {
	return y - ((ISlotTexture) texture).yOffset();
    }

    public RecipeIngredientRole getRole() {
	return role;
    }

}
