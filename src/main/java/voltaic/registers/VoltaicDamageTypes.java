package voltaic.registers;

import net.minecraft.util.DamageSource;

public class VoltaicDamageTypes {

	public static final DamageSource ELECTRICITY = new DamageSource("electricity").bypassArmor().setMagic();
	public static final DamageSource RADIATION = new DamageSource("radiation").bypassArmor().setMagic();

}
