package voltaic.common.condition;

import com.mojang.serialization.MapCodec;

import voltaic.common.settings.VoltaicConstants;
import net.neoforged.neoforge.common.conditions.ICondition;

public class ConfigCondition implements ICondition {

    public static final ConfigCondition INSTANCE = new ConfigCondition();
	
	public static final MapCodec<ConfigCondition> CODEC = MapCodec.unit(INSTANCE).stable();

	public ConfigCondition() {
	    
	}

	@Override
	public boolean test(IContext context) {
		return VoltaicConstants.DISPENSE_GUIDEBOOK;
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
