package voltaic.prefab.inventory.container.slot.item.type;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;
import voltaic.registers.VoltaicCapabilities;

public class SlotGas extends SlotGeneric {

    public SlotGas(Container inventory, int index, int x, int y) {
	super(SlotType.NORMAL, IconType.GAS_DARK, inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
	return super.mayPlace(stack) && stack.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM) != null;
    }

}
