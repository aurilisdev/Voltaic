package voltaic.api.electricity.formatting;

import voltaic.prefab.utilities.VoltaicTextUtils;

public class DisplayUnits {

    public static final DisplayUnit AMPERE = new DisplayUnit(VoltaicTextUtils.gui("displayunit.ampere.name"), VoltaicTextUtils.gui("displayunit.ampere.nameplural"), VoltaicTextUtils.gui("displayunit.ampere.symbol"));

    public static final DisplayUnit AMP_HOUR = new DisplayUnit(VoltaicTextUtils.gui("displayunit.amphour.name"), VoltaicTextUtils.gui("displayunit.amphour.nameplural"), VoltaicTextUtils.gui("displayunit.amphour.symbol"));

    public static final DisplayUnit VOLTAGE = new DisplayUnit(VoltaicTextUtils.gui("displayunit.voltage.name"), VoltaicTextUtils.gui("displayunit.voltage.nameplural"), VoltaicTextUtils.gui("displayunit.voltage.symbol"));

    public static final DisplayUnit WATT = new DisplayUnit(VoltaicTextUtils.gui("displayunit.watt.name"), VoltaicTextUtils.gui("displayunit.watt.nameplural"), VoltaicTextUtils.gui("displayunit.watt.symbol"));

    public static final DisplayUnit WATT_HOUR = new DisplayUnit(VoltaicTextUtils.gui("displayunit.watthour.name"), VoltaicTextUtils.gui("displayunit.watthour.nameplural"), VoltaicTextUtils.gui("displayunit.watthour.symbol"));

    public static final DisplayUnit RESISTANCE = new DisplayUnit(VoltaicTextUtils.gui("displayunit.resistance.name"), VoltaicTextUtils.gui("displayunit.resistance.nameplural"), VoltaicTextUtils.gui("displayunit.resistance.symbol"));

    public static final DisplayUnit CONDUCTANCE = new DisplayUnit(VoltaicTextUtils.gui("displayunit.conductance.name"), VoltaicTextUtils.gui("displayunit.conductance.nameplural"), VoltaicTextUtils.gui("displayunit.conductance.symbol"));

    public static final DisplayUnit JOULES = new DisplayUnit(VoltaicTextUtils.gui("displayunit.joules.name"), VoltaicTextUtils.gui("displayunit.joules.nameplural"), VoltaicTextUtils.gui("displayunit.joules.symbol"));

    public static final DisplayUnit BUCKETS = new DisplayUnit(VoltaicTextUtils.gui("displayunit.buckets.name"), VoltaicTextUtils.gui("displayunit.buckets.nameplural"), VoltaicTextUtils.gui("displayunit.buckets.symbol"));

    public static final DisplayUnit TEMPERATURE_KELVIN = new DisplayUnit(VoltaicTextUtils.gui("displayunit.tempkelvin.name"), VoltaicTextUtils.gui("displayunit.tempkelvin.nameplural"), VoltaicTextUtils.gui("displayunit.tempkelvin.symbol"));

    public static final DisplayUnit TEMPERATURE_CELCIUS = new DisplayUnit(VoltaicTextUtils.gui("displayunit.tempcelcius.name"), VoltaicTextUtils.gui("displayunit.tempcelcius.nameplural"), VoltaicTextUtils.gui("displayunit.tempcelcius.symbol"));

    public static final DisplayUnit TEMPERATURE_FAHRENHEIT = new DisplayUnit(VoltaicTextUtils.gui("displayunit.tempfahrenheit.name"), VoltaicTextUtils.gui("displayunit.tempfahrenheit.nameplural"), VoltaicTextUtils.gui("displayunit.tempfahrenheit.symbol"));

    public static final DisplayUnit TIME_SECONDS = new DisplayUnit(VoltaicTextUtils.gui("displayunit.timeseconds.name"), VoltaicTextUtils.gui("displayunit.timeseconds.nameplural"), VoltaicTextUtils.gui("displayunit.timeseconds.symbol"));

    public static final DisplayUnit TIME_TICKS = new DisplayUnit(VoltaicTextUtils.gui("displayunit.timeticks.name"), VoltaicTextUtils.gui("displayunit.timeticks.nameplural"), VoltaicTextUtils.gui("displayunit.timeticks.symbol"));

    public static final DisplayUnit PRESSURE_ATM = new DisplayUnit(VoltaicTextUtils.gui("displayunit.pressureatm.name"), VoltaicTextUtils.gui("displayunit.pressureatm.nameplural"), VoltaicTextUtils.gui("displayunit.pressureatm.symbol"));

    public static final DisplayUnit PERCENTAGE = new DisplayUnit(VoltaicTextUtils.gui("displayunit.percentage.name"), VoltaicTextUtils.gui("displayunit.percentage.nameplural"), VoltaicTextUtils.gui("displayunit.percentage.symbol"), VoltaicTextUtils.empty());

    public static final DisplayUnit FORGE_ENERGY_UNIT = new DisplayUnit(VoltaicTextUtils.gui("displayunit.forgeenergyunit.name"), VoltaicTextUtils.gui("displayunit.forgeenergyunit.nameplural"), VoltaicTextUtils.gui("displayunit.forgeenergyunit.symbol"));

    public static final DisplayUnit RAD = new DisplayUnit(VoltaicTextUtils.gui("displayunit.radname"), VoltaicTextUtils.gui("displayunit.radnameplural"), VoltaicTextUtils.gui("displayunit.radsymbol"));


}
