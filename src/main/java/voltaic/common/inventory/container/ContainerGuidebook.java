package voltaic.common.inventory.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import voltaic.prefab.inventory.container.GenericContainer;
import voltaic.registers.VoltaicMenuTypes;

public class ContainerGuidebook extends GenericContainer<Container> {

    public ContainerGuidebook(int id, Inventory playerinv) {
	super(VoltaicMenuTypes.CONTAINER_GUIDEBOOK.get(), id, playerinv, EMPTY);
    }

    @Override
    public void validateContainer(Container inventory) {

    }

    @Override
    public void addInventorySlots(Container inv, Inventory playerinv) {

    }

    @Override
    public void addPlayerInventory(Inventory playerinv) {

    }

    @Override
    public boolean stillValid(Player player) {
	return true;
    }
}
