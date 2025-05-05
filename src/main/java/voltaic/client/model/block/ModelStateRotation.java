package voltaic.client.model.block;

import java.util.HashMap;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;

public class ModelStateRotation {

    // DUNSWE
    public static final HashMap<Direction, ModelRotation> ROTATIONS = Util.make(() -> {

        HashMap<Direction, ModelRotation> rotations = new HashMap<>();
        rotations.put(Direction.UP, ModelRotation.X270_Y0);
        rotations.put(Direction.DOWN, ModelRotation.X90_Y0);
        rotations.put(Direction.NORTH, ModelRotation.X0_Y0);
        rotations.put(Direction.SOUTH, ModelRotation.X0_Y180);
        rotations.put(Direction.WEST, ModelRotation.X0_Y270);
        rotations.put(Direction.EAST, ModelRotation.X0_Y90);

        return rotations;

    });

}
