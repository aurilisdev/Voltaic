package voltaic.api.electricity.formatting;

import net.minecraft.network.chat.Component;
import voltaic.prefab.utilities.VoltaicTextUtils;

/**
 * An enum is simpler, however doing it this way lets us add custom measurement units in addon mods without having to update the lib mod
 */
public enum MeasurementUnits implements IMeasurementUnit {

	PICO(VoltaicTextUtils.gui("measurementunit.pico.name"), VoltaicTextUtils.gui("measurementunit.pico.symbol"), 1.0E-12D),
    NANO(VoltaicTextUtils.gui("measurementunit.nano.name"), VoltaicTextUtils.gui("measurementunit.nano.symbol"), 1.0E-9D),
    MICRO(VoltaicTextUtils.gui("measurementunit.micro.name"), VoltaicTextUtils.gui("measurementunit.micro.symbol"), 1.0E-6D),
    MILLI(VoltaicTextUtils.gui("measurementunit.milli.name"), VoltaicTextUtils.gui("measurementunit.milli.symbol"), 1.0E-3D),
    NONE(VoltaicTextUtils.gui("measurementunit.none.name"), VoltaicTextUtils.gui("measurementunit.none.symbol"), 1.0),
    KILO(VoltaicTextUtils.gui("measurementunit.kilo.name"), VoltaicTextUtils.gui("measurementunit.kilo.symbol"), 1.0E3D),
    MEGA(VoltaicTextUtils.gui("measurementunit.mega.name"), VoltaicTextUtils.gui("measurementunit.mega.symbol"), 1.0E6D),
    GIGA(VoltaicTextUtils.gui("measurementunit.giga.name"), VoltaicTextUtils.gui("measurementunit.giga.symbol"), 1.0E9D);

    private final double value;
    private final Component symbol;
    private final Component name;

    private MeasurementUnits(Component name, Component symbol, double value) {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Component getSymbol() {
        return symbol;
    }
    
    @Override
    public Component getName() {
        return name;
    }

}