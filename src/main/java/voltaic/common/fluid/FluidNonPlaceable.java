package voltaic.common.fluid;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;
import voltaic.prefab.utilities.math.Color;

public class FluidNonPlaceable extends Fluid {

	private final Supplier<? extends Item> bucket;
	private final String modId;
	private final String id;
	private final String texture;
	private final Color color;

	public FluidNonPlaceable(Supplier<? extends Item> bucket, String modId, String id, String texture, Color color) {
		this.bucket = bucket;
		this.modId = modId;
		this.id = id;
		this.texture = texture;
		this.color = color;
	}

	@Override
	public Item getBucket() {
		return bucket.get();
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	@Override
	protected Vector3d getFlow(IBlockReader blockReader, BlockPos pos, FluidState fluidState) {
		return Vector3d.ZERO;
	}

	@Override
	public int getTickDelay(IWorldReader levelReader) {
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		return 0;
	}

	@Override
	public float getHeight(FluidState fluidState, IBlockReader blockGetter, BlockPos pos) {
		return 0;
	}

	@Override
	public float getOwnHeight(FluidState state) {
		return 0;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isSource(FluidState state) {
		return false;
	}

	@Override
	public int getAmount(FluidState state) {
		return 0;
	}

	@Override
	public VoxelShape getShape(FluidState state, IBlockReader getter, BlockPos pos) {
		return VoxelShapes.block();
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(new ResourceLocation(modId, "block/fluid/" + texture), new ResourceLocation(modId, "block/fluid/" + texture)).translationKey("fluid." + modId + "." + id).color(color.color()).build(this);
	}
}
