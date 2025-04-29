package voltaic.prefab.screen.component.types;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import voltaic.prefab.screen.component.utils.AbstractScreenComponent;

public class ScreenComponentMultiLabel extends AbstractScreenComponent {

	private final Consumer<PoseStack> fontConsumer;

	public ScreenComponentMultiLabel(int x, int y, Consumer<PoseStack> fontConsumer) {
		super(x, y, 0, 0);
		this.fontConsumer = fontConsumer;
	}

	@Override
	public void renderForeground(PoseStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if(!isVisible()){
			return;
		}
		fontConsumer.accept(poseStack);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

}
