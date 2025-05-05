package voltaic.prefab.inventory.container.slot.item.type;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;

public class SlotNoModification extends SlotGeneric {

	public SlotNoModification(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public SlotNoModification(ISlotTexture slot, ITexture icon, IInventory inventory, int index, int x, int y) {
		super(slot, icon, inventory, index, x, y);
	}

	@Override
	public boolean mayPickup(PlayerEntity pl) {
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
