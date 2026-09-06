package voltaic.prefab.screen.component.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentInventoryIO extends ScreenComponentGeneric {

    public static final int SQUARE_SIZE = 6;
    public static final int COLORS_PER_ROW = 4;
    public static final int DRAWING_AREA_SIZE = 24;

    public Color SLOT_GRAY = new Color(150, 150, 150, 255);

    private final Direction side;

    public ScreenComponentInventoryIO(int x, int y, Direction side) {
	super(InventoryIOTextures.DEFAULT, x, y);
	onTooltip((graphics, component, xAxis, yAxis) -> {
	    graphics.renderTooltip(requireScreen().getFontRenderer(), getLabelFromDir(), xAxis, yAxis);
	});
	this.side = side;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
	super.renderBackground(graphics, xAxis, yAxis, guiWidth, guiHeight);

	GenericScreen<?> screen = (GenericScreen<?>) requireScreen();

	GenericContainerBlockEntity<?> container = (GenericContainerBlockEntity<?>) screen.getMenu();

	GenericTile tile = (GenericTile) container.getSafeHost().orElse(null);
	if (tile == null || !tile.hasComponent(IComponentType.Inventory))
	    return;

	ComponentInventory inv = tile.<ComponentInventory>getComponent(IComponentType.Inventory).orElse(null);

	HashSet<Integer> slots = inv.relativeDirectionToSlotsMap[side.ordinal()];

	if (slots == null)
	    return;

	List<Color> uniqueColors = new ArrayList<>();

	slots.forEach(slot -> {
	    SlotGeneric generic = (SlotGeneric) container.slots.get(slot);
	    if (generic.ioColor == null)
		return;

	    for (Color col : uniqueColors) {
		if (col.equals(generic.ioColor))
		    return;
	    }
	    uniqueColors.add(generic.ioColor);
	});

	if (uniqueColors.isEmpty())
	    return;

	if (uniqueColors.size() <= 16) {
	    fillSquare(graphics, uniqueColors, xLocation + guiWidth, yLocation + guiHeight);
	    return;
	}

	throw new UnsupportedOperationException("There are more than 16 unique colors mapped to a face!");

	// TODO more than 16 unique colors mapped to a single face
	// I will deal with this when it becomes a problem 10/15/23

    }

    private void fillSquare(GuiGraphics graphics, List<Color> colors, int x, int y) {
	int wholeRows = colors.size() / COLORS_PER_ROW;
	int lastRowCount = colors.size() % COLORS_PER_ROW;

	int wholeHeight = DRAWING_AREA_SIZE / (wholeRows + 1);

	int remainderHeight = DRAWING_AREA_SIZE - wholeHeight * wholeRows;

	int index = 0;

	for (int i = 0; i < wholeRows; i++) {
	    for (int j = 0; j < COLORS_PER_ROW; j++) {

		graphics.fill(x + 1 + j * SQUARE_SIZE, y + 1 + i * wholeHeight, x + 1 + (j + 1) * SQUARE_SIZE,
			y + 1 + (i + 1) * wholeHeight, colors.get(index).multiply(SLOT_GRAY).color());

		index++;
	    }
	}

	int remainderWidth = DRAWING_AREA_SIZE / lastRowCount;

	int lastToAdd = DRAWING_AREA_SIZE - remainderWidth * lastRowCount;

	for (int i = 0; i < lastRowCount - 1; i++) {

	    graphics.fill(x + 1 + i * remainderWidth, y + 1 + wholeHeight * wholeRows, x + 1 + (i + 1) * remainderWidth,
		    y + 1 + wholeHeight * wholeRows + remainderHeight, colors.get(index).multiply(SLOT_GRAY).color());

	    index++;
	}

	graphics.fill(x + 1 + remainderWidth * (lastRowCount - 1), y + 1 + wholeHeight * wholeRows,
		x + 1 + lastRowCount * remainderWidth + lastToAdd, y + 1 + wholeHeight * wholeRows + remainderHeight,
		colors.get(index).multiply(SLOT_GRAY).color());

    }

    private MutableComponent getLabelFromDir() {
	MutableComponent component = null;
	component = switch (side) {
	case DOWN -> VoltaicTextUtils.tooltip("inventoryio.bottom");
	default -> VoltaicTextUtils.tooltip("inventoryio.bottom");
	case UP -> VoltaicTextUtils.tooltip("inventoryio.top");
	case EAST -> VoltaicTextUtils.tooltip("inventoryio.left");
	case WEST -> VoltaicTextUtils.tooltip("inventoryio.right");
	case NORTH -> VoltaicTextUtils.tooltip("inventoryio.front");
	case SOUTH -> VoltaicTextUtils.tooltip("inventoryio.back");
	};
//		return component.append(": " + StringUtils.capitalize(side.name().toLowerCase()));
	// TODO: Add some dynamic face direction here for the slots.
	return component;
    }

    public enum InventoryIOTextures implements ITexture {
	DEFAULT(26, 26, 0, 0, 26, 26, "slotio");

	private final int textureWidth;
	private final int textureHeight;
	private final int textureU;
	private final int textureV;
	private final int imageWidth;
	private final int imageHeight;
	private final ResourceLocation loc;

	InventoryIOTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth,
		int imageHeight, String name) {
	    this.textureWidth = textureWidth;
	    this.textureHeight = textureHeight;
	    this.textureU = textureU;
	    this.textureV = textureV;
	    this.imageWidth = imageWidth;
	    this.imageHeight = imageHeight;
	    loc = Voltaic.rl("textures/screen/component/io/" + name + ".png");
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
