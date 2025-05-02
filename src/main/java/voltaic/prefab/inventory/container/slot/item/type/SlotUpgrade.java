package voltaic.prefab.inventory.container.slot.item.type;

import java.util.Arrays;
import java.util.List;

import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.slot.utils.IUpgradeSlot;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class SlotUpgrade extends SlotGeneric implements IUpgradeSlot {

	private final List<SubtypeItemUpgrade> upgrades;

	public SlotUpgrade(Container inventory, int index, int x, int y, SubtypeItemUpgrade... upgrades) {
		super(SlotType.NORMAL, IconType.UPGRADE_DARK, inventory, index, x, y);

		this.upgrades = Arrays.asList(upgrades);

	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof ItemUpgrade upgrade && upgrades.contains(upgrade.subtype);
	}

	@Override
	public List<SubtypeItemUpgrade> getUpgrades() {
		return upgrades;
	}

}
