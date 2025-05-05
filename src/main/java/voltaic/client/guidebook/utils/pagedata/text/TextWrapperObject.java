package voltaic.client.guidebook.utils.pagedata.text;

import voltaic.client.guidebook.utils.pagedata.AbstractWrapperObject;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.util.text.ITextComponent;

/**
 * A simple wrapper class that contains a segment of text along with basic formatting data for it
 * 
 * @author skip999
 *
 */
public class TextWrapperObject extends AbstractWrapperObject<TextWrapperObject> {

	public static final TextWrapperObject BLANK_LINE = new TextWrapperObject(VoltaicTextUtils.empty());

	public static final Color DEFAULT_COLOR = new Color(4210752);

	public static final Color LIGHT_GREY = new Color(170, 170, 170, 255);

	public Color color;
	public ITextComponent text;

	public int numberOfIndentions = 0;
	public boolean isSeparateStart = false;
	public boolean center = false;

	public TextWrapperObject(ITextComponent text) {
		this(DEFAULT_COLOR, text);
	}

	public TextWrapperObject(Color color, ITextComponent text) {
		this.color = color;
		this.text = text;
	}

	public TextWrapperObject setIndentions(int numOfIndentions) {
		this.numberOfIndentions = numOfIndentions;
		return this;
	}

	public TextWrapperObject setSeparateStart() {
		isSeparateStart = true;
		return this;
	}

	public TextWrapperObject setCentered() {
		center = true;
		return this;
	}

}
