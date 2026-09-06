package voltaic.client.particle.lavawithphysics;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;

public class ParticleLavaWithPhysics extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final double bounceFactor;

    public ParticleLavaWithPhysics(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
	    double zSpeed, ParticleOptionLavaWithPhysics options, SpriteSet sprites) {
	super(level, x, y, z, xSpeed, ySpeed, zSpeed);
	this.sprites = sprites;
	gravity = 0.75F;
	friction = 0.999F;
	hasPhysics = true;
	bounceFactor = options.bounceFactor;
	xd = xSpeed;
	yd = ySpeed;
	zd = zSpeed;
	quadSize = options.scale;
	lifetime = options.lifetime;
	setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
	return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
	int i = super.getLightColor(partialTick);
	int k = i >> 16 & 0xFF;
	return 240 | k << 16;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
	float f = (age + scaleFactor) / lifetime;
	return quadSize * (1.0F - f * f);
    }

    @Override
    public void tick() {
	super.tick();
	setSpriteFromAge(sprites);
	if (!removed) {
	    if (stoppedByCollision) {
		xd = -xd * bounceFactor;
		yd = -yd * bounceFactor;
		zd = -zd * bounceFactor;
		stoppedByCollision = false;
	    }

	    float f = (float) age / (float) lifetime;
	    if (random.nextFloat() > f) {
		level.addParticle(ParticleTypes.SMOKE, x, y, z, xd, yd, zd);
	    }
	}
    }

    public static class Factory implements ParticleProvider<ParticleOptionLavaWithPhysics>,
	    ParticleEngine.SpriteParticleRegistration<ParticleOptionLavaWithPhysics> {

	private final SpriteSet sprites;

	public Factory(SpriteSet sprites) {
	    this.sprites = sprites;
	}

	@Override
	public Particle createParticle(ParticleOptionLavaWithPhysics type, ClientLevel level, double x, double y,
		double z, double xSpeed, double ySpeed, double zSpeed) {
	    return new ParticleLavaWithPhysics(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
	}

	@Override
	public ParticleProvider<ParticleOptionLavaWithPhysics> create(SpriteSet sprites) {
	    return new ParticleLavaWithPhysics.Factory(sprites);
	}

    }
}
