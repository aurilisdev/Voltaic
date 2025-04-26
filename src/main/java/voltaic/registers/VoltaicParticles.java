package voltaic.registers;

import voltaic.Voltaic;
import voltaic.client.particle.fluiddrop.ParticleOptionFluidDrop;
import voltaic.client.particle.lavawithphysics.ParticleOptionLavaWithPhysics;
import voltaic.client.particle.plasmaball.ParticleOptionPlasmaBall;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Voltaic.ID);

	public static final DeferredHolder<ParticleType<?>, ParticleOptionPlasmaBall> PARTICLE_PLASMA_BALL = PARTICLES.register("plasmaball", ParticleOptionPlasmaBall::new);
	public static final DeferredHolder<ParticleType<?>, ParticleOptionLavaWithPhysics> PARTICLE_LAVAWITHPHYSICS = PARTICLES.register("lavawithphysics", ParticleOptionLavaWithPhysics::new);
	public static final DeferredHolder<ParticleType<?>, ParticleOptionFluidDrop> PARTICLE_FLUIDDROP = PARTICLES.register("fluiddrop", ParticleOptionFluidDrop::new);

}
