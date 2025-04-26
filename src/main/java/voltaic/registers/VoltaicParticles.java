package voltaic.registers;

import voltaic.Voltaic;
import voltaic.client.particle.fluiddrop.ParticleOptionFluidDrop;
import voltaic.client.particle.lavawithphysics.ParticleOptionLavaWithPhysics;
import voltaic.client.particle.plasmaball.ParticleOptionPlasmaBall;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Voltaic.ID);

	public static final RegistryObject<ParticleOptionPlasmaBall> PARTICLE_PLASMA_BALL = PARTICLES.register("plasmaball", ParticleOptionPlasmaBall::new);
	public static final RegistryObject<ParticleOptionLavaWithPhysics> PARTICLE_LAVAWITHPHYSICS = PARTICLES.register("lavawithphysics", ParticleOptionLavaWithPhysics::new);
	public static final RegistryObject<ParticleOptionFluidDrop> PARTICLE_FLUIDDROP = PARTICLES.register("fluiddrop", ParticleOptionFluidDrop::new);

}
