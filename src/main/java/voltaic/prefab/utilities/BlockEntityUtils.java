package voltaic.prefab.utilities;

import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.tile.GenericTile;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class BlockEntityUtils {

	public static final BlockPos OUT_OF_REACH = new BlockPos(0, -1000, 0);
	public static final int[][] RELATIVE_MATRIX = {
			//DUNSWE
			{ 3, 2, 1, 0, 5, 4 }, //D Note these really don't work; you need two variables to track this - skip999
			{ 4, 5, 0, 1, 2, 3 }, //U Note these really don't work; you need two variables to track this - skip999
			{ 0, 1, 2, 3, 4, 5 }, //N
			{ 0, 1, 3, 2, 5, 4 }, //S
			{ 0, 1, 4, 5, 3, 2 },
			{ 0, 1, 5, 4, 2, 3 }, //W
			  //E
	};

	public static Direction getRelativeSide(Direction facingDirection, Direction relativeDirection) {
		if (facingDirection == null || relativeDirection == null) {
			return Direction.UP;
		}
		return Direction.values()[RELATIVE_MATRIX[facingDirection.ordinal()][relativeDirection.ordinal()]];
	}

	public static void updateLit(GenericTile tile, Boolean value) {
		Level world = tile.getLevel();
		BlockPos pos = tile.getBlockPos();
		if (tile.getBlockState().hasProperty(VoltaicBlockStates.LIT)) {
			world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(VoltaicBlockStates.LIT, value));
		}
	}

	public static boolean isLit(GenericTile tile) {
		if (tile.getBlockState().hasProperty(VoltaicBlockStates.LIT)) {
			return tile.getBlockState().getValue(VoltaicBlockStates.LIT);
		}
		return false;
	}

	@Nullable
	public static Direction directionFromPos(BlockPos thisPos, BlockPos otherPos) {
		int x = otherPos.getX() - thisPos.getX();
		int y = otherPos.getY() - thisPos.getY();
		int z = otherPos.getZ() - thisPos.getZ();
		return fromDelta(x, y, z);
	}
	
	@Nullable
	public static Direction fromDelta(int x, int y, int z) {
		if (x == 0) {
            if (y == 0) {
                if (z > 0) {
                    return Direction.SOUTH;
                }

                if (z < 0) {
                    return Direction.NORTH;
                }
            } else if (z == 0) {
                if (y > 0) {
                    return Direction.UP;
                }

                return Direction.DOWN;
            }
        } else if (y == 0 && z == 0) {
            if (x > 0) {
                return Direction.EAST;
            }

            return Direction.WEST;
        }
		return null;
	}


	/**
	 * This enum is used as a wrapper for the Vanilla directions to make the directions used by machine IO a little
	 * easier to understand. The perspectives are defined from that of the machine i.e. the front of the machine is
	 * facing north.
	 *
	 *
	 * @author skip999
	 */
	public static enum MachineDirection {
		BOTTOM(Direction.DOWN), TOP(Direction.UP), FRONT(Direction.NORTH), BACK(Direction.SOUTH), LEFT(Direction.WEST), RIGHT(Direction.EAST);

		public final Direction mappedDir;
		private MachineDirection(Direction mappedDir) {
			this.mappedDir = mappedDir;
		}

		public static Direction[] toDirectionArray(MachineDirection...machineDirections){
			Direction[] dirs = new Direction[machineDirections.length];
			for(int i = 0; i < machineDirections.length; i++) {
				dirs[i] = machineDirections[i].mappedDir;
			}
			return dirs;
		}

		public static MachineDirection fromDirection(Direction dir) {
			return values()[dir.ordinal()];
		}
	}

}
