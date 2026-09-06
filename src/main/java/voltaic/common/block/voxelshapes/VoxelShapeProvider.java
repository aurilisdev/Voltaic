package voltaic.common.block.voxelshapes;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeProvider {

    private final @Nullable VoxelShape omni;
    private final @Nullable VoxelShape[] shapes;

    public static final VoxelShapeProvider DEFAULT = new VoxelShapeProvider(Shapes.block());

    private VoxelShapeProvider(VoxelShape omni) {
	this.omni = omni;
	shapes = null;
    }

    private VoxelShapeProvider(VoxelShape[] shapes) {
	omni = null;
	this.shapes = shapes;
    }

    public VoxelShape getShape(Direction dir) {
	if (omni != null)
	    return omni;
	else if (dir == Direction.UP || dir == Direction.DOWN)
	    return Shapes.block();
	else if (shapes != null)
	    return shapes[dir.ordinal()];
	throw new IllegalStateException(
		"VoxelShapeProvider has no shape data. This indicates an invalid provider state.");
    }

    public static VoxelShapeProvider createOmni(VoxelShape shape) {
	return new VoxelShapeProvider(shape);
    }

    public static VoxelShapeProvider createDirectional(Direction startDirection, VoxelShape shape) {
	return new VoxelShapeProvider(createShapes(shape, startDirection));
    }

    public static final Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.SOUTH, Direction.WEST,
	    Direction.EAST };

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
	VoxelShape[] buffer = { shape, Shapes.empty() };

	int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
	for (int i = 0; i < times; i++) {
	    buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
		    Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
	    buffer[0] = buffer[1];
	    buffer[1] = Shapes.empty();
	}

	return buffer[0];
    }

    public static VoxelShape[] createShapes(VoxelShape baseShape, Direction baseDirection) {
	VoxelShape[] shapes = new VoxelShape[6];
	for (Direction dir : HORIZONTAL_DIRECTIONS) {
	    shapes[dir.ordinal()] = rotateShape(baseDirection, dir, baseShape);
	}
	return shapes;
    }

}
