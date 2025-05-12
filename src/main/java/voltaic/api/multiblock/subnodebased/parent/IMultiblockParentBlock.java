package voltaic.api.multiblock.subnodebased.parent;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import voltaic.api.multiblock.subnodebased.Subnode;

public interface IMultiblockParentBlock {

	boolean hasMultiBlock();

	default boolean isValidMultiblockPlacement(BlockState state, IWorldReader worldIn, BlockPos pos, Subnode[] nodes) {
		for (Subnode sub : nodes) {
			BlockPos check = pos.offset(sub.pos());
			if (!worldIn.getBlockState(check).getMaterial().isReplaceable()) {
				return false;
			}
		}
		return true;
	}

	public static class SubnodeWrapper {

		public static final SubnodeWrapper EMPTY = new SubnodeWrapper(new Subnode[0]);
		private HashMap<Direction, Subnode[]> subnodeMap = new HashMap<>();
		private Subnode[] omni = null;

		private SubnodeWrapper(Subnode[] omni) {
			this.omni = omni;
		}

		private SubnodeWrapper(Subnode[] north, Subnode[] east, Subnode[] south, Subnode[] west) {
			subnodeMap.put(Direction.NORTH, north);
			subnodeMap.put(Direction.EAST, east);
			subnodeMap.put(Direction.SOUTH, south);
			subnodeMap.put(Direction.WEST, west);
		}

		public Subnode[] getSubnodes(@Nullable Direction dir) {
			if(omni != null) {
				return omni;
			}
			return subnodeMap.getOrDefault(dir, new Subnode[0]);
		}

		public static SubnodeWrapper createOmni(Subnode[] omni) {
			return new SubnodeWrapper(omni);
		}

		public static SubnodeWrapper createDirectional(Subnode[] north, Subnode[] east, Subnode[] south, Subnode[] west) {
			return new SubnodeWrapper(north, east, south, west);
		}


	}

}
