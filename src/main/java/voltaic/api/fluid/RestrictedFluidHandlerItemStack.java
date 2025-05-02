package voltaic.api.fluid;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/**
 * Almost carbon copy of Forge's FluidHandlerItemStack capability, except the way you validate fluids actually makes
 * sense and I don't have to bolt on a bunch of random crap to make it work
 * 
 * @author skip999
 */
public class RestrictedFluidHandlerItemStack implements IFluidHandlerItem, ICapabilityProvider {

    public static final String FLUID_NBT_KEY = "Fluid";

    private Predicate<FluidStack> isFluidValid = stack -> true;
    
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;
    protected int capacity;

    public RestrictedFluidHandlerItemStack(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    public RestrictedFluidHandlerItemStack setValidator(Predicate<FluidStack> isFluidValid) {
        this.isFluidValid = isFluidValid;
        return this;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid.test(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(1, resource)) {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            int fillAmount = Math.min(capacity, resource.getAmount());

            if (action.execute()) {
                FluidStack filled = resource.copy();
                filled.setAmount(fillAmount);
                setFluid(filled);
            }

            return fillAmount;
        }
        if (resource.isFluidEqual(contained)) {
            int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

            if (action.execute() && fillAmount > 0) {
                contained.grow(fillAmount);
                setFluid(contained);
            }

            return fillAmount;
        }

        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !getFluid().isFluidEqual(resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            return FluidStack.EMPTY;
        }

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);

        FluidStack drained = contained.copy();
		drained.setAmount(drainAmount);

        if (action.execute()) {
            contained.shrink(drainAmount);
            if (contained.isEmpty()) {
                setContainerToEmpty();
            } else {
                setFluid(contained);
            }
        }

        return drained;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }
    
    @Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
	}

    @NotNull
	public FluidStack getFluid() {
		CompoundTag tagCompound = container.getTag();
		if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY)) {
			return FluidStack.EMPTY;
		}
		return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
	}

	public void setFluid(FluidStack fluid) {
		if (!container.hasTag()) {
			container.setTag(new CompoundTag());
		}

		CompoundTag fluidTag = new CompoundTag();
		fluid.writeToNBT(fluidTag);
		container.getTag().put(FLUID_NBT_KEY, fluidTag);
	}

	public void setContainerToEmpty() {
		container.removeTagKey(FLUID_NBT_KEY);
	}

    public static class Consumable extends RestrictedFluidHandlerItemStack {

        public Consumable(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        public void setContainerToEmpty() {
            super.setContainerToEmpty();
            container.shrink(1);
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends RestrictedFluidHandlerItemStack {

        public final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        public void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer;
        }
    }

}
