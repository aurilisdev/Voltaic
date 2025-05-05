package voltaic.client.guidebook.utils.components;

import java.util.ArrayList;
import java.util.List;

import voltaic.client.guidebook.utils.pagedata.OnClick;
import voltaic.client.guidebook.utils.pagedata.OnKeyPress;
import voltaic.client.guidebook.utils.pagedata.OnTooltip;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import net.minecraft.util.text.IFormattableTextComponent;

/**
 * A simple data-wrapping class that contains a name, a logo, and the various chapters associated with it
 * 
 * @author skip999
 *
 */
public abstract class Module {

	public List<Chapter> chapters = new ArrayList<>();
	private int startingPageNumber = 0;

	public Module() {

	}

	public void setStartPage(int page) {
		startingPageNumber = page;
	}

	public int getPage() {
		return startingPageNumber;
	}

	public boolean isCat(IFormattableTextComponent cat) {
		return getTitle().getString().equals(cat.getString());
	}

	public abstract void addChapters();

	public abstract AbstractGraphicWrapper<?> getLogo();

	public abstract IFormattableTextComponent getTitle();

	public OnTooltip onTooltip() {
		return null;
	}

	public OnClick onClick() {
		return null;
	}

	public OnKeyPress onKeyPress() {
		return null;
	}

	public boolean isFirst() {
		return false;
	}

}
