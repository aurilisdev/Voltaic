package voltaic.api.sound;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class DistanceSound extends AbstractTickableSoundInstance {
    private final BlockPos blockPos;

    @Nullable
    private Block block;

    public DistanceSound(SoundEvent sound, SoundSource source, float volume, float pitch, BlockPos pos) {
	super(sound, source, RandomSource.create());

	blockPos = pos.immutable();
	this.volume = volume;
	this.pitch = pitch;
	x = pos.getX();
	y = pos.getY();
	z = pos.getZ();
	attenuation = Attenuation.LINEAR;

	ClientLevel level = Minecraft.getInstance().level;
	if (level != null) {
	    block = level.getBlockState(blockPos).getBlock();
	}
    }

    @Override
    public float getVolume() {
	Minecraft minecraft = Minecraft.getInstance();
	ClientLevel level = minecraft.level;
	Player player = minecraft.player;
	if (block == null || level == null || player == null)
	    return 0.0F;

	double distance = player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5);
	if (distance > 16 * 16)
	    return 0.0F;

	return super.getVolume() * (float) Math.min(1.0D, 1.0D / Math.sqrt(distance));
    }

    @Override
    public void tick() {
    }
}