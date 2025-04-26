package voltaic.client.guidebook.utils.pagedata.graphics;

import voltaic.client.guidebook.utils.components.Page;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class FluidWrapperObject extends AbstractGraphicWrapper<FluidWrapperObject> {

	public final Fluid fluid;

	public FluidWrapperObject(int xOffset, int yOffset, int width, int height, int trueHeight, Fluid fluid, GraphicTextDescriptor... descriptors) {
		super(xOffset, yOffset, xOffset, yOffset, width, height, trueHeight, descriptors);
		this.fluid = fluid;
	}

	@Override
	public void render(GuiGraphics graphics, int wrapperX, int wrapperY, int xShift, int guiWidth, int guiHeight, Page page) {

		ResourceLocation texture = IClientFluidTypeExtensions.of(fluid).getStillTexture();

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

		RenderingUtils.setShaderColor(new Color(IClientFluidTypeExtensions.of(fluid).getTintColor()));

		graphics.blit(guiWidth + wrapperX + xShift, guiHeight + wrapperY, 0, width, height, sprite);

		RenderingUtils.resetShaderColor();

	}

}
