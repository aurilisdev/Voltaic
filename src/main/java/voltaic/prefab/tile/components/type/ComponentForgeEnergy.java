package voltaic.prefab.tile.components.type;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import org.jetbrains.annotations.Nullable;
import voltaic.Voltaic;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicCapabilities;

public class ComponentForgeEnergy implements IComponent {

    private GenericTile holder;
    private final boolean electroLoaded;

    private final ComponentElectrodynamic electro;

    public ComponentForgeEnergy(GenericTile holder) {
        this.holder = holder;
        if(!holder.hasComponent(IComponentType.Electrodynamic)) {
            throw new RuntimeException("You must define a ComponentElectrodynamic before defining a ComponentForgeEnergy!");
        }
        electro = holder.getComponent(IComponentType.Electrodynamic);
        if(Voltaic.isElectroLoaded() == null) {
            throw new RuntimeException("You are doing something very wrong indeed");
        }
        electroLoaded = Voltaic.isElectroLoaded();

    }

    @Override
    public IComponentType getType() {
        return IComponentType.ForgeEnergy;
    }

    @Override
    public void holder(GenericTile holder) {
        this.holder = holder;
    }

    @Nullable
    @Override
    public GenericTile getHolder() {
        return holder;
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side, CapabilityInputType inputType) {
    	if(electroLoaded || side == null) {
            return LazyOptional.empty();
        }

        ICapabilityElectrodynamic electrodynamic = electro.getCapability(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, side, inputType).resolve().get();

        return electrodynamic == null ? LazyOptional.empty() : LazyOptional.of(() -> new ElectrodynamicWrapper(electrodynamic)).cast();
    }

    private static final class ElectrodynamicWrapper implements IEnergyStorage {

        private final ICapabilityElectrodynamic electro;

        private ElectrodynamicWrapper(ICapabilityElectrodynamic electro) {
            this.electro = electro;
        }


        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            return (int) Math.ceil(electro.receivePower(TransferPack.joulesVoltage(toReceive, electro.getVoltage()), simulate).getJoules());
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return (int) electro.extractPower(TransferPack.joulesVoltage(toExtract, electro.getVoltage()), simulate).getJoules();
        }

        @Override
        public int getEnergyStored() {
            return (int) electro.getJoulesStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) electro.getMaxJoulesStored();
        }

        @Override
        public boolean canExtract() {
            return electro.isEnergyProducer();
        }

        @Override
        public boolean canReceive() {
            return electro.isEnergyReceiver();
        }
    }

}
