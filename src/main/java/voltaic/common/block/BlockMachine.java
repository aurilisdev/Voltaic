package voltaic.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.api.tile.IMachine;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.block.GenericMachineBlock;
import voltaic.prefab.utilities.BlockEntityUtils;

public class BlockMachine extends GenericMachineBlock implements IMultiblockParentBlock {

    protected final IMachine machine;

    public BlockMachine(IMachine machine) {
        super(machine.getBlockEntitySupplier(), machine.getVoxelShapeProvider());
        this.machine = machine;
        if (machine.usesLit()) {
            registerDefaultState(stateDefinition.any().setValue(VoltaicBlockStates.LIT, false));
        }

    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, IBlockReader pLevel, BlockPos pPos) {
        if (machine.propegatesLightDown()) {
            return true;
        }
        return super.propagatesSkylightDown(pState, pLevel, pPos);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {

        if(machine.isMultiblock()) {
            return isValidMultiblockPlacement(state, worldIn, pos, machine.getSubnodes().getSubnodes(state.hasProperty(VoltaicBlockStates.FACING) ? state.getValue(VoltaicBlockStates.FACING) : Direction.NORTH));
        }
        return super.canSurvive(state, worldIn, pos);

    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return machine.getRenderShape();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {

        if (machine.getLitBrightness() > 0 && state.hasProperty(VoltaicBlockStates.LIT) && state.getValue(VoltaicBlockStates.LIT)) {
            return machine.getLitBrightness();
        }

        return super.getLightValue(state, world, pos);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tile = worldIn.getBlockEntity(pos);
        if (hasMultiBlock() && tile instanceof IMultiblockParentTile) {
        	IMultiblockParentTile multi = (IMultiblockParentTile) tile;
            multi.onNodePlaced(worldIn, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = worldIn.getBlockEntity(pos);
        if (!(state.getBlock() == newState.getBlock() && state.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING))) {

        	if (hasMultiBlock() && tile instanceof IMultiblockParentTile) {
            	IMultiblockParentTile multi = (IMultiblockParentTile) tile;
                multi.onNodeReplaced(worldIn, pos, true);
            }
        }
        if (newState.isAir(worldIn, pos)) {
        	if (hasMultiBlock() && tile instanceof IMultiblockParentTile) {
            	IMultiblockParentTile multi = (IMultiblockParentTile) tile;
                multi.onNodeReplaced(worldIn, pos, false);
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);

    }

    @Override
    public boolean hasMultiBlock() {
        return machine.isMultiblock();
    }

    @Override
    public boolean isIPlayerStorable() {
        return machine.isPlayerStorable();
    }
    
    @Override
    public ItemStack getCloneItemStack(IBlockReader level, BlockPos pos, BlockState state) {
    	ItemStack stack = super.getCloneItemStack(level, pos, state);
        TileEntity tile = level.getBlockEntity(pos);
        if (tile != null) {
        	BlockEntityUtils.saveToItem(tile, stack);
        }
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).setValue(VoltaicBlockStates.LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VoltaicBlockStates.LIT);
    }

}
