package voltaic.prefab.screen.component;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.ITexture.Textures;
import voltaic.prefab.screen.component.utils.AbstractScreenComponent;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

/**
 * simple implementation of AbstractScreenComponent that allows custom images to be drawn to the screen
 * 
 * @author skip999
 *
 */
public class ScreenComponentGeneric extends AbstractScreenComponent {

	public ITexture texture;

	@Nullable
	public ITexture icon;

	public Color color = Color.WHITE;

	@Nullable
	public OnTooltip onTooltip = null;

	public ScreenComponentGeneric(ITexture texture, int x, int y) {
		super(x, y, texture.textureWidth(), texture.textureHeight());
		this.texture = texture;
	}

	public ScreenComponentGeneric(int x, int y, int width, int height) {
		super(x, y, width, height);
		texture = Textures.NONE;
	}

	public ScreenComponentGeneric setColor(Color color) {
		this.color = color;
		return this;
	}

	public ScreenComponentGeneric onTooltip(OnTooltip onTooltip) {
		this.onTooltip = onTooltip;
		return this;
	}

	public ScreenComponentGeneric setIcon(ITexture icon) {
		this.icon = icon;
		return this;
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if (!isVisible()) {
			return;
		}
		RenderingUtils.setShaderColor(color);
		graphics.blit(texture.getLocation(), guiWidth + xLocation, guiHeight + yLocation, texture.textureU(), texture.textureV(), texture.textureWidth(), texture.textureHeight(), texture.imageWidth(), texture.imageHeight());
		if(icon != null) {
			int xOffset = (texture.imageWidth() - icon.imageWidth()) / 2;
			int yOffset = (texture.imageHeight() - icon.imageHeight()) / 2;
			graphics.blit(icon.getLocation(), guiWidth + xLocation + xOffset, guiHeight + yLocation + yOffset, icon.textureU(), icon.textureV(), icon.textureWidth(), icon.textureHeight(), icon.imageWidth(), icon.imageHeight());
		}
		RenderingUtils.resetShaderColor();
	}

	@Override
	public void renderForeground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		super.renderForeground(graphics, xAxis, yAxis, guiWidth, guiHeight);
		if (isVisible() && isHovered() && onTooltip != null) {
			onTooltip.onTooltip(graphics, this, xAxis, yAxis);
		}
	}

}
