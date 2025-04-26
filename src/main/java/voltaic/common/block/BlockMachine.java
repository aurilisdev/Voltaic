package voltaic.common.block;

import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.api.tile.IMachine;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.block.GenericMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.HitResult;

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
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (machine.propegatesLightDown()) {
            return true;
        }
        return super.propagatesSkylightDown(pState, pLevel, pPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {

        if(machine.isMultiblock()) {
            return isValidMultiblockPlacement(state, worldIn, pos, machine.getSubnodes().getSubnodes(state.hasProperty(VoltaicBlockStates.FACING) ? state.getValue(VoltaicBlockStates.FACING) : Direction.NORTH));
        }
        return super.canSurvive(state, worldIn, pos);

    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return machine.getRenderShape();
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {

        if (machine.getLitBrightness() > 0 && state.hasProperty(VoltaicBlockStates.LIT) && state.getValue(VoltaicBlockStates.LIT)) {
            return machine.getLitBrightness();
        }

        return super.getLightEmission(state, world, pos);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (hasMultiBlock() && tile instanceof IMultiblockParentTile multi) {
            multi.onNodePlaced(worldIn, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (!(state.getBlock() == newState.getBlock() && state.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING))) {

            if (tile instanceof IMultiblockParentTile multi) {
                multi.onNodeReplaced(worldIn, pos, true);
            }
        }
        if (newState.isAir()) {
            if (tile instanceof IMultiblockParentTile multi) {
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    	ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile != null) {
            tile.saveToItem(stack);
        }
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(VoltaicBlockStates.LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VoltaicBlockStates.LIT);
    }

}
