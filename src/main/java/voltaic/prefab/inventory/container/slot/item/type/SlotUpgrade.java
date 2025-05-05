package voltaic.prefab.inventory.container.slot.item.type;

import java.util.Arrays;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.slot.utils.IUpgradeSlot;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;

public class SlotUpgrade extends SlotGeneric implements IUpgradeSlot {

	private final List<SubtypeItemUpgrade> upgrades;

	public SlotUpgrade(IInventory inventory, int index, int x, int y, SubtypeItemUpgrade... upgrades) {
		super(SlotType.NORMAL, IconType.UPGRADE_DARK, inventory, index, x, y);

		this.upgrades = Arrays.asList(upgrades);

	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if(stack.getItem() instanceof ItemUpgrade) {
			ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();
			return upgrades.contains(upgrade.subtype);
		}
		return false;
	}

	@Override
	public List<SubtypeItemUpgrade> getUpgrades() {
		return upgrades;
	}

}
