package voltaic.client.particle.lavawithphysics;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import voltaic.api.codec.StreamCodec;
import voltaic.registers.VoltaicParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionLavaWithPhysics extends ParticleType<ParticleOptionLavaWithPhysics> implements ParticleOptions {

    public float scale;
    public double bounceFactor;
    public int lifetime;

    public static final Codec<ParticleOptionLavaWithPhysics> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
                    Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
                    Codec.DOUBLE.fieldOf("bouncefactor").forGetter(instance0 -> instance0.bounceFactor)
            ).apply(instance, (scale, lifetime, bounceFactor) -> new ParticleOptionLavaWithPhysics().setParameters(scale, lifetime, bounceFactor)));

    public static final StreamCodec<FriendlyByteBuf, ParticleOptionLavaWithPhysics> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, ParticleOptionLavaWithPhysics>() {
		
		@Override
		public void encode(FriendlyByteBuf buffer, ParticleOptionLavaWithPhysics value) {
			StreamCodec.FLOAT.encode(buffer, value.scale);
			StreamCodec.INT.encode(buffer, value.lifetime);
			StreamCodec.DOUBLE.encode(buffer, value.bounceFactor);
		}
		
		@Override
		public ParticleOptionLavaWithPhysics decode(FriendlyByteBuf buffer) {
			return new ParticleOptionLavaWithPhysics().setParameters(buffer.readFloat(), buffer.readInt(), buffer.readDouble());
		}
	};
	
	public static final ParticleOptions.Deserializer<ParticleOptionLavaWithPhysics> DESERIALIZER = new ParticleOptions.Deserializer<ParticleOptionLavaWithPhysics>() {

		@Override
		public ParticleOptionLavaWithPhysics fromCommand(ParticleType<ParticleOptionLavaWithPhysics> pParticleType, StringReader reader) throws CommandSyntaxException {
			ParticleOptionLavaWithPhysics particle = new ParticleOptionLavaWithPhysics();

			reader.expect(' ');
			float scale = reader.readFloat();

			reader.expect(' ');
			int lifetime = reader.readInt();

			reader.expect(' ');
			double bounceFactor = reader.readDouble();

			return particle.setParameters(scale, lifetime, bounceFactor);
		}

		@Override
		public ParticleOptionLavaWithPhysics fromNetwork(ParticleType<ParticleOptionLavaWithPhysics> pParticleType, FriendlyByteBuf pBuffer) {
			return STREAM_CODEC.decode(pBuffer);
		}
	};

    public ParticleOptionLavaWithPhysics() {
        super(false, DESERIALIZER);
    }

    public ParticleOptionLavaWithPhysics setParameters(float scale, int lifetime, double bounceFactor) {
        this.scale = scale;
        this.lifetime = lifetime;
        this.bounceFactor = bounceFactor;
        return this;
    }

    @Override
    public ParticleType<?> getType() {
        return VoltaicParticles.PARTICLE_LAVAWITHPHYSICS.get();
    }

	@Override
	public void writeToNetwork(FriendlyByteBuf pBuffer) {
		STREAM_CODEC.encode(pBuffer, this);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", scale: " + scale + ", bounceFactor: " + bounceFactor + ", lifetime: " + lifetime;
	}

	@Override
	public Codec<ParticleOptionLavaWithPhysics> codec() {
		return CODEC;
	}
}
