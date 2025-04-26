package voltaic.api.gas;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import voltaic.registers.VoltaicGases;
import voltaic.registers.VoltaicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Basic implementation of a Gas mirroring certain aspects of fluids
 * 
 * Gases are not designed to be place hence how lightweight it is
 * 
 * @author skip999
 *
 */
public class Gas {

	public static final int ROOM_TEMPERATURE = 293;
	public static final int PRESSURE_AT_SEA_LEVEL = 1;
	public static final int MINIMUM_HEAT_BURN_TEMP = 327;
	public static final int MINIMUM_FREEZE_TEMP = 260;

	private final Supplier<Item> container;
	private final Component description;
	private final int condensationTemp; // Degrees Kelvin; set to 0 if this gas does not condense
	@Nullable
	private final Fluid condensedFluid; // set to empty if gas does not condense

	public Gas(Supplier<Item> container, Component description) {
		this.container = container;
		this.description = description;
		this.condensationTemp = 0;
		this.condensedFluid = Fluids.EMPTY;
	}

	public Gas(Supplier<Item> container, Component description, int condensationTemp, Fluid condensedFluid) {
		this.container = container;
		this.description = description;
		this.condensationTemp = condensationTemp;
		this.condensedFluid = condensedFluid;
		VoltaicGases.MAPPED_GASSES.put(condensedFluid, this);
	}

	public Component getDescription() {
		return description;
	}

	public Item getContainer() {
		return container.get();
	}

	public Holder<Gas> getBuiltInRegistry() {
		return VoltaicRegistries.gasRegistry().getHolder(this).get();
	}

	public boolean isEmpty() {
		return this == VoltaicGases.EMPTY.get();
	}

	public int getCondensationTemp() {
		return condensationTemp;
	}

	public boolean noCondensedFluid() {
		return condensedFluid == null || condensedFluid == Fluids.EMPTY;
	}

	public Fluid getCondensedFluid() {
		return condensedFluid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Gas other) {
			return other == this;
		}
		return false;
	}

	@Override
	public String toString() {
		return description.getString() + ",\tcondensation temp : " + condensationTemp + " K,\tcondensed fluid: " + getCondensedFluid().getFluidType().getDescription().getString();
	}
	
	public boolean doesCondense() {
	    return condensationTemp > 0;
	}

}
