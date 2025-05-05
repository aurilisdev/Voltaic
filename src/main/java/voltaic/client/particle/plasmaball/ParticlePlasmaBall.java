package voltaic.client.particle.plasmaball;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.particle.ParticleManager.IParticleMetaFactory;
import net.minecraft.client.world.ClientWorld;

public class ParticlePlasmaBall extends SpriteTexturedParticle {

	private final IAnimatedSprite sprites;

	public ParticlePlasmaBall(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionPlasmaBall options, IAnimatedSprite sprites) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.sprites = sprites;
		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.gravity = options.gravity;
		float f = this.random.nextFloat() * 0.4F + 0.6F;
		this.rCol = this.randomizeColor(options.r / 255.0F, f);
		this.gCol = this.randomizeColor(options.g / 255.0F, f);
		this.bCol = this.randomizeColor(options.b / 255.0F, f);
		this.alpha = options.a / 255.0F;
		this.quadSize *= 0.75F * options.scale;
		this.lifetime = options.maxAge;
		this.setSpriteFromAge(sprites);
		this.roll = 0.1F;
	}

	public float randomizeColor(float pCoordMultiplier, float pMultiplier) {
		return (this.random.nextFloat() * 0.2F + 0.8F) * pCoordMultiplier * pMultiplier;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory implements IParticleFactory<ParticleOptionPlasmaBall>, IParticleMetaFactory<ParticleOptionPlasmaBall> {

		private final IAnimatedSprite sprites;

		public Factory(IAnimatedSprite sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(ParticleOptionPlasmaBall type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticlePlasmaBall(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
		}

		@Override
		public IParticleFactory<ParticleOptionPlasmaBall> create(IAnimatedSprite sprites) {
			return new Factory(sprites);
		}

	}

}
