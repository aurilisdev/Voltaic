package voltaic.registers;

import voltaic.Voltaic;
import voltaic.api.radiation.EffectRadiation;
import voltaic.api.radiation.EffectRadiationResistance;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Voltaic.ID);

	public static final DeferredHolder<MobEffect, EffectRadiation> RADIATION = EFFECTS.register("radiation", EffectRadiation::new);
	public static final DeferredHolder<MobEffect, EffectRadiationResistance> RADIATION_RESISTANCE = EFFECTS.register("radiationresistance", EffectRadiationResistance::new);

}
