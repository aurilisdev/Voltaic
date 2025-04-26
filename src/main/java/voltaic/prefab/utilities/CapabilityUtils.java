package voltaic.prefab.utilities;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.BlockPosVolume;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;

public class CapabilityUtils {

	public static final IGasHandler EMPTY_GAS = new IGasHandler() {

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public GasStack getGasInTank(int tank) {
			return GasStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public int getTankMaxTemperature(int tank) {
			return 0;
		}

		@Override
		public int getTankMaxPressure(int tank) {
			return 0;
		}

		@Override
		public boolean isGasValid(int tank, GasStack gas) {
			return false;
		}

		@Override
		public int fill(GasStack gas, GasAction action) {
			return 0;
		}

		@Override
		public GasStack drain(GasStack gas, GasAction action) {
			return GasStack.EMPTY;
		}

		@Override
		public GasStack drain(int maxFill, GasAction action) {
			return GasStack.EMPTY;
		}

		@Override
		public int heat(int tank, int deltaTemperature, GasAction action) {
			return -1;
		}

		@Override
		public int bringPressureTo(int tank, int atm, GasAction action) {
			return -1;
		}

	};

	public static final IFluidHandler EMPTY_FLUID = new IFluidHandler() {

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}

	};

	public static final IEnergyStorage EMPTY_FE = new IEnergyStorage() {
		@Override
		public int receiveEnergy(int toReceive, boolean simulate) {
			return 0;
		}

		@Override
		public int extractEnergy(int toExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return 0;
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return false;
		}
	};

	public static class FEInputDispatcher implements IEnergyStorage {

		private final IEnergyStorage parent;

		public FEInputDispatcher(IEnergyStorage parent) {
			this.parent = parent;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return parent.receiveEnergy(maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return parent.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return parent.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return true;
		}

	}

	public static class FEOutputDispatcher implements IEnergyStorage {

		private final IEnergyStorage parent;

		public FEOutputDispatcher(IEnergyStorage parent) {
			this.parent = parent;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return parent.extractEnergy(maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			return parent.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return parent.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return false;
		}

	}

	public static final IFluidHandlerItem EMPTY_FLUID_ITEM = new IFluidHandlerItem() {

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull ItemStack getContainer() {
			return ItemStack.EMPTY;
		}
	};
	
	public static final IGasHandlerItem EMPTY_GAS_ITEM = new IGasHandlerItem() {
		
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public GasStack getGasInTank(int tank) {
			return GasStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public int getTankMaxTemperature(int tank) {
			return 0;
		}

		@Override
		public int getTankMaxPressure(int tank) {
			return 0;
		}

		@Override
		public boolean isGasValid(int tank, GasStack gas) {
			return false;
		}

		@Override
		public int fill(GasStack gas, GasAction action) {
			return 0;
		}

		@Override
		public GasStack drain(GasStack gas, GasAction action) {
			return GasStack.EMPTY;
		}

		@Override
		public GasStack drain(int maxFill, GasAction action) {
			return GasStack.EMPTY;
		}

		@Override
		public int heat(int tank, int deltaTemperature, GasAction action) {
			return -1;
		}

		@Override
		public int bringPressureTo(int tank, int atm, GasAction action) {
			return -1;
		}

		@Override
		public ItemStack getContainer() {
			return ItemStack.EMPTY;
		}
	};
	
	public static final IItemHandler EMPTY_ITEM_HANDLER = new IItemHandler() {

		@Override
		public int getSlots() {
			return 0;
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return ItemStack.EMPTY;
		}

		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 0;
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return false;
		}
		
	};
	
	public static final IRadiationRecipient EMPTY_RADIATION_REPIPIENT = new IRadiationRecipient() {

		@Override
		public void recieveRadiation(LivingEntity entity, double rads, double strength) {

			
		}

		@Override
		public RadioactiveObject getRecievedRadiation(LivingEntity entity) {
			return RadioactiveObject.ZERO;
		}

		@Override
		public void tick(LivingEntity entity) {
			
		}
		
	};
	
	public static final IRadiationManager EMPTY_MANAGER = new IRadiationManager() {

		@Override
		public List<SimpleRadiationSource> getPermanentSources(Level world) {
			return Collections.emptyList();
		}

		@Override
		public List<TemporaryRadiationSource> getTemporarySources(Level world) {
			return Collections.emptyList();
		}

		@Override
		public List<FadingRadiationSource> getFadingSources(Level world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getPermanentLocations(Level world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getTemporaryLocations(Level world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getFadingLocations(Level world) {
			return Collections.emptyList();
		}

		@Override
		public void addRadiationSource(SimpleRadiationSource source, Level level) {

		}

		@Override
		public int getReachOfSource(Level world, BlockPos pos) {
			return 0;
		}

		@Override
		public void setDisipation(double radiationDisipation, Level level) {

		}

		@Override
		public void setLocalizedDisipation(double disipation, BlockPosVolume area, Level level) {

		}

		@Override
		public void removeLocalizedDisipation(BlockPosVolume area, Level level) {

		}

		@Override
		public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, Level level) {
			return false;
		}

		@Override
		public void wipeAllSources(Level level) {

		}

		@Override
		public void tick(Level world) {

		}
		
	};

}
