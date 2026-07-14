package voltaic.prefab.inventory.container.slot.item.type;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;

public class SlotQuarryTrashcan extends SlotGeneric {

	public SlotQuarryTrashcan(Container inventory, int index, int x, int y) {
		super(SlotType.NORMAL, IconType.TRASH_CAN_DARK, inventory, index, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean isActive() {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack item = container.getItem(i);
			if (!item.isEmpty() && item.getItem() instanceof ItemUpgrade upgrade && upgrade.subtype == SubtypeItemUpgrade.itemvoid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return isActive() ? super.mayPlace(stack) : false;
	}

}
