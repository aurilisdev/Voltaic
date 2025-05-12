package voltaic.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import voltaic.prefab.utilities.RenderingUtils;

public abstract class GenericCustomScreen<T extends Container> extends ContainerScreen<T> {
	protected GenericCustomScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderingUtils.resetShaderColor();;
		RenderingUtils.bindTexture(getScreenBackground());
		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	public abstract ResourceLocation getScreenBackground();
}
