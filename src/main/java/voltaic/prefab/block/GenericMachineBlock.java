package voltaic.prefab.block;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import voltaic.api.tile.TileEntitySupplier;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

public class GenericMachineBlock extends GenericEntityBlockWaterloggable {

    private final TileEntitySupplier<TileEntity> blockEntitySupplier;
    private final VoxelShapeProvider shapeProvider;

    public static HashMap<BlockPos, LivingEntity> IPLAYERSTORABLE_MAP = new HashMap<>();

    public GenericMachineBlock(TileEntitySupplier<TileEntity> blockEntitySupplier, VoxelShapeProvider provider) {
        super(Properties.copy(Blocks.IRON_BLOCK).strength(3.5F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(VoltaicBlockStates.FACING, Direction.NORTH));
        this.blockEntitySupplier = blockEntitySupplier;
        this.shapeProvider = provider;
    }

    public GenericMachineBlock(TileEntitySupplier<TileEntity> blockEntitySupplier, boolean temp) {
        this(blockEntitySupplier, VoxelShapeProvider.DEFAULT);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction dir = null;
        if (state.hasProperty(VoltaicBlockStates.FACING)) {

            dir = state.getValue(VoltaicBlockStates.FACING);

        }
        return shapeProvider.getShape(dir);
    }

    @Override
    public void setPlacedBy(World pWorld, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (isIPlayerStorable()) {
            IPLAYERSTORABLE_MAP.put(pPos, pPlacer);
        }
        super.setPlacedBy(pWorld, pPos, pState, pPlacer, pStack);
    }

    @Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return blockEntitySupplier.create(world);
	}

    @Override
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).setValue(VoltaicBlockStates.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VoltaicBlockStates.FACING);
    }

    public boolean isIPlayerStorable() {
        return false;
    }

}
