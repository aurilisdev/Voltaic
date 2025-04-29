package voltaic.client.guidebook.utils.pagedata;

import com.mojang.blaze3d.vertex.PoseStack;

import voltaic.client.guidebook.ScreenGuidebook;

public interface OnTooltip {

	public void onTooltip(PoseStack poseStack, int xAxis, int yAxis, ScreenGuidebook screen);

}
