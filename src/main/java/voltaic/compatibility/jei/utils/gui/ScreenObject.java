package voltaic.compatibility.jei.utils.gui;

import net.minecraft.util.ResourceLocation;
import voltaic.Voltaic;
import voltaic.api.screen.ITexture;

public class ScreenObject {

	protected static final ResourceLocation BACKGROUND = Voltaic.rl("textures/screen/jei/background.png");
	protected static final ResourceLocation ITEM_SLOTS = Voltaic.rl("textures/screen/jei/itemslots.png");
	protected static final ResourceLocation FLUID_GAUGES = Voltaic.rl("textures/screen/jei/fluidgauges.png");
	protected static final ResourceLocation ARROWS = Voltaic.rl("textures/screen/jei/arrows.png");
	protected static final ResourceLocation GAS_GAUGES = Voltaic.rl("textures/screen/jei/gasgauges.png");

	protected ITexture texture;
	protected int x;
	protected int y;

	public ScreenObject(ITexture texture, int x, int y) {
		this.texture = texture;
		this.x = x;
		this.y = y;
	}

	public ITexture getTexture() {
		return texture;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return texture.textureWidth();
	}

	public int getHeight() {
		return texture.textureHeight();
	}

}
