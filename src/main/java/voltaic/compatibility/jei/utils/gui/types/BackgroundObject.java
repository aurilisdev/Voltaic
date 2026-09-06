package voltaic.compatibility.jei.utils.gui.types;

import voltaic.compatibility.jei.utils.gui.JeiTextures;
import voltaic.compatibility.jei.utils.gui.ScreenObject;

public class BackgroundObject extends ScreenObject {

    private final int width;
    private final int height;

    public BackgroundObject(int width, int height) {
	super(JeiTextures.BACKGROUND_DEFAULT, 0, 0);
	this.width = width;
	this.height = height;
    }

    @Override
    public int getWidth() {
	return width;
    }

    @Override
    public int getHeight() {
	return height;
    }

}
