package voltaic.prefab.inventory.container.slot.item;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;
import voltaic.prefab.screen.component.utils.SlotTextureProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.utilities.math.Color;

public class SlotGeneric extends Slot implements SlotTextureProvider {

	private final ISlotTexture slotType;
	private final ITexture iconType;

	private boolean active = true;

	@Nullable
	public Color ioColor = null; // null means there is no color for this slot in IO mode meaning it isn't mapped to a face!

	public SlotGeneric(ISlotTexture slotType, ITexture iconType, IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		this.slotType = slotType;
		this.iconType = iconType;
	}

	public SlotGeneric(IInventory inventory, int index, int x, int y) {
		this(SlotType.NORMAL, IconType.NONE, inventory, index, x, y);
	}

	public SlotGeneric setIOColor(Color color) {
		this.ioColor = color;
		return this;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack != null && container.canPlaceItem(getSlotIndex(), stack);
	}

	@Override
	public ISlotTexture getSlotType() {
		return slotType;
	}

	@Override
	public ITexture getIconType() {
		return iconType;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public void setChanged() {
		if (container instanceof ComponentInventory) {
			ComponentInventory inv = (ComponentInventory) container;
			inv.setChanged(index);
		} else {
			super.setChanged();
		}
	}

}