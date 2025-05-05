package voltaic.prefab.utilities;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.BlockPosVolume;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.api.electricity.ICapabilityElectrodynamic;

public class CapabilityUtils {

	public static final IFluidHandler EMPTY_FLUID = new IFluidHandler() {

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @Nonnull FluidStack getFluidInTank(int tank) {
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		@Override
		public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
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
		public @Nonnull FluidStack getFluidInTank(int tank) {
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		@Override
		public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @Nonnull ItemStack getContainer() {
			return ItemStack.EMPTY;
		}
	};
	
	public static final IItemHandler EMPTY_ITEM_HANDLER = new IItemHandler() {

		@Override
		public int getSlots() {
			return 0;
		}

		@Override
		public @Nonnull ItemStack getStackInSlot(int slot) {
			return ItemStack.EMPTY;
		}

		@Override
		public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 0;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
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

		@Override
		public CompoundNBT toTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromTag(CompoundNBT nbt) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static final IRadiationManager EMPTY_MANAGER = new IRadiationManager() {

		@Override
		public List<SimpleRadiationSource> getPermanentSources(World world) {
			return Collections.emptyList();
		}

		@Override
		public List<TemporaryRadiationSource> getTemporarySources(World world) {
			return Collections.emptyList();
		}

		@Override
		public List<FadingRadiationSource> getFadingSources(World world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getPermanentLocations(World world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getTemporaryLocations(World world) {
			return Collections.emptyList();
		}

		@Override
		public List<BlockPos> getFadingLocations(World world) {
			return Collections.emptyList();
		}

		@Override
		public void addRadiationSource(SimpleRadiationSource source, World level) {

		}

		@Override
		public int getReachOfSource(World world, BlockPos pos) {
			return 0;
		}

		@Override
		public void setDisipation(double radiationDisipation, World level) {

		}

		@Override
		public void setLocalizedDisipation(double disipation, BlockPosVolume area, World level) {

		}

		@Override
		public void removeLocalizedDisipation(BlockPosVolume area, World level) {

		}

		@Override
		public boolean removeRadiationSource(BlockPos pos, boolean shouldLeaveFadingSource, World level) {
			return false;
		}

		@Override
		public void wipeAllSources(World level) {

		}

		@Override
		public void tick(World world) {

		}

		@Override
		public CompoundNBT toTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromTag(CompoundNBT tag) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static final ICapabilityElectrodynamic EMPTY_ELECTRO = new ICapabilityElectrodynamic() {

		@Override
		public double getJoulesStored() {
			return 0;
		}

		@Override
		public double getMaxJoulesStored() {
			return 0;
		}

		@Override
		public void setJoulesStored(double joules) {
			
		}

		@Override
		public boolean isEnergyReceiver() {
			return false;
		}

		@Override
		public boolean isEnergyProducer() {
			return false;
		}

		@Override
		public void onChange() {
			
		}

		@Override
		public TransferPack getConnectedLoad(LoadProfile loadProfile, Direction dir) {
			return TransferPack.EMPTY;
		}
		
	};

}
