package voltaic.prefab.screen.component.button.type;

import voltaic.api.screen.ITexture;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.prefab.screen.component.button.ScreenComponentButton;

public class ButtonSpecificPage extends ScreenComponentButton<ButtonSpecificPage> {

    public final int page;

    public ButtonSpecificPage(int x, int y, int width, int height, int page) {
	super(x, y, width, height);
	this.page = page;
    }

    public ButtonSpecificPage(ITexture texture, int x, int y, int page) {
	super(texture, x, y);
	this.page = page;
    }

    @Override
    public boolean isVisible() {
	return page == ScreenGuidebook.currPageNumber || page == ScreenGuidebook.currPageNumber + 1;
    }

}
