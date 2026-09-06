package voltaic.compatibility.jei.utils.gui.types;

import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import voltaic.api.screen.ITexture;
import voltaic.compatibility.jei.utils.gui.ScreenObject;
import voltaic.prefab.screen.component.types.ScreenComponentProgress.ProgressBars;

public class ArrowAnimatedObject extends ScreenObject {

    private final ScreenObject offArrow;
    private final StartDirection startDirection;

    public ArrowAnimatedObject(ITexture offTexture, ITexture onTexture, int x, int y, StartDirection startDirection) {
	super(onTexture, x, y);
	offArrow = new ScreenObject(offTexture, x, y);
	this.startDirection = startDirection;
    }

    public ArrowAnimatedObject(ProgressBars bar, int x, int y, StartDirection startDirection) {
	this(bar.off, bar.on, x, y, startDirection);
    }

    public ScreenObject getOffArrow() {
	return offArrow;
    }

    public StartDirection startDirection() {
	return startDirection;
    }

}
