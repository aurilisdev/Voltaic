package voltaic.api.electricity;

import net.minecraft.util.Direction;
import voltaic.prefab.utilities.object.TransferPack;

public class CapabilityElectrodynamicStorage implements ICapabilityElectrodynamic {
	
	private double joulesStored = 0;
	private double maxJoulesStored;
	private double voltage;
	private boolean isEnergyProducer;
	private boolean isEnergyReciever;
	
	public CapabilityElectrodynamicStorage(boolean energyReciever, boolean energyProducer, double maxJoules, double joules, double voltage) {
		isEnergyProducer = energyProducer;
		isEnergyReciever = energyReciever;
		maxJoulesStored = maxJoules;
		this.joulesStored = joules;
		this.voltage = voltage;
	}

	@Override
	public double getJoulesStored() {
		return joulesStored;
	}

	@Override
	public double getMaxJoulesStored() {
		return maxJoulesStored;
	}

	@Override
	public void setJoulesStored(double joules) {
		joulesStored = joules;
	}

	@Override
	public boolean isEnergyReceiver() {
		return isEnergyReciever;
	}

	@Override
	public boolean isEnergyProducer() {
		return isEnergyProducer;
	}
	
	@Override
	public double getVoltage() {
		return voltage;
	}

	@Override
	public void onChange() {

	}

	@Override
	public TransferPack getConnectedLoad(LoadProfile loadProfile, Direction dir) {
		return TransferPack.EMPTY;
	}

}
