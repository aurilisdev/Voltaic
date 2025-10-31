package voltaic.common.condition;

import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.common.conditions.ICondition;
import voltaic.common.settings.VoltaicConfig;

public class ConfigCondition implements ICondition {

    public static final ConfigCondition INSTANCE = new ConfigCondition();
	
	public static final MapCodec<ConfigCondition> CODEC = MapCodec.unit(INSTANCE).stable();

	public ConfigCondition() {
	    
	}

	@Override
	public boolean test(IContext context) {
		return VoltaicConfig.INSTANCE.DISPENSE_GUIDEBOOK.isTrue();
	}

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
    
    @Override
    public String toString() {
        return "Guidebook toggle config";
    }
}
