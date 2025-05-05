package voltaic.registers;

import net.minecraft.potion.Effect;
import voltaic.Voltaic;
import voltaic.api.radiation.EffectRadiation;
import voltaic.api.radiation.EffectRadiationResistance;

public class VoltaicEffects {

	public static final Effect RADIATION = new EffectRadiation().setRegistryName(Voltaic.ID, "radiation");
	public static final Effect RADIATION_RESISTANCE = new EffectRadiationResistance().setRegistryName(Voltaic.ID, "radiationresistance");

}
