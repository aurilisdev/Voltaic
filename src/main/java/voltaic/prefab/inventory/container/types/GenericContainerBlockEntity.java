package voltaic.prefab.inventory.container.types;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;

public abstract class GenericContainerBlockEntity<T extends TileEntity> extends GenericContainerSlotData<IInventory> {

	public GenericContainerBlockEntity(ContainerType<?> type, int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
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
		return (T) getLevel().getBlockEntity(new BlockPos(getData().get(0), getData().get(1), getData().get(2)));
	}

	@Override
	public void validateContainer(IInventory inventory) {
		checkContainerSize(inventory, inventory.getContainerSize());
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return getContainer().stillValid(player);
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		getContainer().stopOpen(player);
	}
}