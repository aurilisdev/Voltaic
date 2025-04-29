package voltaic.prefab.screen.component.types;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import voltaic.prefab.screen.component.utils.AbstractScreenComponent;

public class ScreenComponentCustomRender extends AbstractScreenComponent {

    private final Consumer<PoseStack> poseStackConsumer;

    public ScreenComponentCustomRender(int x, int y, Consumer<PoseStack> poseStackConsumer) {
        super(x, y, 0, 0);
        this.poseStackConsumer = poseStackConsumer;
    }

    @Override
    public void renderBackground(PoseStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
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
