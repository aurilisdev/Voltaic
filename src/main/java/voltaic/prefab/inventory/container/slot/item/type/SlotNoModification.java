package voltaic.prefab.inventory.container.slot.item.type;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;

public class SlotNoModification extends SlotGeneric {

    public SlotNoModification(Container inventory, int index, int x, int y) {
	super(inventory, index, x, y);
    }

    public SlotNoModification(ISlotTexture slot, ITexture icon, Container inventory, int index, int x, int y) {
	super(slot, icon, inventory, index, x, y);
    }

    @Override
    public boolean allowModification(Player pl) {
	return false;
    }

    @Override
    public boolean mayPickup(Player pl) {
	return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
	return false;
    }
}
