package voltaic.prefab.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import voltaic.api.item.IItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

public class CapabilityForgeEnergyItem implements IEnergyStorage {

    private final ItemStack stack;
    private final IItemElectric electric;

    private final boolean canRecieve;
    private final boolean canExtract;

    public CapabilityForgeEnergyItem(ItemStack stack, boolean canRecieve, boolean canExtract) {
        this.stack = stack;
        electric = (IItemElectric) stack.getItem();
        this.canRecieve = canRecieve;
        this.canExtract = canExtract;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return (int) electric.receivePower(stack, TransferPack.joulesVoltage(toReceive, electric.getElectricProperties().receive.getVoltage()), simulate).getJoules();
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return (int) electric.extractPower(stack, toExtract, simulate).getJoules();
    }

    @Override
    public int getEnergyStored() {
        return (int) electric.getJoulesStored(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) electric.getMaximumCapacity(stack);
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canReceive() {
        return canRecieve;
    }
}
