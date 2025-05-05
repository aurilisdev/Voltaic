package voltaic.api.radiation.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.BlockPos;

public class BlockPosVolume {

    public static final Codec<BlockPosVolume> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("start").forGetter(BlockPosVolume::start),
            BlockPos.CODEC.fieldOf("end").forGetter(BlockPosVolume::end)
    ).apply(instance, BlockPosVolume::new));
    
    private final BlockPos start;
    private final BlockPos end;
    
    public BlockPosVolume(BlockPos start, BlockPos end) {
    	this.start = start;
    	this.end = end;
    }

    public boolean isIn(BlockPos pos) {
        if (start().getX() > pos.getX() || end().getX() < pos.getX()) {
            return false;
        }
        if (start().getY() > pos.getY() || end().getY() < pos.getY()) {
            return false;
        }
        if (start().getZ() > pos.getZ() || end().getZ() < pos.getZ()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockPosVolume) {
        	BlockPosVolume other = (BlockPosVolume) obj;
            return other.start().equals(this.start()) && other.end().equals(this.end());
        }
        return false;
    }
    
    public BlockPos start() {
    	return start;
    }
    
    public BlockPos end() {
    	return end;
    }
}
