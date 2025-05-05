package voltaic.prefab.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.utilities.ContainerUtils;

public abstract class GenericContainer<CONTAINERTYPE> extends Container {

	public static final IInventory EMPTY = new Inventory(0);

	private final CONTAINERTYPE inventory;
	private final World world;
	private final PlayerInventory playerinv;
	private final PlayerEntity player;
	private final int slotCount;
	private int playerInvOffset = 0;
	private int nextIndex = 0;

	public int nextIndex() {
		return nextIndex++;
	}

	public int nextIndex(int offset){
		nextIndex += offset;
		return nextIndex ++;
	}

	public GenericContainer(ContainerType<?> type, int id, PlayerInventory playerinv, CONTAINERTYPE inventory) {
		super(type, id);
		validateContainer(inventory);
		this.inventory = inventory;
		this.playerinv = playerinv;
		this.player = playerinv.player;
		this.world = playerinv.player.level;
		addInventorySlots(inventory, playerinv);
		this.slotCount = slots.size();
		addPlayerInventory(playerinv);
	}

	public void addPlayerInventory(PlayerInventory playerinv) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlot(new SlotGeneric(playerinv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + getPlayerInvOffset()));
			}
		}

		for (int k = 0; k < 9; ++k) {
			addSlot(new SlotGeneric(playerinv, k, 8 + k * 18, 142 + getPlayerInvOffset()));
		}
	}


	public abstract void validateContainer(CONTAINERTYPE inventory);

	public abstract void addInventorySlots(CONTAINERTYPE inv, PlayerInventory playerinv);

	public void setPlayerInvOffset(int offset) {
		playerInvOffset = offset;
	}

	public CONTAINERTYPE getContainer() {
		return inventory;
	}

	public World getLevel() {
		return world;
	}

	public PlayerInventory getPlayerInventory() {
		return playerinv;
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public int getAdditionalSlotCount() {
		return slotCount;
	}

	public int getPlayerInvOffset() {
		return playerInvOffset;
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		return ContainerUtils.handleShiftClick(slots, player, index);
	}

}
