package voltaic.prefab.screen.component.types;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.prefab.screen.component.utils.AbstractScreenComponent;

public class ScreenComponentMultiLabel extends AbstractScreenComponent {

	private final Consumer<MatrixStack> fontConsumer;

	public ScreenComponentMultiLabel(int x, int y, Consumer<MatrixStack> fontConsumer) {
		super(x, y, 0, 0);
		this.fontConsumer = fontConsumer;
	}

	@Override
	public void renderForeground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
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
