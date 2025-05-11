package voltaic.prefab.block;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.IWrenchable;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;

public abstract class GenericEntityBlock extends Block implements IWrenchable {

	protected GenericEntityBlock(Properties properties) {
		super(properties);
	}

	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void onRotate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (player.level.getBlockState(pos).hasProperty(VoltaicBlockStates.FACING)) {
			BlockState state = rotate(player.level.getBlockState(pos), Rotation.CLOCKWISE_90);
			player.level.setBlockAndUpdate(pos, state);
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (state.hasProperty(VoltaicBlockStates.FACING)) {
			return state.setValue(VoltaicBlockStates.FACING, rot.rotate(state.getValue(VoltaicBlockStates.FACING)));
		}
		return super.rotate(state, rot);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		if (state.hasProperty(VoltaicBlockStates.FACING)) {
			return state.rotate(mirrorIn.getRotation(state.getValue(VoltaicBlockStates.FACING)));
		}
		return super.mirror(state, mirrorIn);
	}

	@Override
	public void onPickup(ItemStack stack, BlockPos pos, PlayerEntity player) {
		World world = player.level;
		world.destroyBlock(pos, true, player);
	}

	// TODO get this to work
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		TileEntity tile = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if (tile instanceof GenericTile) {
			GenericTile machine = (GenericTile) tile;
			ItemStack stack = new ItemStack(this);
			ComponentInventory inv = machine.getComponent(IComponentType.Inventory);
			if (inv != null) {
				InventoryHelper.dropContents(machine.getLevel(), machine.getBlockPos(), inv.getItems());
				
				if(machine.hasComponent(IComponentType.Electrodynamic)) {
				    
				    ComponentElectrodynamic electro = machine.getComponent(IComponentType.Electrodynamic);
				    
				    double joules = electro.getJoulesStored();
                    if (joules > 0) {
                    	stack.getOrCreateTag().putDouble("joules", joules);
                    }
				    
				}

			}
			return Arrays.asList(stack);

		}
		return super.getDrops(state, builder);
	}

	// TODO get this to work

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			if (newState.isAir(level, pos) || !newState.is(state.getBlock())) {
				generic.onBlockDestroyed();
			} else {
				generic.onBlockStateUpdate(state, newState);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	/**
	 * Fired when a neighboring tile changes
	 */
	@Override
	public void onNeighborChange(BlockState state, IWorldReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			generic.onNeightborChanged(neighbor, false);
		}
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

	/**
	 * Fired when a neighboring blockstate changes
	 */
	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, neighbor, isMoving);
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			generic.onNeightborChanged(neighbor, true);
		}

	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			return generic.getComparatorSignal();
		}
		return super.getAnalogOutputSignal(state, level, pos);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity entity = worldIn.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			return generic.use(player, handIn, hit);
		}

		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			return generic.getDirectSignal(direction);
		}
		return super.getDirectSignal(state, level, pos, direction);
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			return generic.getSignal(direction);
		}
		return super.getSignal(state, level, pos, direction);
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		super.entityInside(state, level, pos, entity);
		TileEntity tile = level.getBlockEntity(pos);
		if (tile instanceof GenericTile) {
			GenericTile generic = (GenericTile) tile;
			generic.onEntityInside(state, level, pos, entity);
		}
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		TileEntity entity = level.getBlockEntity(pos);
		if (entity instanceof GenericTile) {
			GenericTile generic = (GenericTile) entity;
			generic.setPlacedBy(placer, stack);
		}

	}
}
