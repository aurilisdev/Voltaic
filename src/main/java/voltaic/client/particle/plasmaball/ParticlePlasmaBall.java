package voltaic.client.particle.plasmaball;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ParticlePlasmaBall extends TextureSheetParticle {

    private final SpriteSet sprites;

    public ParticlePlasmaBall(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
	    double zSpeed, ParticleOptionPlasmaBall options, SpriteSet sprites) {
	super(level, x, y, z, xSpeed, ySpeed, zSpeed);
	friction = 0.96F;
	speedUpWhenYMotionIsBlocked = true;
	this.sprites = sprites;
	xd *= 0.1F;
	yd *= 0.1F;
	zd *= 0.1F;
	gravity = options.gravity;
	float f = random.nextFloat() * 0.4F + 0.6F;
	rCol = randomizeColor(options.r / 255.0F, f);
	gCol = randomizeColor(options.g / 255.0F, f);
	bCol = randomizeColor(options.b / 255.0F, f);
	alpha = options.a / 255.0F;
	quadSize *= 0.75F * options.scale;
	lifetime = options.maxAge;
	setSpriteFromAge(sprites);
	roll = 0.1F;
    }

    public float randomizeColor(float pCoordMultiplier, float pMultiplier) {
	return (random.nextFloat() * 0.2F + 0.8F) * pCoordMultiplier * pMultiplier;
    }

    @Override
    public void tick() {
	super.tick();
	setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
	return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<ParticleOptionPlasmaBall>,
	    SpriteParticleRegistration<ParticleOptionPlasmaBall> {

	private final SpriteSet sprites;

	public Factory(SpriteSet sprites) {
	    this.sprites = sprites;
	}

	@Override
	public Particle createParticle(ParticleOptionPlasmaBall type, ClientLevel level, double x, double y, double z,
		double xSpeed, double ySpeed, double zSpeed) {
	    return new ParticlePlasmaBall(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
	}

	@Override
	public ParticleProvider<ParticleOptionPlasmaBall> create(SpriteSet sprites) {
	    return new Factory(sprites);
	}

    }

}
