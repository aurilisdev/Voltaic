package voltaic.client.guidebook.utils.pagedata.graphics;

import voltaic.client.guidebook.utils.components.Page;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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
	public void render(PoseStack poseStack, int wrapperX, int wrapperY, int xShift, int guiWidth, int guiHeight, Page page) {

		ResourceLocation texture = IClientFluidTypeExtensions.of(fluid).getStillTexture();

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

		RenderSystem.setShaderTexture(0, sprite.atlas().getId());

		RenderingUtils.setShaderColor(new Color(IClientFluidTypeExtensions.of(fluid).getTintColor()));

		GuiComponent.blit(poseStack, guiWidth + wrapperX + xShift, guiHeight + wrapperY, 0, width, height, sprite);

		RenderingUtils.resetShaderColor();

	}

}
