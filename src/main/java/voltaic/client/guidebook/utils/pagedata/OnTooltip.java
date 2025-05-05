package voltaic.client.guidebook.utils.pagedata;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.client.guidebook.ScreenGuidebook;

public interface OnTooltip {

	public void onTooltip(MatrixStack poseStack, int xAxis, int yAxis, ScreenGuidebook screen);

}
