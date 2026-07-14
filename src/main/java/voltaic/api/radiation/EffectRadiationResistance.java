package voltaic.api.radiation;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import voltaic.prefab.utilities.math.Color;

public class EffectRadiationResistance extends MobEffect {

    public static final Color COLOR = new Color(255, 251, 245, 255);

    public EffectRadiationResistance(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public EffectRadiationResistance() {
        this(MobEffectCategory.HARMFUL, COLOR.color());
    }
    
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
    	return true;
    }

}
