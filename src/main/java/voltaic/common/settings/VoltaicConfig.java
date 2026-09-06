package voltaic.common.settings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class VoltaicConfig {
    public static final VoltaicConfig INSTANCE = new VoltaicConfig();

    public ModConfigSpec.BooleanValue DISPENSE_GUIDEBOOK;
    public ModConfigSpec.DoubleValue BACKGROUND_RADIATION_DISSIPATION;
    public ModConfigSpec.DoubleValue IODINE_RESISTANCE_THRESHOLD;
    public ModConfigSpec.DoubleValue IODINE_RAD_REDUCTION;
    public ModConfigSpec.BooleanValue RADIATION_SYSTEM_ENABLED;
    public ModConfigSpec.BooleanValue ORES_EMIT_RADIATION;
    public ModConfigSpec.IntValue ORE_RADIATION_ADMIT_RATE;
    public ModConfigSpec SPEC;

    private VoltaicConfig() {
	var builder = new ModConfigSpec.Builder();

	builder.push("common");
	DISPENSE_GUIDEBOOK = builder.comment("Whether guidebook should be dispensed").define("dispenseGuidebook", true);
	builder.pop();

	builder.push("radiation");
	BACKGROUND_RADIATION_DISSIPATION = builder.defineInRange("backgroundRadiationDissipation", 1, 0,
		Double.MAX_VALUE);
	IODINE_RESISTANCE_THRESHOLD = builder.defineInRange("iodineResistanceThreshold", 300, 0, Double.MAX_VALUE);
	IODINE_RAD_REDUCTION = builder.defineInRange("iodineRadreduction", 0.8, 1, Double.MAX_VALUE);
	RADIATION_SYSTEM_ENABLED = builder.define("radiationSystemEnabled", true);
	ORES_EMIT_RADIATION = builder.comment("Whether ores like Uranium emit radiation").define("oresEmitRadiation",
		true);
	ORE_RADIATION_ADMIT_RATE = builder
		.comment("How frequently ores random tick radiation. Value of 1 is fastest rate.")
		.defineInRange("oreRadiationEmitRate", 20, 1, Integer.MAX_VALUE);

	builder.pop();
	SPEC = builder.build();
    }
}
