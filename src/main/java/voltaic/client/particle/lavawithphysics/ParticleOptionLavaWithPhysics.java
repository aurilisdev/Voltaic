package voltaic.client.particle.lavawithphysics;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import voltaic.api.codec.StreamCodec;
import voltaic.registers.VoltaicParticles;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionLavaWithPhysics extends ParticleType<ParticleOptionLavaWithPhysics> implements IParticleData {

    public float scale;
    public double bounceFactor;
    public int lifetime;

    public static final Codec<ParticleOptionLavaWithPhysics> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale),
                    Codec.INT.fieldOf("lifetime").forGetter(instance0 -> instance0.lifetime),
                    Codec.DOUBLE.fieldOf("bouncefactor").forGetter(instance0 -> instance0.bounceFactor)
            ).apply(instance, (scale, lifetime, bounceFactor) -> new ParticleOptionLavaWithPhysics().setParameters(scale, lifetime, bounceFactor)));

    public static final StreamCodec<PacketBuffer, ParticleOptionLavaWithPhysics> STREAM_CODEC = new StreamCodec<PacketBuffer, ParticleOptionLavaWithPhysics>() {
		
		@Override
		public void encode(PacketBuffer buffer, ParticleOptionLavaWithPhysics value) {
			StreamCodec.FLOAT.encode(buffer, value.scale);
			StreamCodec.INT.encode(buffer, value.lifetime);
			StreamCodec.DOUBLE.encode(buffer, value.bounceFactor);
		}
		
		@Override
		public ParticleOptionLavaWithPhysics decode(PacketBuffer buffer) {
			return new ParticleOptionLavaWithPhysics().setParameters(buffer.readFloat(), buffer.readInt(), buffer.readDouble());
		}
	};
	
	public static final IParticleData.IDeserializer<ParticleOptionLavaWithPhysics> DESERIALIZER = new IParticleData.IDeserializer<ParticleOptionLavaWithPhysics>() {

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
		public ParticleOptionLavaWithPhysics fromNetwork(ParticleType<ParticleOptionLavaWithPhysics> pParticleType, PacketBuffer pBuffer) {
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
	public void writeToNetwork(PacketBuffer pBuffer) {
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
