package voltaic.api.radiation;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import voltaic.prefab.utilities.math.Color;

public class EffectRadiationResistance extends Effect {

    public static final Color COLOR = new Color(255, 251, 245, 255);

    public EffectRadiationResistance(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public EffectRadiationResistance() {
        this(EffectType.HARMFUL, COLOR.color());
    }
    
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
    	return true;
    }

}
