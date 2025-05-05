package voltaic.prefab.inventory.container.types;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.api.item.CapabilityItemStackHandler;

public abstract class GenericContainerItem extends GenericContainerSlotData<CapabilityItemStackHandler> {

    /**
     * Documentation note here: DO NOT use ItemStack.EMPTY for the dummy handler created on the client. The empty
     * ItemStack cannot store data components. Use a piece of cobblestone or something generic like that!
     */
    public GenericContainerItem(ContainerType<?> type, int id, PlayerInventory playerinv, CapabilityItemStackHandler handler, IIntArray data) {
        super(type, id, playerinv, handler, data);
    }

    @Override
    public void validateContainer(CapabilityItemStackHandler inventory) {

    }

    @Override
    public ItemStack clicked(int slot, int button, ClickType type, PlayerEntity pl) {

        PlayerInventory playerinv = pl.inventory;

        ItemStack owner = getOwnerItem();

        if (owner.isEmpty() || (slot >= 0 && slot <= pl.inventory.getContainerSize() - 1 && ItemStack.isSame(playerinv.getItem(slot), owner))) {
            return ItemStack.EMPTY;
        }

        return super.clicked(slot, button, type, pl);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {

        return !getOwnerItem().isEmpty();
    }


    /**
     * Retrieves the item that owns this container
     *
     * @return Empty if not found
     */
    public ItemStack getOwnerItem() {

        if (getData().getCount() == 0 || getData().get(0) == -1) {
            return ItemStack.EMPTY;
        }

        try {
            return getPlayer().getItemInHand(Hand.values()[getData().get(0)]);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }

    }

    public @Nullable Hand getHand() {
        if (getData().getCount() == 0 || getData().get(0) == -1) {
            return null;
        }

        try {
            return Hand.values()[getData().get(0)];
        } catch (Exception e) {
            return null;
        }
    }

    public static IIntArray makeDefaultData(int size) {
        IntArray data = new IntArray(size);
        for (int i = 0; i < size; i++) {
            data.set(i, -1);
        }
        return data;
    }

    public static IIntArray makeData(Hand hand) {
        IntArray data = new IntArray(1);
        data.set(0, hand.ordinal());
        return data;
    }

}
