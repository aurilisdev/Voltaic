package voltaic.api.multiblock.subnodebased;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class Subnode {

	public static final Subnode EMPTY = new Subnode(BlockPos.ZERO, VoxelShapes.empty());
	
	private final BlockPos pos;
	private final VoxelShape[] shapes;
	
	public Subnode(BlockPos pos, VoxelShape[] shapes) {
		this.pos = pos;
		this.shapes = shapes;
	}

	public Subnode(BlockPos pos, VoxelShape allDirsShape) {
		this(pos, new VoxelShape[] { allDirsShape, allDirsShape, allDirsShape, allDirsShape, allDirsShape, allDirsShape });
	}

	public VoxelShape getShape(Direction dir) {
		return shapes[dir.ordinal()];
	}
	
	public BlockPos pos() {
		return pos;
	}

}
