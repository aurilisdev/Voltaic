package voltaic.common.inventory.container;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.slot.item.type.SlotRestricted;
import voltaic.prefab.inventory.container.slot.item.type.SlotUpgrade;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.math.Color;
import voltaic.registers.VoltaicMenuTypes;

public class ContainerO2OProcessorDouble extends GenericContainerBlockEntity<GenericTile> {

	public ContainerO2OProcessorDouble(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(9), new SimpleContainerData(5));
	}

	public ContainerO2OProcessorDouble(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(VoltaicMenuTypes.CONTAINER_O2OPROCESSORDOUBLE.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		addSlot(new SlotGeneric(inv, nextIndex(), 56 - ContainerO2OProcessor.startXOffset, 24).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotGeneric(inv, nextIndex(), 56 - ContainerO2OProcessor.startXOffset, 44).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotRestricted(inv, nextIndex(), 116 - ContainerO2OProcessor.startXOffset, 24).setIOColor(new Color(255, 0, 0, 255)));
		addSlot(new SlotRestricted(inv, nextIndex(), 116 - ContainerO2OProcessor.startXOffset, 44).setIOColor(new Color(255, 0, 0, 255)));
		addSlot(new SlotRestricted(inv, nextIndex(), 116 - ContainerO2OProcessor.startXOffset + 20, 24).setIOColor(new Color(255, 255, 0, 255)));
		addSlot(new SlotRestricted(inv, nextIndex(), 116 - ContainerO2OProcessor.startXOffset + 20, 44).setIOColor(new Color(255, 255, 0, 255)));
		addSlot(new SlotUpgrade(inv, nextIndex(), 153, 14, ContainerO2OProcessor.VALID_UPGRADES));
		addSlot(new SlotUpgrade(inv, nextIndex(), 153, 34, ContainerO2OProcessor.VALID_UPGRADES));
		addSlot(new SlotUpgrade(inv, nextIndex(), 153, 54, ContainerO2OProcessor.VALID_UPGRADES));
	}
}