package voltaic.client.guidebook.utils.pagedata.graphics;

import voltaic.client.guidebook.utils.components.Page;
import voltaic.prefab.utilities.RenderingUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.Item;

/**
 * A simple wrapper class that contains render data for an item or block on the Guidebook screen The class is capable of displaying items/blocks as well as text descriptors for said items/blocks
 * 
 * @author skip999
 *
 */
public class ItemWrapperObject extends AbstractGraphicWrapper<ItemWrapperObject> {

	public final float scale;
	public final Item item;

	public ItemWrapperObject(int xOffset, int yOffset, int width, int height, int trueHeight, float scale, Item item, GraphicTextDescriptor... descriptors) {
		super(xOffset, yOffset, xOffset - 7, yOffset - 10, width, height, trueHeight, descriptors);
		this.scale = scale;
		this.item = item;
	}

	@Override
	public void render(PoseStack stack, int wrapperX, int wrapperY, int xShift, int guiWidth, int guiHeight, Page page) {
		RenderingUtils.renderItemScaled(item, guiWidth + xOffset + wrapperX + xShift, guiHeight + yOffset + wrapperY, scale);
	}

}
