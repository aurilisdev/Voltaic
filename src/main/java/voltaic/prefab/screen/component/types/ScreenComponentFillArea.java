package voltaic.prefab.screen.component.types;

import net.minecraft.client.gui.GuiGraphics;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentFillArea extends ScreenComponentGeneric {

    private final Color fill;
    private final Color outline;

    public ScreenComponentFillArea(int x, int y, int width, int height, Color fill, Color outline) {
        super(x, y, width, height);
        this.fill = fill;
        this.outline = outline;
    }

    public ScreenComponentFillArea(int x, int y, int width, int height, Color fill) {
        this(x, y, width, height, fill, fill);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()) {
            return;
        }
        graphics.fill(xLocation + guiWidth, yLocation + guiHeight, xLocation + guiWidth + width, yLocation + guiHeight + height, fill.color());
        graphics.renderOutline(xLocation + guiWidth - 1, yLocation + guiHeight - 1, width + 1, height + 1, outline.color());
    }
}
