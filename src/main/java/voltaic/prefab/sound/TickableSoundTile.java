package voltaic.prefab.sound;

import voltaic.prefab.utilities.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class TickableSoundTile<T extends TileEntity & ITickableSound> extends TickableSound {

	// Yes it's weird, but I couldn't think of a better way
    public static final double MAXIMUM_DISTANCE = 10;

    protected final T tile;
    protected final float initialVolume;
    protected final double range;
    
    public TickableSoundTile(SoundEvent event, T tile, double range, boolean repeat) {
        this(event, SoundCategory.BLOCKS, tile, 0.5F, 1.0F, range, repeat);
    }

    public TickableSoundTile(SoundEvent event, T tile, boolean repeat) {
    	this(event, SoundCategory.BLOCKS, tile, 0.5F, 1.0F, MAXIMUM_DISTANCE, repeat);
    }
    
    public TickableSoundTile(SoundEvent event, SoundCategory source, T tile, float volume, float pitch, boolean repeat) {
        this(event, source, tile, volume, pitch, MAXIMUM_DISTANCE, repeat);
    }

    public TickableSoundTile(SoundEvent event, SoundCategory source, T tile, float volume, float pitch, double range, boolean repeat) {
        super(event, source);
        this.tile = tile;
        this.volume = volume;
        this.pitch = pitch;
        x = tile.getBlockPos().getX();
        y = tile.getBlockPos().getY();
        z = tile.getBlockPos().getZ();
        looping = repeat;
        initialVolume = volume;
        this.range = range;
    }

    @Override
    public void tick() {
    	if (!tile.shouldPlaySound() || tile.isRemoved()) {
    		stop();
			tile.setNotPlaying();
			return;
        }
        PlayerEntity player = Minecraft.getInstance().player;
        double distance = WorldUtils.distanceBetweenPositions(player.blockPosition(), tile.getBlockPos());
        if(distance <= 1) {
            volume = initialVolume;
        } else if (distance > 1 && distance <= range) {
            volume = (float) (initialVolume / distance);
        } else if (distance > range) {
            volume = 0;
        } else {
            volume = initialVolume;
        }
    }

}
