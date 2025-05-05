package voltaic.prefab.screen.component.types;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import voltaic.prefab.screen.component.utils.AbstractScreenComponent;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentSimpleLabel extends AbstractScreenComponent {

	private Supplier<ITextComponent> text = () -> VoltaicTextUtils.empty();
	public Color color = Color.WHITE;

	public ScreenComponentSimpleLabel(int x, int y, int height, Color color, ITextComponent text) {
		this(x, y, height, color, () -> text);
	}

	public ScreenComponentSimpleLabel(int x, int y, int height, Color color, Supplier<ITextComponent> text) {
		super(x, y, 0, height);
		this.text = text;
		this.color = color;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isPointInRegion(x, y, mouseX - gui.getGuiWidth(), mouseY - gui.getGuiHeight(), gui.getFontRenderer().width(text.get()), height);
	}

	@Override
	public void renderForeground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if (isVisible()) {
			gui.getFontRenderer().draw(poseStack, text.get(), x, y, color.color());
		}
	}

}
