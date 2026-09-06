package voltaic.client.particle.plasmaball;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import voltaic.prefab.utilities.CodecUtils;
import voltaic.registers.VoltaicParticles;

public class ParticleOptionPlasmaBall extends ParticleType<ParticleOptionPlasmaBall> implements ParticleOptions {
    public float scale;
    public float gravity;
    public int maxAge;
    public int r;
    public int g;
    public int b;
    public int a;

    public static final MapCodec<ParticleOptionPlasmaBall> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
	    .group(Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
		    Codec.FLOAT.fieldOf("gravity").forGetter(instance0 -> instance0.gravity),
		    Codec.INT.fieldOf("maxage").forGetter(instance0 -> instance0.maxAge),
		    Codec.INT.fieldOf("r").forGetter(instance0 -> instance0.r),
		    Codec.INT.fieldOf("g").forGetter(instance0 -> instance0.g),
		    Codec.INT.fieldOf("b").forGetter(instance0 -> instance0.b),
		    Codec.INT.fieldOf("a").forGetter(instance0 -> instance0.a))
	    .apply(instance, (scale, gravity, age, r, g, b, a) -> new ParticleOptionPlasmaBall().setParameters(scale,
		    gravity, age, r, g, b, a)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptionPlasmaBall> STREAM_CODEC = CodecUtils
	    .composite(ByteBufCodecs.FLOAT, instance0 -> instance0.scale, ByteBufCodecs.FLOAT,
		    instance0 -> instance0.gravity, ByteBufCodecs.INT, instance0 -> instance0.maxAge, ByteBufCodecs.INT,
		    instance0 -> instance0.r, ByteBufCodecs.INT, instance0 -> instance0.g, ByteBufCodecs.INT,
		    instance0 -> instance0.b, ByteBufCodecs.INT, instance0 -> instance0.a, (scale, gravity, age, r, g,
			    b, a) -> new ParticleOptionPlasmaBall().setParameters(scale, gravity, age, r, g, b, a));

    public ParticleOptionPlasmaBall() {
	super(false);
    }

    public ParticleOptionPlasmaBall setParameters(float scale, float gravity, int maxAge, int r, int g, int b, int a) {
	this.scale = scale;
	this.gravity = gravity;
	this.maxAge = maxAge;
	this.r = r;
	this.g = g;
	this.b = b;
	this.a = a;
	return this;
    }

    @Override
    public ParticleType<?> getType() {
	return VoltaicParticles.PARTICLE_PLASMA_BALL.get();
    }

    @Override
    public String toString() {
	ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(getType());

	return (key == null ? "unregistered" : key.toString()) + ", scale: " + scale + ", gravity: " + gravity
		+ ", maxage: " + maxAge + ", r: " + r + ", g: " + g + ", b: " + b + ", a: " + a;
    }

    @Override
    public MapCodec<ParticleOptionPlasmaBall> codec() {
	return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptionPlasmaBall> streamCodec() {
	return STREAM_CODEC;
    }

}
