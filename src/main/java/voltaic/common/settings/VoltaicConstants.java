package voltaic.common.settings;

import voltaic.api.configuration.BooleanValue;
import voltaic.api.configuration.Configuration;
import voltaic.api.configuration.DoubleValue;

@Configuration(name = "Voltaic")
public class VoltaicConstants {
	@BooleanValue(def = true)
	public static boolean DISPENSE_GUIDEBOOK = true;
	@DoubleValue(def = 1)
	public static double BACKROUND_RADIATION_DISSIPATION = 1;
	@DoubleValue(def = 300)
	public static double IODINE_RESISTANCE_THRESHHOLD = 300;
	@DoubleValue(def = 0.8)
	public static double IODINE_RAD_REDUCTION = 0.8;
	@BooleanValue(def = true)
	public static boolean RADIATION_SYSTEM_ENABLED = true;
	@BooleanValue(def = true, comment = "Whether ores like Uranium will emit radiation")
	public static boolean ORES_EMIT_RADIATION = true;

}
