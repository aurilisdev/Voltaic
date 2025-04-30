package voltaic.client.model.block;

import java.util.HashMap;

import net.minecraft.Util;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;

public class ModelStateRotation {

    // DUNSWE
    public static final HashMap<Direction, BlockModelRotation> ROTATIONS = Util.make(() -> {

        HashMap<Direction, BlockModelRotation> rotations = new HashMap<>();
        rotations.put(Direction.UP, BlockModelRotation.X270_Y0);
        rotations.put(Direction.DOWN, BlockModelRotation.X90_Y0);
        rotations.put(Direction.NORTH, BlockModelRotation.X0_Y0);
        rotations.put(Direction.SOUTH, BlockModelRotation.X0_Y180);
        rotations.put(Direction.WEST, BlockModelRotation.X0_Y270);
        rotations.put(Direction.EAST, BlockModelRotation.X0_Y90);

        return rotations;

    });

}
