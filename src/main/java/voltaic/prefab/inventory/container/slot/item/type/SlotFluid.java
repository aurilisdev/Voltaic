package voltaic.prefab.inventory.container.slot.item.type;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;

public class SlotFluid extends SlotGeneric {

    public SlotFluid(Container inventory, int index, int x, int y) {
        super(SlotType.NORMAL, IconType.FLUID_DARK, inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (super.mayPlace(stack) && stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
            return true;
        }
        return false;
    }

}
