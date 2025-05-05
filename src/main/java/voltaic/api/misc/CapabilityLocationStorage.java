package voltaic.api.misc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import voltaic.prefab.utilities.object.Location;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityLocationStorage implements ILocationStorage, ICapabilitySerializable<CompoundNBT> {
	
	public final LazyOptional<ILocationStorage> holder = LazyOptional.of(() -> this);

	public CapabilityLocationStorage(int size) {
		// avoids null errors
		for (int i = 0; i < size; i++) {
			locations.add(new Location(0, 0, 0));
		}
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == VoltaicCapabilities.CAPABILITY_LOCATIONSTORAGE_ITEM) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		if (VoltaicCapabilities.CAPABILITY_LOCATIONSTORAGE_ITEM != null) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("size", locations.size());
			for (int i = 0; i < locations.size(); i++) {
				locations.get(i).writeToNBT(nbt, VoltaicCapabilities.LOCATION_KEY + i);
			}
			return nbt;
		}
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (VoltaicCapabilities.CAPABILITY_LOCATIONSTORAGE_ITEM != null) {
			locations.clear();
			for (int i = 0; i < nbt.getInt("size"); i++) {
				locations.add(Location.readFromNBT(nbt, VoltaicCapabilities.LOCATION_KEY + i));
			}
		}
	}

	private final List<Location> locations = new ArrayList<>();

	@Override
	public void setLocation(int index, double x, double y, double z) {
		locations.set(index, new Location(x, y, z));
	}

	@Override
	public Location getLocation(int index) {
		return locations.get(index);
	}

	@Override
	public void addLocation(double x, double y, double z) {
		locations.add(new Location(x, y, z));
	}

	@Override
	public void removeLocation(Location location) {
		locations.remove(location);
	}

	@Override
	public void clearLocations() {
		locations.clear();
	}

	@Override
	public List<Location> getLocations() {
		return locations;
	}

	@Override
	public void setLocations(List<Location> locations) {
		locations.clear();
		locations.addAll(locations);
	}

}