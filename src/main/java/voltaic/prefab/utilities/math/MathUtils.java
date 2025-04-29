package voltaic.prefab.utilities.math;

import voltaic.prefab.utilities.object.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public class MathUtils {

	public static final CollisionContext EMPTY = new EntityCollisionContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, fluidState -> false, null) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean canAscend) {
			return canAscend;
		}
	};

	public static Location getRaytracedBlock(Entity entity) {
		return getRaytracedBlock(entity, 100);
	}

	public static Location getRaytracedBlock(Entity entity, double rayLength) {
		return getRaytracedBlock(entity.level, entity.getLookAngle(), entity.getEyePosition(0), rayLength);
	}

	public static Location getRaytracedBlock(Level world, Vec3 direction, Vec3 from, double rayLength) {
		// Just normalize for safety. Allows the direction
		// vector to be from some block to another with no
		// consideration for more math.
		Vec3 rayPath = direction.normalize().scale(rayLength);
		Vec3 to = from.add(rayPath);
		ClipContext rayContext = new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, null);
		BlockHitResult rayHit = world.clip(rayContext);

		return rayHit.getType() != Type.BLOCK ? null : new Location(rayHit.getBlockPos());
	}

	public static int logBase2(int value) {
		return (int) (Math.log(value) / Math.log(2) + 1e-10);
	}

	public static int nearestPowerOf10(double value, boolean roundUp) {
		double power = Math.log10(value);
		if(roundUp) {
			return (int) Math.ceil(power);
		} else {
			return (int) Math.floor(power);
		}
	}

	public static <T> T[] fillArr(T[] arr, T val) {
		Arrays.fill(arr, val);
		return arr;
	}

}
