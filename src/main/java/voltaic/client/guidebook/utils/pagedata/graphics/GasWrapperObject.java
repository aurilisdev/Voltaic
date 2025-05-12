package voltaic.client.guidebook.utils.pagedata.graphics;

import voltaic.api.gas.Gas;
import voltaic.client.VoltaicClientRegister;
import voltaic.client.guidebook.utils.components.Page;
import net.minecraft.client.gui.GuiGraphics;
import voltaic.prefab.utilities.RenderingUtils;

public class GasWrapperObject extends AbstractGraphicWrapper<GasWrapperObject> {

	public final Gas gas;

	public GasWrapperObject(int xOffset, int yOffset, int width, int height, int trueHeight, Gas gas, GraphicTextDescriptor... descriptors) {
		super(xOffset, yOffset, xOffset, yOffset, width, height, trueHeight, descriptors);
		this.gas = gas;
	}

	@Override
	public void render(GuiGraphics graphics, int wrapperX, int wrapperY, int xShift, int guiWidth, int guiHeight, Page page) {
		RenderingUtils.setShaderColor(gas.getColor());
		graphics.blit(guiWidth + wrapperX + xShift, guiHeight + wrapperY, 0, width, height, VoltaicClientRegister.getSprite(VoltaicClientRegister.TEXTURE_GAS));
		RenderingUtils.resetShaderColor();
	}

}
