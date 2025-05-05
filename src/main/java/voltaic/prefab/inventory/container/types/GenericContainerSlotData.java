package voltaic.prefab.inventory.container.types;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import voltaic.prefab.inventory.container.GenericContainer;

public abstract class GenericContainerSlotData<CONTAINERTYPE> extends GenericContainer<CONTAINERTYPE> {

    private final IIntArray data;

    public GenericContainerSlotData(ContainerType<?> type, int id, PlayerInventory playerinv, CONTAINERTYPE inventory, IIntArray data) {
        super(type, id, playerinv, inventory);
        checkContainerDataCount(data, data.getCount());
        this.data = data;
        addDataSlots(this.data);
    }

    public IIntArray getData() {
        return data;
    }
}
