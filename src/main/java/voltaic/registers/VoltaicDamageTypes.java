package voltaic.registers;

import voltaic.Voltaic;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class VoltaicDamageTypes {

	public static final ResourceKey<DamageType> ELECTRICITY = create("electricity");
	public static final ResourceKey<DamageType> RADIATION = create("radiation");

	public static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, Voltaic.rl(name));
	}

	public static void registerTypes(BootstapContext<DamageType> context) {
		context.register(ELECTRICITY, new DamageType("electricity", DamageScaling.NEVER, 0, DamageEffects.HURT));
		context.register(RADIATION, new DamageType("radiation", DamageScaling.NEVER, 0.1F, DamageEffects.HURT));
	}

}
