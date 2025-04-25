package voltaic.registers;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voltaic.Voltaic;
import voltaic.api.radiation.EffectRadiation;
import voltaic.api.radiation.EffectRadiationResistance;

public class VoltaicEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Voltaic.ID);

	public static final RegistryObject<EffectRadiation> RADIATION = EFFECTS.register("radiation", EffectRadiation::new);
	public static final RegistryObject<EffectRadiationResistance> RADIATION_RESISTANCE = EFFECTS.register("radiationresistance", EffectRadiationResistance::new);

}
