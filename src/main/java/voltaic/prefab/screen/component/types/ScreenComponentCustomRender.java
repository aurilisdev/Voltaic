package voltaic.prefab.screen.component.types;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.prefab.screen.component.utils.AbstractScreenComponent;

public class ScreenComponentCustomRender extends AbstractScreenComponent {

    private final Consumer<MatrixStack> poseStackConsumer;

    public ScreenComponentCustomRender(int x, int y, Consumer<MatrixStack> poseStackConsumer) {
        super(x, y, 0, 0);
        this.poseStackConsumer = poseStackConsumer;
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()){
            return;
        }
        poseStackConsumer.accept(poseStack);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
