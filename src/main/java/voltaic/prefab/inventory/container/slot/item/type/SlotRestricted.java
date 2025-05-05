package voltaic.prefab.inventory.container.slot.item.type;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class SlotRestricted extends SlotGeneric {

    private List<Item> whitelist;
    private List<Class<?>> classes;
    private List<Capability<?>> validCapabilities;

    private Predicate<ItemStack> mayPlace = stack -> false;

    public SlotRestricted(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public SlotRestricted(ISlotTexture slot, ITexture icon, IInventory inv, int index, int x, int y) {
        super(slot, icon, inv, index, x, y);
    }

    public SlotRestricted setRestriction(Predicate<ItemStack> mayPlace) {
        this.mayPlace = mayPlace;
        return this;
    }

    public SlotRestricted setRestriction(Item... items) {
        whitelist = Arrays.asList(items);
        mayPlace = stack -> whitelist.contains(stack.getItem());
        return this;
    }

    public SlotRestricted setRestriction(Class<?>... items) {
        classes = Arrays.asList(items);
        mayPlace = stack -> {
            if (classes != null) {
                for (Class<?> cl : classes) {
                    if (cl.isInstance(stack.getItem())) {
                        return true;
                    }
                }
            }
            return false;
        };
        return this;
    }

    public SlotRestricted setRestriction(Capability<?>... capabilities) {
        validCapabilities = Arrays.asList(capabilities);
        mayPlace = stack -> {
            if (validCapabilities != null) {
                for (Capability<?> cap : validCapabilities) {
                    if (stack.getCapability(cap) != null) {
                        return true;
                    }
                }
            }
            return false;
        };
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && mayPlace.test(stack);
    }
}
