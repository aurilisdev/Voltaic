package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public record RadiationShielding(double transmission, double level) {
    public static final RadiationShielding NONE = new RadiationShielding(1.0, 0);

    public static final Codec<RadiationShielding> CODEC = RecordCodecBuilder.create(instance -> instance
	    .group(Codec.DOUBLE.fieldOf("transmission").forGetter(RadiationShielding::transmission),
		    Codec.DOUBLE.fieldOf("level").forGetter(RadiationShielding::level))
	    .apply(instance, RadiationShielding::new));

    public static final StreamCodec<ByteBuf, RadiationShielding> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static RadiationShielding getDefault(BlockGetter world, BlockPos pos, BlockState state) {
	if (state.isAir() || state.getCollisionShape(world, pos).isEmpty()) {
	    return RadiationShielding.NONE;
	}

	double hardness = state.getDestroySpeed(world, pos);

	if (hardness <= 0 || !Double.isFinite(hardness)) {
	    return RadiationShielding.NONE;
	}

	double blocked = Math.min(0.20, Math.log1p(hardness) * 0.04);
	double transmission = 1.0 - blocked;

	return new RadiationShielding(transmission, 1.0);
    }
}
