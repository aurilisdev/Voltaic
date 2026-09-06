package voltaic.prefab.screen.component.editbox;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.utilities.math.Color;

@OnlyIn(Dist.CLIENT)
/**
 * A modified variant of the EditBox class integrated into the Electrodynamics
 * render system and fixing certain issues with the vanilla-provided class
 * 
 * That's a spicy copy-pasta
 * 
 * @author skip999
 */
public class ScreenComponentEditBox extends ScreenComponentGeneric {

    public static final ResourceLocation TEXTURE = Voltaic.rl("textures/screen/component/textinputbar.png");

    public static final char[] POSITIVE_DECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' };
    public static final char[] DECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-' };

    public static final char[] POSITIVE_INTEGER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final char[] INTEGER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' };

    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    public static final Color DEFAULT_TEXT_COLOR = new Color(224, 224, 224, 0);
    private final Font font;
    /** Has the current text being edited on the textbox. */
    private String value = "";
    private int maxLength = 32;
    private int frame;
    /** if true the textbox can lose focus by clicking elsewhere on the screen */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEditable = true;
    private boolean shiftPressed;
    /**
     * The current character index that should be used as start of the rendered
     * text.
     */
    private int displayPos;
    private int cursorPos;
    /** other selection position, maybe the same as the cursor */
    private int highlightPos;
    private Color textColor = DEFAULT_TEXT_COLOR;
    private Color textColorUneditable = new Color(112, 112, 112, 0);
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    /** Called to check if the text is valid */
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (p_94147_, p_94148_) -> FormattedCharSequence
	    .forward(p_94147_, Style.EMPTY);

    public ScreenComponentEditBox(int x, int y, int width, int height, Font font) {
	super(x, y, width, height);
	texture = TextInputTextures.TEXT_INPUT_BASE;
	this.font = font;
    }

    public ScreenComponentEditBox setResponder(Consumer<String> responder) {
	this.responder = responder;
	return this;
    }

    public ScreenComponentEditBox setFormatter(BiFunction<String, Integer, FormattedCharSequence> textFormatter) {
	formatter = textFormatter;
	return this;
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public ScreenComponentEditBox setValue(String text) {
	if (filter.test(text)) {
	    if (text.length() > maxLength) {
		value = text.substring(0, maxLength);
	    } else {
		value = text;
	    }

	    moveCursorToEnd();
	    setHighlightPos(cursorPos);
	    onValueChange(text);
	}
	return this;
    }

    /**
     * Returns the contents of the textbox
     */
    public String getValue() {
	return value;
    }

    /**
     * Returns the text between the cursor and selectionEnd.
     */
    public String getHighlighted() {
	int min = Math.min(cursorPos, highlightPos);
	int max = Math.max(cursorPos, highlightPos);
	return value.substring(min, max);
    }

    public ScreenComponentEditBox setFilter(Predicate<String> validator) {
	filter = validator;
	return this;
    }

    public ScreenComponentEditBox setFilter(char[] validChars) {
	return setFilter(getValidator(validChars));
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text
     * if there is a selection.
     */
    public void insertText(String textToWrite) {
	int min = Math.min(cursorPos, highlightPos);
	int max = Math.max(cursorPos, highlightPos);
	int length = maxLength - value.length() - (min - max);
	String filtered = StringUtil.filterText(textToWrite);
	int filteredLength = filtered.length();
	if (length < filteredLength) {
	    filtered = filtered.substring(0, length);
	    filteredLength = length;
	}

	String updated = new StringBuilder(value).replace(min, max, filtered).toString();
	if (filter.test(updated)) {
	    value = updated;
	    setCursorPosition(min + filteredLength);
	    setHighlightPos(cursorPos);
	    onValueChange(value);
	}
    }

    private void onValueChange(String newText) {
	if (responder != null) {
	    responder.accept(newText);
	}

    }

    private void deleteText(int count) {
	if (Screen.hasControlDown()) {
	    deleteWords(count);
	} else {
	    deleteChars(count);
	}

    }

    /**
     * Deletes the given number of words from the current cursor's position, unless
     * there is currently a selection, in which case the selection is deleted
     * instead.
     */
    public void deleteWords(int num) {
	if (!value.isEmpty()) {
	    if (highlightPos != cursorPos) {
		insertText("");
	    } else {
		deleteChars(this.getWordPosition(num) - cursorPos);
	    }
	}
    }

    /**
     * Deletes the given number of characters from the current cursor's position,
     * unless there is currently a selection, in which case the selection is deleted
     * instead.
     */
    public void deleteChars(int num) {
	if (!value.isEmpty()) {
	    if (highlightPos != cursorPos) {
		insertText("");
	    } else {
		int cursorPos = getCursorPos(num);
		int min = Math.min(cursorPos, this.cursorPos);
		int max = Math.max(cursorPos, this.cursorPos);
		if (min != max) {
		    String updated = new StringBuilder(value).delete(min, max).toString();
		    if (filter.test(updated)) {
			value = updated;
			moveCursorTo(min);
		    }
		}
	    }
	}
    }

    /**
     * Gets the starting index of the word at the specified number of words away
     * from the cursor position.
     */
    public int getWordPosition(int numWords) {
	return this.getWordPosition(numWords, getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of
     * words away from the given position.
     */
    private int getWordPosition(int numOfWords, int position) {
	return this.getWordPosition(numOfWords, position, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping
     * consecutive spaces
     */
    private int getWordPosition(int numOfWords, int position, boolean skipSpaces) {
	int originalPos = position;
	boolean noWords = numOfWords < 0;
	int absNoWords = Math.abs(numOfWords);

	for (int i = 0; i < absNoWords; ++i) {
	    if (!noWords) {
		int lengthOfText = value.length();
		originalPos = value.indexOf(32, originalPos);
		if (originalPos == -1) {
		    originalPos = lengthOfText;
		} else {
		    while (skipSpaces && originalPos < lengthOfText && value.charAt(originalPos) == ' ') {
			++originalPos;
		    }
		}
	    } else {
		while (skipSpaces && originalPos > 0 && value.charAt(originalPos - 1) == ' ') {
		    --originalPos;
		}

		while (originalPos > 0 && value.charAt(originalPos - 1) != ' ') {
		    --originalPos;
		}
	    }
	}

	return originalPos;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the
     * selection
     */
    public void moveCursor(int delta) {
	moveCursorTo(getCursorPos(delta));
    }

    private int getCursorPos(int delta) {
	return Util.offsetByCodepoints(value, cursorPos, delta);
    }

    /**
     * Sets the current position of the cursor.
     */
    public void moveCursorTo(int pos) {
	setCursorPosition(pos);
	if (!shiftPressed) {
	    setHighlightPos(cursorPos);
	}

	onValueChange(value);
    }

    public void setCursorPosition(int pos) {
	cursorPos = Mth.clamp(pos, 0, value.length());
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    public void moveCursorToStart() {
	moveCursorTo(0);
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    public void moveCursorToEnd() {
	moveCursorTo(value.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
	if (!canConsumeInput())
	    return false;
	shiftPressed = Screen.hasShiftDown();
	if (Screen.isSelectAll(keyCode)) {
	    moveCursorToEnd();
	    setHighlightPos(0);
	    return true;
	}
	if (Screen.isCopy(keyCode)) {
	    Minecraft.getInstance().keyboardHandler.setClipboard(getHighlighted());
	    return true;
	}
	if (Screen.isPaste(keyCode)) {
	    if (isEditable) {
		insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
	    }

	    return true;
	}
	if (Screen.isCut(keyCode)) {
	    Minecraft.getInstance().keyboardHandler.setClipboard(getHighlighted());
	    if (isEditable) {
		insertText("");
	    }

	    return true;
	}
	switch (keyCode) {
	case 259:
	    if (isEditable) {
		shiftPressed = false;
		deleteText(-1);
		shiftPressed = Screen.hasShiftDown();
	    }

	    return true;
	case 260:
	case 264:
	case 265:
	case 266:
	case 267:
	default:
	    return false;
	case 261:
	    if (isEditable) {
		shiftPressed = false;
		deleteText(1);
		shiftPressed = Screen.hasShiftDown();
	    }

	    return true;
	case 262:
	    if (Screen.hasControlDown()) {
		moveCursorTo(this.getWordPosition(1));
	    } else {
		moveCursor(1);
	    }

	    return true;
	case 263:
	    if (Screen.hasControlDown()) {
		moveCursorTo(this.getWordPosition(-1));
	    } else {
		moveCursor(-1);
	    }

	    return true;
	case 268:
	    moveCursorToStart();
	    return true;
	case 269:
	    moveCursorToEnd();
	    return true;
	}
    }

    public boolean canConsumeInput() {
	return isVisible() && isFocused() && isEditable();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
	if (!canConsumeInput() || !StringUtil.isAllowedChatCharacter(codePoint))
	    return false;
	if (isEditable) {
	    insertText(Character.toString(codePoint));
	}

	return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
	if (!isVisible())
	    return false;
	boolean mouseOver = isMouseOver(mouseX, mouseY);
	if (canLoseFocus) {
	    setFocus(mouseOver);
	}

	if (isFocused() && mouseOver && button == 0) {
	    int exitBoxXPos = Mth.floor(mouseX) - xLocation - (int) requireScreen().getGuiWidth() - 4;

	    String text = font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());
	    moveCursorTo(font.plainSubstrByWidth(text, exitBoxXPos).length() + displayPos);
	    return true;
	}
	return false;
    }

    /**
     * Sets focus to this gui element
     */
    public ScreenComponentEditBox setFocus(boolean isFocused) {
	setFocused(isFocused);
	return this;
    }

    /**
     * Increments the cursor counter
     */
    public void tick() {
	++frame;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

	drawExpandedBox(graphics, texture.getLocation(), xLocation + guiWidth, yLocation + guiHeight, width, height);

	Color textColor = isEditable ? this.textColor : textColorUneditable;
	int highlightedSize = cursorPos - displayPos;
	int highlightedLength = highlightPos - displayPos;

	String displayedText = font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());

	boolean isHighlightedValid = highlightedSize >= 0 && highlightedSize <= displayedText.length();
	boolean blinkCursor = isFocused() && frame / 6 % 2 == 0 && isHighlightedValid;

	int textStartX = xLocation + guiWidth + 4;
	int textStartY = yLocation + guiHeight + (height - 8) / 2;

	int textStartPre = textStartX;

	if (highlightedLength > displayedText.length()) {
	    highlightedLength = displayedText.length();
	}

	if (!displayedText.isEmpty()) {
	    String highlightedText = isHighlightedValid ? displayedText.substring(0, highlightedSize) : displayedText;
	    textStartPre = graphics.drawString(font, formatter.apply(highlightedText, displayPos), textStartX,
		    textStartY, textColor.color());
	}

	boolean isCursorPastLength = cursorPos < value.length() || value.length() >= getMaxLength();

	int textStartPreCopy = textStartPre;

	if (!isHighlightedValid) {
	    textStartPreCopy = highlightedSize > 0 ? textStartX + width : textStartX;
	} else if (isCursorPastLength) {
	    textStartPreCopy = textStartPre - 1;
	    --textStartPre;
	}

	if (!displayedText.isEmpty() && isHighlightedValid && highlightedSize < displayedText.length()) {
	    graphics.drawString(font, formatter.apply(displayedText.substring(highlightedSize), cursorPos),
		    textStartPre, textStartY, textColor.color());
	}

	if (!isCursorPastLength && suggestion != null) {
	    graphics.drawString(font, suggestion, textStartPreCopy - 1, textStartY, -8355712);
	}

	if (blinkCursor) {
	    if (isCursorPastLength) {
		graphics.fill(RenderType.guiOverlay(), textStartPreCopy, textStartY - 1, textStartPreCopy + 1,
			textStartY + 1 + 9, -3092272);
	    } else {
		graphics.drawString(font, "_", textStartPreCopy, textStartY, textColor.color());
	    }
	}

	if (highlightedLength != highlightedSize) {
	    int l1 = textStartX + font.width(displayedText.substring(0, highlightedLength));
	    renderHighlight(graphics, textStartPreCopy, textStartY - 1, l1 - 1, textStartY + 1 + 9, guiWidth,
		    guiHeight);
	}

    }

    /**
     * Draws the blue selection box.
     */
    private void renderHighlight(GuiGraphics graphics, int pMinX, int pMinY, int pMaxX, int pMaxY, int guiWidth,
	    int guiHeight) {
	if (pMinX < pMaxX) {
	    int i = pMinX;
	    pMinX = pMaxX;
	    pMaxX = i;
	}

	if (pMinY < pMaxY) {
	    int j = pMinY;
	    pMinY = pMaxY;
	    pMaxY = j;
	}

	if (pMaxX > xLocation + width + guiWidth) {
	    pMaxX = yLocation + width;
	}

	if (pMinX > xLocation + width + guiWidth) {
	    pMinX = xLocation + width;
	}

	graphics.fill(RenderType.guiTextHighlight(), pMinX, pMinY, pMaxX, pMaxY, -16776961);
    }

    /**
     * Sets the maximum length for the text in this text box. If the current text is
     * longer than this length, the current text will be trimmed.
     */
    public ScreenComponentEditBox setMaxLength(int length) {
	maxLength = length;
	if (value.length() > length) {
	    value = value.substring(0, length);
	    onValueChange(value);
	}
	return this;

    }

    /**
     * Returns the maximum number of character that can be contained in this
     * textbox.
     */
    public int getMaxLength() {
	return maxLength;
    }

    /**
     * Returns the current position of the cursor.
     */
    public int getCursorPosition() {
	return cursorPos;
    }

    /**
     * Sets the color to use when drawing this text box's text. A different color is
     * used if this text box is disabled.
     */
    public ScreenComponentEditBox setTextColor(Color pColor) {
	textColor = pColor;
	return this;
    }

    /**
     * Sets the color to use for text in this text box when this text box is
     * disabled.
     */
    public ScreenComponentEditBox setTextColorUneditable(Color pColor) {
	textColorUneditable = pColor;
	return this;
    }

    protected void onFocusedChanged(boolean pFocused) {
	if (pFocused) {
	    frame = 0;
	}
    }

    private boolean isEditable() {
	return isEditable;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed
     * in.
     */
    public void setEditable(boolean pEnabled) {
	isEditable = pEnabled;
    }

    /**
     * Returns the width of the textbox depending on if background drawing is
     * enabled.
     */
    public int getInnerWidth() {
	return width - 8;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the
     * cursor position mark the edges of the selection). If the anchor is set beyond
     * the bounds of the current text, it will be put back inside.
     */
    public void setHighlightPos(int position) {
	int length = value.length();
	highlightPos = Mth.clamp(position, 0, length);
	if (displayPos > length) {
	    displayPos = length;
	}

	int innerWidth = getInnerWidth();

	String text = font.plainSubstrByWidth(value.substring(displayPos), innerWidth);

	int textStartX = text.length() + displayPos;
	if (highlightPos == displayPos) {
	    displayPos -= font.plainSubstrByWidth(value, innerWidth, true).length();
	}

	if (highlightPos > textStartX) {
	    displayPos += highlightPos - textStartX;
	} else if (highlightPos <= displayPos) {
	    displayPos -= displayPos - highlightPos;
	}

	displayPos = Mth.clamp(displayPos, 0, length);
    }

    /**
     * Sets whether this text box loses focus when something other than it is
     * clicked.
     */
    public void setCanLoseFocus(boolean canLoseFocus) {
	this.canLoseFocus = canLoseFocus;
    }

    public void setSuggestion(@Nullable String suggestion) {
	this.suggestion = suggestion;
    }

    public int getScreenX(int charNum) {
	return (int) (charNum > value.length() ? xLocation + requireScreen().getGuiWidth()
		: xLocation + requireScreen().getGuiWidth() + font.width(value.substring(0, charNum)));
    }

    public void setX(int xPos) {
	xLocation = xPos;
    }

    public static void drawExpandedBox(GuiGraphics graphics, ResourceLocation texture, int x, int y, int boxWidth,
	    int boxHeight) {
	if (boxWidth < 18) {
	    if (boxHeight < 18) {
		graphics.blit(texture, x, y, boxWidth, boxHeight, 0, 0, boxWidth, boxHeight, boxWidth, boxHeight);
	    } else {
		graphics.blit(texture, x, y, boxWidth, 7, 0, 0, boxWidth, 7, boxWidth, 18);

		int sectionHeight = boxHeight - 14;
		int heightIterations = sectionHeight / 4;
		int remainderHeight = sectionHeight % 4;

		int heightOffset = 7;
		for (int i = 0; i < heightIterations; i++) {
		    graphics.blit(texture, x, y + heightOffset, boxWidth, 4, 0, 7, boxWidth, 4, boxWidth, 18);
		    heightOffset += 4;
		}
		graphics.blit(texture, x, y + heightOffset, boxWidth, remainderHeight, 0, 7, boxWidth, remainderHeight,
			boxWidth, 18);

		graphics.blit(texture, x, y + boxHeight - 7, boxWidth, 7, 0, 11, boxWidth, 7, boxWidth, 18);
	    }
	} else if (boxHeight < 18) {
	    graphics.blit(texture, x, y, 7, boxHeight, 0, 0, 7, boxHeight, 18, boxHeight);

	    int sectionWidth = boxWidth - 14;
	    int widthIterations = sectionWidth / 4;
	    int remainderWidth = sectionWidth % 4;

	    int widthOffset = 7;
	    for (int i = 0; i < widthIterations; i++) {
		graphics.blit(texture, x + widthOffset, y, 4, boxHeight, 7, 0, 4, boxHeight, 18, boxHeight);
		widthOffset += 4;
	    }
	    graphics.blit(texture, x + widthOffset, y, remainderWidth, boxHeight, 7, 0, remainderWidth, boxHeight, 18,
		    boxHeight);

	    graphics.blit(texture, x + boxWidth - 7, y, 7, boxHeight, 11, 0, 7, boxHeight, 18, boxHeight);
	} else {
	    // the button is >= 18x18 at this point

	    // draw squares
	    int squareWidth = boxWidth - 10;
	    int squareWidthIterations = squareWidth / 8;
	    int remainderSquareWidth = squareWidth % 8;

	    int squareHeight = boxHeight - 10;
	    int squareHeightIterations = squareHeight / 8;
	    int remainderSquareHeight = squareHeight % 8;

	    int heightOffset = 5;
	    int widthOffset = 5;

	    for (int i = 0; i <= squareHeightIterations; i++) {
		int height = i == squareHeightIterations ? remainderSquareHeight : 8;
		for (int j = 0; j < squareWidthIterations; j++) {
		    draw(graphics, texture, x, y, widthOffset, heightOffset, 5, 5, 8, height);
		    widthOffset += 8;
		}
		draw(graphics, texture, x, y, widthOffset, heightOffset, 5, 5, remainderSquareWidth, height);
		widthOffset = 5;
		heightOffset += 8;
	    }

	    // draw tl corner
	    draw(graphics, texture, x, y, 0, 0, 0, 0, 8, 8);

	    // draw top strip

	    int stripWidth = boxWidth - 14;
	    int stripWidthIterations = stripWidth / 4;
	    int remainderStripWidth = stripWidth % 4;

	    int stripHeight = boxHeight - 14;
	    int stripHeightIterations = stripHeight / 4;
	    int remainderStripHeight = stripHeight % 4;

	    widthOffset = 7;
	    for (int i = 0; i < stripWidthIterations; i++) {
		draw(graphics, texture, x, y, widthOffset, 0, 7, 0, 4, 5);
		widthOffset += 4;
	    }
	    draw(graphics, texture, x, y, widthOffset, 0, 7, 0, remainderStripWidth, 5);

	    // draw tr corner
	    draw(graphics, texture, x, y, boxWidth - 8, 0, 10, 0, 8, 8);

	    // draw left strip
	    heightOffset = 7;
	    for (int i = 0; i < stripHeightIterations; i++) {
		draw(graphics, texture, x, y, 0, heightOffset, 0, 7, 5, 4);
		heightOffset += 4;
	    }
	    draw(graphics, texture, x, y, 0, heightOffset, 0, 5, 5, remainderStripHeight);

	    // draw right strip
	    heightOffset = 7;
	    widthOffset = boxWidth - 5;
	    for (int i = 0; i < stripHeightIterations; i++) {
		draw(graphics, texture, x, y, widthOffset, heightOffset, 13, 7, 5, 4);
		heightOffset += 4;
	    }
	    draw(graphics, texture, x, y, widthOffset, heightOffset, 13, 7, 5, remainderStripHeight);

	    // draw bl corner
	    draw(graphics, texture, x, y, 0, boxHeight - 8, 0, 10, 8, 8);

	    // draw bottom strip
	    heightOffset = boxHeight - 5;
	    widthOffset = 7;
	    for (int i = 0; i < stripWidthIterations; i++) {
		draw(graphics, texture, x, y, widthOffset, heightOffset, 7, 13, 4, 5);
		widthOffset += 4;
	    }
	    draw(graphics, texture, x, y, widthOffset, heightOffset, 7, 13, remainderStripWidth, 5);

	    // draw br corner
	    draw(graphics, texture, x, y, boxWidth - 8, boxHeight - 8, 10, 10, 8, 8);

	}

    }

    private static void draw(GuiGraphics graphics, ResourceLocation texture, int x, int y, int widthOffset,
	    int heightOffset, int textXOffset, int textYOffset, int width, int height) {
	graphics.blit(texture, x + widthOffset, y + heightOffset, width, height, textXOffset, textYOffset, width,
		height, 18, 18);
    }

    public static Predicate<String> getValidator(char[] validChars) {
	return string -> {

	    if (string.isEmpty())
		return true;

	    boolean flag = false;

	    for (char character : string.toCharArray()) {
		for (char valid : validChars) {
		    if (valid == character) {
			flag = true;
			break;
		    }
		}
		if (!flag)
		    return false;
		flag = false;
	    }
	    return true;
	};
    }

    public enum TextInputTextures implements ITexture {
	TEXT_INPUT_BASE(0, 0, 0, 0, 16, 16, TEXTURE);

	private final int textureWidth;
	private final int textureHeight;
	private final int textureU;
	private final int textureV;
	private final int imageWidth;
	private final int imageHeight;
	private final ResourceLocation loc;

	TextInputTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth,
		int imageHeight, ResourceLocation loc) {
	    this.textureWidth = textureWidth;
	    this.textureHeight = textureHeight;
	    this.textureU = textureU;
	    this.textureV = textureV;
	    this.imageWidth = imageWidth;
	    this.imageHeight = imageHeight;
	    this.loc = loc;
	}

	@Override
	public ResourceLocation getLocation() {
	    return loc;
	}

	@Override
	public int imageHeight() {
	    return imageHeight;
	}

	@Override
	public int imageWidth() {
	    return imageWidth;
	}

	@Override
	public int textureHeight() {
	    return textureHeight;
	}

	@Override
	public int textureU() {
	    return textureU;
	}

	@Override
	public int textureV() {
	    return textureV;
	}

	@Override
	public int textureWidth() {
	    return textureWidth;
	}

    }
}