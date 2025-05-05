package voltaic.prefab.screen.component.types;

import com.mojang.blaze3d.matrix.MatrixStack;

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
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()) {
            return;
        }
        int x = this.x + guiWidth;
        int y = this.y + guiHeight;
        fill(poseStack, x - 1, y - 1, x + width + 1, y + height + 1, outline.color());
        fill(poseStack, x, y, x + width, y + height, fill.color());
    }
}
