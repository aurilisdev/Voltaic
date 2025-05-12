package voltaic.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import voltaic.prefab.inventory.container.GenericContainer;
import voltaic.registers.VoltaicMenuTypes;

public class ContainerGuidebook extends GenericContainer<IInventory> {

	public ContainerGuidebook(int id, PlayerInventory playerinv) {
		super(VoltaicMenuTypes.CONTAINER_GUIDEBOOK.get(), id, playerinv, EMPTY);
	}

	@Override
	public void validateContainer(IInventory inventory) {

	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {

	}

	@Override
	public void addPlayerInventory(PlayerInventory playerinv) {

	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
}
