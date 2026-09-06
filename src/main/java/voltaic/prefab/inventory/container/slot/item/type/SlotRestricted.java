package voltaic.prefab.inventory.container.slot.item.type;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;

public class SlotRestricted extends SlotGeneric {
    @Nullable
    private List<Class<?>> classes;
    @Nullable
    private List<ItemCapability<?, Void>> validCapabilities;

    private Predicate<ItemStack> mayPlace = stack -> false;

    public SlotRestricted(Container inventory, int index, int x, int y) {
	super(inventory, index, x, y);
    }

    public SlotRestricted(ISlotTexture slot, ITexture icon, Container inv, int index, int x, int y) {
	super(slot, icon, inv, index, x, y);
    }

    public SlotRestricted setRestriction(Predicate<ItemStack> mayPlace) {
	this.mayPlace = mayPlace;
	return this;
    }

    public SlotRestricted setRestriction(Item... items) {
	List<Item> pwhitelist = Arrays.asList(items);
	mayPlace = stack -> pwhitelist.contains(stack.getItem());
	return this;
    }

    public SlotRestricted setRestriction(Class<?>... items) {
	classes = Arrays.asList(items);
	mayPlace = stack -> {
	    List<Class<?>> pClasses = classes;
	    if (pClasses != null) {
		for (Class<?> cl : pClasses) {
		    if (cl.isInstance(stack.getItem()))
			return true;
		}
	    }
	    return false;
	};
	return this;
    }

    public SlotRestricted setRestriction(ItemCapability<?, Void>... capabilities) {
	validCapabilities = Arrays.asList(capabilities);
	mayPlace = stack -> {
	    if (validCapabilities != null) {
		for (ItemCapability<?, Void> cap : validCapabilities) {
		    if (stack.getCapability(cap) != null)
			return true;
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
