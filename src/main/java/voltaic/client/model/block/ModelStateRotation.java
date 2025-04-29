package voltaic.client.model.block;

import java.util.HashMap;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;

import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;

public class ModelStateRotation implements ModelState {

    public static final ModelStateRotation UP = new ModelStateRotation(270, 0, 0);
    public static final ModelStateRotation DOWN = new ModelStateRotation(90, 0, 0);
    public static final ModelStateRotation NORTH = new ModelStateRotation(0, 0, 0);
    public static final ModelStateRotation SOUTH = new ModelStateRotation(0, 180, 0);
    public static final ModelStateRotation WEST = new ModelStateRotation(0, 270, 0);
    public static final ModelStateRotation EAST = new ModelStateRotation(0, 90, 0);

    // DUNSWE
    public static final HashMap<Direction, ModelStateRotation> ROTATIONS = Util.make(() -> {

        HashMap<Direction, ModelStateRotation> rotations = new HashMap<>();
        rotations.put(Direction.UP, UP);
        rotations.put(Direction.DOWN, DOWN);
        rotations.put(Direction.NORTH, NORTH);
        rotations.put(Direction.SOUTH, SOUTH);
        rotations.put(Direction.WEST, WEST);
        rotations.put(Direction.EAST, EAST);

        return rotations;

    });

    private final Transformation transformation;

    private ModelStateRotation(int x, int y, int z) {
        float d2r = (float) (Math.PI / 180F);
        Quaternion q = new Quaternion(-z * d2r, 0F, 0F, 1F);
        q.mul(new Quaternion(-y * d2r, 0F, 1F, 0F));
        q.mul(new Quaternion(-x * d2r, 1F, 0F, 0F));
        this.transformation = new Transformation(null, q, null, null);
    }

    @Override
    public Transformation getRotation() {
        return transformation;
    }

}
