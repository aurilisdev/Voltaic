package voltaic.client.particle.fluiddrop;

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

public class ParticleOptionFluidDrop extends ParticleType<ParticleOptionFluidDrop> implements IParticleData {

    public static final Codec<ParticleOptionFluidDrop> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(instance0 -> instance0.r),
                    Codec.FLOAT.fieldOf("g").forGetter(instance0 -> instance0.g),
                    Codec.FLOAT.fieldOf("b").forGetter(instance0 -> instance0.b),
                    Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> instance0.scale)
            ).apply(instance, (r, g, b, scale) -> new ParticleOptionFluidDrop().setParameters(r, g, b, scale)));

    public static final StreamCodec<PacketBuffer, ParticleOptionFluidDrop> STREAM_CODEC = new StreamCodec<PacketBuffer, ParticleOptionFluidDrop>() {
		
		@Override
		public void encode(PacketBuffer buffer, ParticleOptionFluidDrop value) {
			buffer.writeFloat(value.r);
			buffer.writeFloat(value.g);
			buffer.writeFloat(value.b);
			buffer.writeFloat(value.scale);
		}
		
		@Override
		public ParticleOptionFluidDrop decode(PacketBuffer buffer) {
			return new ParticleOptionFluidDrop().setParameters(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		}
	};
	
	public static final IParticleData.IDeserializer<ParticleOptionFluidDrop> DESERIALIZER = new IParticleData.IDeserializer<ParticleOptionFluidDrop>() {

		@Override
		public ParticleOptionFluidDrop fromCommand(ParticleType<ParticleOptionFluidDrop> pParticleType, StringReader reader) throws CommandSyntaxException {
			ParticleOptionFluidDrop particle = new ParticleOptionFluidDrop();

			reader.expect(' ');
			float r = reader.readFloat();

			reader.expect(' ');
			float g = reader.readFloat();

			reader.expect(' ');
			float b = reader.readFloat();

			reader.expect(' ');
			float scale = reader.readFloat();

			return particle.setParameters(r, g, b, scale);
		}

		@Override
		public ParticleOptionFluidDrop fromNetwork(ParticleType<ParticleOptionFluidDrop> pParticleType, PacketBuffer pBuffer) {
			return STREAM_CODEC.decode(pBuffer);
		}
	};

    public float r;
    public float g;
    public float b;

    public float scale;

    public ParticleOptionFluidDrop() {
        super(false, DESERIALIZER);
    }

    public ParticleOptionFluidDrop setParameters(float r, float g, float b, float scale) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.scale = scale;
        return this;
    }


    @Override
    public ParticleType<?> getType() {
        return VoltaicParticles.PARTICLE_FLUIDDROP.get();
    }

	@Override
	public void writeToNetwork(PacketBuffer pBuffer) {
		STREAM_CODEC.encode(pBuffer, this);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", r: " + r + ", g: " + g + ", b: " + b + ", scale: " + scale;
	}

	@Override
	public Codec<ParticleOptionFluidDrop> codec() {
		return CODEC;
	}
}
