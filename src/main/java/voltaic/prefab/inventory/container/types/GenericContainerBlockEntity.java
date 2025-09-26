package voltaic.prefab.inventory.container.types;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class GenericContainerBlockEntity<T extends BlockEntity> extends GenericContainerSlotData<Container> {

	public GenericContainerBlockEntity(MenuType<?> type, int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(type, id, playerinv, inventory, inventorydata);
	}

	@Nullable
	public T getSafeHost() {
		try {
			return getUnsafeHost();
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	public T getUnsafeHost() {
		ContainerData data = getData();

		int x = data.get(0) * 30000 + data.get(1);
		int y = data.get(2); // realistically y will only be between -64 (0 if 1.16.5) and 300
		int z = data.get(3) * 30000 + data.get(4);

		return (T) getLevel().getBlockEntity(new BlockPos(x, y, z));
	}

	@Override
	public void validateContainer(Container inventory) {
		checkContainerSize(inventory, inventory.getContainerSize());
	}

	@Override
	public boolean stillValid(Player player) {
		return getContainer().stillValid(player);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		getContainer().stopOpen(player);
	}
}