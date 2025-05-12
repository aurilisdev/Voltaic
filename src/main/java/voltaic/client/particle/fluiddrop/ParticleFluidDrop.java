package voltaic.client.particle.fluiddrop;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.particle.ParticleManager.IParticleMetaFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class ParticleFluidDrop extends SpriteTexturedParticle {

    private final IAnimatedSprite sprites;

    public ParticleFluidDrop(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleOptionFluidDrop options, IAnimatedSprite set) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        sprites = set;
        this.xd = 0;
        this.yd = ySpeed;
        this.zd = 0;
        //this.quadSize = this.quadSize * 0.75F * options.scale;
        setSpriteFromAge(sprites);
        setColor(options.r, options.g, options.b);
        int i = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.lifetime = (int)Math.max(i * options.scale, 1.0F);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * MathHelper.clamp((this.age + scaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class Factory implements IParticleFactory<ParticleOptionFluidDrop>, IParticleMetaFactory<ParticleOptionFluidDrop> {

        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ParticleOptionFluidDrop type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleFluidDrop(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites);
        }

        @Override
        public IParticleFactory<ParticleOptionFluidDrop> create(IAnimatedSprite sprites) {
            return new ParticleFluidDrop.Factory(sprites);
        }

    }
}
