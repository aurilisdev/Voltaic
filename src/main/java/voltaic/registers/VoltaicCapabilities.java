package voltaic.registers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import voltaic.Voltaic;
import voltaic.api.electricity.CapabilityElectrodynamicStorage;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.misc.CapabilityLocationStorage;
import voltaic.api.misc.ILocationStorage;
import voltaic.api.radiation.CapabilityRadiationRecipient;
import voltaic.api.radiation.RadiationManager;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.prefab.utilities.object.Location;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class VoltaicCapabilities {

    public static final double DEFAULT_VOLTAGE = 120.0;
    public static final String LOCATION_KEY = "location";

    @CapabilityInject(ICapabilityElectrodynamic.class)
    public static Capability<ICapabilityElectrodynamic> CAPABILITY_ELECTRODYNAMIC_BLOCK;
    @CapabilityInject(ILocationStorage.class)
	public static Capability<ILocationStorage> CAPABILITY_LOCATIONSTORAGE_ITEM;
	@CapabilityInject(IRadiationRecipient.class)
	public static Capability<IRadiationRecipient> CAPABILITY_RADIATIONRECIPIENT;
	@CapabilityInject(IRadiationManager.class)
	public static Capability<IRadiationManager> CAPABILITY_RADIATIONMANAGER;
	
	public static void register() {
		
		CapabilityManager.INSTANCE.register(ICapabilityElectrodynamic.class, new IStorage<ICapabilityElectrodynamic>() {

			@Override
			public INBT writeNBT(Capability<ICapabilityElectrodynamic> capability, ICapabilityElectrodynamic instance, Direction side) {
				CompoundNBT tag = new CompoundNBT();
				tag.putDouble("joulesstored", instance.getJoulesStored());
				return tag;
			}

			@Override
			public void readNBT(Capability<ICapabilityElectrodynamic> capability, ICapabilityElectrodynamic instance, Direction side, INBT nbt) {
				CompoundNBT tag = (CompoundNBT) nbt;
				instance.setJoulesStored(tag.getDouble("joulesstored"));
			}
			
		}, () -> new CapabilityElectrodynamicStorage(true, true, 100000, 0, DEFAULT_VOLTAGE));
		
		CapabilityManager.INSTANCE.register(ILocationStorage.class, new IStorage<ILocationStorage>() {

			@Override
			public INBT writeNBT(Capability<ILocationStorage> capability, ILocationStorage instance, Direction side) {
				CompoundNBT nbt = new CompoundNBT();
				List<Location> locations = instance.getLocations();
				nbt.putInt("size", locations.size());
				for (int i = 0; i < locations.size(); i++) {
					locations.get(i).writeToNBT(nbt, VoltaicCapabilities.LOCATION_KEY + i);
				}
				return nbt;
			}

			@Override
			public void readNBT(Capability<ILocationStorage> capability, ILocationStorage instance, Direction side, INBT nbt) {
				CompoundNBT tag = (CompoundNBT) nbt;
				List<Location> locations = new ArrayList<>();
				for (int i = 0; i < tag.getInt("size"); i++) {
					locations.add(Location.readFromNBT(tag, VoltaicCapabilities.LOCATION_KEY + i));
				}
				instance.setLocations(locations);
			}
			
		}, () -> new CapabilityLocationStorage(0));
		
		CapabilityManager.INSTANCE.register(IRadiationRecipient.class, new IStorage<IRadiationRecipient>() {

			@Override
			public INBT writeNBT(Capability<IRadiationRecipient> capability, IRadiationRecipient instance, Direction side) {
				return instance.toTag();
			}

			@Override
			public void readNBT(Capability<IRadiationRecipient> capability, IRadiationRecipient instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
			
		}, () -> new CapabilityRadiationRecipient());
		
		CapabilityManager.INSTANCE.register(IRadiationManager.class, new IStorage<IRadiationManager>() {

			@Override
			public INBT writeNBT(Capability<IRadiationManager> capability, IRadiationManager instance, Direction side) {
				return instance.toTag();
			}

			@Override
			public void readNBT(Capability<IRadiationManager> capability, IRadiationManager instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
			
		}, () -> new RadiationManager());
		
	}


}
