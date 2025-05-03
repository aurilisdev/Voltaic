package voltaic.api.electricity.formatting;

import voltaic.prefab.utilities.VoltaicTextUtils;

/**
 * An enum is simpler, however doing it this way lets us add custom measurement units in addon mods without having to update the lib mod
 */
public class MeasurementUnits {

    public static final MeasurementUnit PICO = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.pico.name"), VoltaicTextUtils.gui("measurementunit.pico.symbol"), 1.0E-12D);
    public static final MeasurementUnit NANO = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.nano.name"), VoltaicTextUtils.gui("measurementunit.nano.symbol"), 1.0E-9D);
    public static final MeasurementUnit MICRO = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.micro.name"), VoltaicTextUtils.gui("measurementunit.micro.symbol"), 1.0E-6D);
    public static final MeasurementUnit MILLI = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.milli.name"), VoltaicTextUtils.gui("measurementunit.milli.symbol"), 1.0E-3D);
    public static final MeasurementUnit NONE = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.none.name"), VoltaicTextUtils.gui("measurementunit.none.symbol"), 1.0);
    public static final MeasurementUnit KILO = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.kilo.name"), VoltaicTextUtils.gui("measurementunit.kilo.symbol"), 1.0E3D);
    public static final MeasurementUnit MEGA = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.mega.name"), VoltaicTextUtils.gui("measurementunit.mega.symbol"), 1.0E6D);
    public static final MeasurementUnit GIGA = new MeasurementUnit(VoltaicTextUtils.gui("measurementunit.giga.name"), VoltaicTextUtils.gui("measurementunit.giga.symbol"), 1.0E9D);

}