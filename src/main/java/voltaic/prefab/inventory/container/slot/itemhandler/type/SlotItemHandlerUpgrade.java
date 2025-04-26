package voltaic.prefab.inventory.container.slot.itemhandler.type;

import java.util.Arrays;
import java.util.List;

import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.itemhandler.SlotItemHandlerGeneric;
import voltaic.prefab.inventory.container.slot.utils.IUpgradeSlot;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotItemHandlerUpgrade extends SlotItemHandlerGeneric implements IUpgradeSlot {

	private final List<SubtypeItemUpgrade> upgrades;

	public SlotItemHandlerUpgrade(IItemHandler itemHandler, int index, int xPosition, int yPosition, SubtypeItemUpgrade... upgrades) {
		super(SlotType.NORMAL, IconType.UPGRADE_DARK, itemHandler, index, xPosition, yPosition);

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
