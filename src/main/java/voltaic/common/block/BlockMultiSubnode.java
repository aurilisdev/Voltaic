package voltaic.common.block;

import voltaic.api.multiblock.subnodebased.child.IMultiblockChildBlock;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.block.GenericEntityBlock;
import voltaic.prefab.tile.GenericTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMultiSubnode extends GenericEntityBlock implements IMultiblockChildBlock {

    public BlockMultiSubnode() {
        super(Properties.copy(Blocks.GLASS).strength(3.5F).sound(SoundType.METAL).isRedstoneConductor((a, b, c) -> false).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    	TileEntity blockentity = worldIn.getBlockEntity(pos);
    	if(blockentity instanceof TileMultiSubnode) {
    		TileMultiSubnode subnode = (TileMultiSubnode) blockentity;
    		return subnode.getShape();
    	}
        return VoxelShapes.block();
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }
    
    @Override
    public ItemStack getCloneItemStack(IBlockReader level, BlockPos pos, BlockState state) {
    	TileEntity tile = level.getBlockEntity(pos);
    	if (tile instanceof TileMultiSubnode) {
    		TileMultiSubnode subnode = (TileMultiSubnode) tile;
            return new ItemStack(level.getBlockState(subnode.parentPos.getValue()).getBlock());
        }
    	return super.getCloneItemStack(level, pos, state);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileMultiSubnode();
	}

    @Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity entity = level.getBlockEntity(pos);
		if (newState.isAir(level, pos) && entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			generic.onBlockDestroyed();
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

    @Override
	public void onPlace(BlockState newState, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(newState, level, pos, oldState, isMoving);
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			generic.onPlace(oldState, isMoving);
		}
	}

}
