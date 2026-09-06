package voltaic.api.multiblock.subnodebased.parent;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.multiblock.subnodebased.Subnode;

public interface IMultiblockParentBlock {

    boolean hasMultiBlock();

    default boolean isValidMultiblockPlacement(BlockState state, LevelReader worldIn, BlockPos pos, Subnode[] nodes) {
	for (Subnode sub : nodes) {
	    BlockPos check = pos.offset(sub.pos());
	    if (!worldIn.getBlockState(check).canBeReplaced())
		return false;
	}
	return true;
    }

    public static class SubnodeWrapper {
	private static final Subnode[] EMPTY_SUBNODES = {};
	public static final SubnodeWrapper EMPTY = new SubnodeWrapper(EMPTY_SUBNODES);
	private final HashMap<Direction, Subnode[]> subnodeMap = new HashMap<>();

	private final @Nullable Subnode[] omni;

	private SubnodeWrapper(Subnode[] omni) {
	    this.omni = omni;
	}

	private SubnodeWrapper(Subnode[] north, Subnode[] east, Subnode[] south, Subnode[] west) {
	    omni = null;
	    subnodeMap.put(Direction.NORTH, north);
	    subnodeMap.put(Direction.EAST, east);
	    subnodeMap.put(Direction.SOUTH, south);
	    subnodeMap.put(Direction.WEST, west);
	}

	public Subnode[] getSubnodes(@Nullable Direction dir) {
	    Subnode[] omni = this.omni;
	    if (omni != null)
		return omni;
	    return subnodeMap.getOrDefault(dir, EMPTY_SUBNODES);
	}

	public static SubnodeWrapper createOmni(Subnode[] omni) {
	    return new SubnodeWrapper(omni);
	}

	public static SubnodeWrapper createDirectional(Subnode[] north, Subnode[] east, Subnode[] south,
		Subnode[] west) {
	    return new SubnodeWrapper(north, east, south, west);
	}

    }
}
