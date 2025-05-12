package voltaic.common.block.connect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.block.GenericEntityBlockWaterloggable;
import voltaic.prefab.tile.types.GenericConnectTile;

public abstract class AbstractConnectBlock extends GenericEntityBlockWaterloggable {

	protected final VoxelShape[] boundingBoxes = new VoxelShape[7];

	// 6 possible directions
	int maxValue = 0b1000000;
	protected VoxelShape[] shapestates = new VoxelShape[maxValue];
	public AbstractConnectBlock(Properties properties, double radius) {
		super(properties);
		generateBoundingBoxes(radius);
		stateDefinition.any().setValue(VoltaicBlockStates.HAS_SCAFFOLDING, false);
	}

	public void generateBoundingBoxes(double radius) {
		double w = radius;
		double sm = 8 - w;
		double lg = 8 + w;
		// down
		boundingBoxes[0] = Block.box(sm, 0, sm, lg, lg, lg);
		// up
		boundingBoxes[1] = Block.box(sm, sm, sm, lg, 16, lg);
		// north
		boundingBoxes[2] = Block.box(sm, sm, 0, lg, lg, lg);
		// south
		boundingBoxes[3] = Block.box(sm, sm, sm, lg, lg, 16);
		// west
		boundingBoxes[4] = Block.box(0, sm, sm, lg, lg, lg);
		// east
		boundingBoxes[5] = Block.box(sm, sm, sm, 16, lg, lg);
		// center
		boundingBoxes[6] = Block.box(sm, sm, sm, lg, lg, lg);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

		VoxelShape camoShape = VoxelShapes.empty();

		TileEntity entity = worldIn.getBlockEntity(pos);
		
		if (state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING) && entity instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) entity;

			if (connect.isCamoAir()) {
				camoShape = connect.getScaffoldBlock().getShape(worldIn, pos, context);
			} else {
				camoShape = connect.getCamoBlock().getShape(worldIn, pos, context);
			}

		}

		if(!(entity instanceof GenericConnectTile)) {
			return VoxelShapes.empty();
		}

		EnumConnectType[] connections = ((GenericConnectTile) entity).readConnections();
		int hash = hashPresentSides(connections);
		// Check for existing shape
		if (shapestates[hash] != null) {
			return getCamoShape(shapestates[hash], camoShape);
		}
		// Create new shape for connections
		VoxelShape shape = boundingBoxes[6];
		for (int i = 0; i < 6; i++) {
			if (connections[i] == EnumConnectType.NONE) {
				continue;
			}

			shape = VoxelShapes.join(shape, boundingBoxes[i], IBooleanFunction.OR);
		}
		shapestates[hash] = shape;
		if (shape == null) {
			return VoxelShapes.empty();
		}
		return getCamoShape(shape, camoShape);
	}

	private VoxelShape getCamoShape(VoxelShape wireShape, VoxelShape camoShape) {
		if (camoShape == VoxelShapes.empty()) return wireShape;
		if (camoShape == VoxelShapes.block()) return camoShape;
		return VoxelShapes.join(wireShape, camoShape, IBooleanFunction.OR);
	}

	public static int hashPresentSides(EnumConnectType[] connections) {
		int flag = 0;
		for (short i = 0; i < 6; i++) {
			if (connections[i] != EnumConnectType.NONE) {
				flag = flag | (1 << i);
			}
		}
		return flag;
	}

	@Override
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(VoltaicBlockStates.HAS_SCAFFOLDING);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		if (level.getBlockState(pos).getBlock() instanceof BlockScaffold) {
			return true;
		}
		return super.canSurvive(state, level, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState superState = super.getStateForPlacement(context);
		World world = context.getPlayer().level;
		boolean set = false;
		if (world.getBlockState(context.getClickedPos()).getBlock() instanceof BlockScaffold) {
			superState = superState.setValue(VoltaicBlockStates.HAS_SCAFFOLDING, true);
			set = true;
		} else {
			superState = superState.setValue(VoltaicBlockStates.HAS_SCAFFOLDING, false);
		}
		if (!world.isClientSide() && set) {
			world.setBlockAndUpdate(context.getClickedPos(), Blocks.AIR.defaultBlockState());
		}
		return superState;
	}

	/*
	@Override
	public void onPlace(BlockState newState, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(newState, level, pos, oldState, isMoving);
		if (newState.hasProperty(ElectrodynamicsBlockStates.HAS_SCAFFOLDING) && oldState.hasProperty(ElectrodynamicsBlockStates.HAS_SCAFFOLDING)) {
			newState = newState.setValue(ElectrodynamicsBlockStates.HAS_SCAFFOLDING, oldState.getValue(ElectrodynamicsBlockStates.HAS_SCAFFOLDING));
		}
	}

	 */
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack stack = player.getItemInHand(handIn);
		if (stack.isEmpty()) {
			return super.use(state, worldIn, pos, player, handIn, hit);
		}

		TileEntity blockentity = worldIn.getBlockEntity(pos);
		
		if (stack.getItem() instanceof BlockItem && blockentity instanceof GenericConnectTile) {
			
			BlockItem blockitem = (BlockItem) stack.getItem();
			GenericConnectTile connect = (GenericConnectTile) blockentity;

			BlockItemUseContext newCtx = new BlockItemUseContext(player, handIn, stack, hit);

			if (blockitem.getBlock() instanceof BlockScaffold) {
				BlockScaffold scaffold = (BlockScaffold) blockitem.getBlock();
				if (!state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING)) {
					if (!worldIn.isClientSide) {
						if (!player.isCreative()) {
							stack.shrink(1);
							player.setItemInHand(handIn, stack);
						}
						state = state.setValue(VoltaicBlockStates.HAS_SCAFFOLDING, true);
						worldIn.setBlockAndUpdate(pos, state);
						connect.setScaffoldBlock(scaffold.getStateForPlacement(newCtx));
						worldIn.playSound(null, pos, blockitem.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					return ActionResultType.CONSUME;
				}

			} else if (!(blockitem.getBlock() instanceof AbstractConnectBlock) && state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING)) {
				if (connect.isCamoAir()) {
					if (!worldIn.isClientSide) {
						connect.setCamoBlock(blockitem.getBlock().getStateForPlacement(newCtx));
						if (!player.isCreative()) {
							stack.shrink(1);
							player.setItemInHand(handIn, stack);
						}
						worldIn.playSound(null, pos, blockitem.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
						worldIn.getChunkSource().getLightEngine().checkBlock(pos);
					}
					return ActionResultType.CONSUME;
				}
				if (!connect.getCamoBlock().is(blockitem.getBlock())) {
					if (!worldIn.isClientSide) {
						if (!player.isCreative()) {
							if (!player.addItem(new ItemStack(connect.getCamoBlock().getBlock()))) {
								worldIn.addFreshEntity(new ItemEntity(player.level, (int) player.getX(), (int) player.getY(), (int) player.getZ(), new ItemStack(connect.getCamoBlock().getBlock())));
							}
							stack.shrink(1);
							player.setItemInHand(handIn, stack);
						}
						connect.setCamoBlock(blockitem.getBlock().getStateForPlacement(newCtx));
						worldIn.playSound(null, pos, blockitem.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					return ActionResultType.CONSUME;

				}
			}

		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ArrayList<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
		if (state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING) && builder.getOptionalParameter(LootParameters.BLOCK_ENTITY) instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
			drops.add(new ItemStack(connect.getScaffoldBlock().getBlock()));
			if (!connect.isCamoAir()) {
				drops.add(new ItemStack(connect.getCamoBlock().getBlock()));
			}
		}
		return drops;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader level, BlockPos pos) {
		if (!state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING)) {
			return true;
		}
		TileEntity blockEntity = level.getBlockEntity(pos);
		if (level.getBlockEntity(pos) instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) blockEntity;
			if (connect.isCamoAir()) {
				return connect.getScaffoldBlock().propagatesSkylightDown(level, pos);
			}
			return connect.getCamoBlock().propagatesSkylightDown(level, pos);
		}

		return true;
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos pos) {
		if (!state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING)) {
			return 0;
		}

		TileEntity blockEntity = level.getBlockEntity(pos);
		if (level.getBlockEntity(pos) instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) blockEntity;
			if (connect.isCamoAir()) {
				return connect.getScaffoldBlock().getBlock().getLightValue(connect.getScaffoldBlock(), level, pos);
			}
			return connect.getCamoBlock().getBlock().getLightValue(connect.getCamoBlock(), level, pos);
		}

		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		if (!state.getValue(VoltaicBlockStates.HAS_SCAFFOLDING)) {
			return super.getVisualShape(state, level, pos, context);
		}
		TileEntity blockEntity = level.getBlockEntity(pos);
		if (level.getBlockEntity(pos) instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) blockEntity;
			if (connect.isCamoAir()) {
				return connect.getScaffoldBlock().getVisualShape(level, pos, context);
			}
			return connect.getCamoBlock().getVisualShape(level, pos, context);
		}
		return super.getVisualShape(state, level, pos, context);
	}

	@Override
	public void onRotate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		World level = player.level;
		if (level.isClientSide()) {
			return;
		}
		TileEntity blockEntity = level.getBlockEntity(pos);
		if (level.getBlockEntity(pos) instanceof GenericConnectTile) {
			GenericConnectTile connect = (GenericConnectTile) blockEntity;

			if (!connect.isCamoAir()) {
				Block camo = connect.getCamoBlock().getBlock();

				connect.setCamoBlock(Blocks.AIR.defaultBlockState());

				if (!player.isCreative()) {
					if (!player.addItem(new ItemStack(camo))) {
						level.addFreshEntity(new ItemEntity(player.level, (int) player.getX(), (int) player.getY(), (int) player.getZ(), new ItemStack(camo)));
					}
				}

				return;
			}
			if (!connect.isScaffoldAir()) {
				Block scaffold = connect.getScaffoldBlock().getBlock();

				connect.setScaffoldBlock(Blocks.AIR.defaultBlockState());

				level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(VoltaicBlockStates.HAS_SCAFFOLDING, false));

				if (!player.isCreative()) {
					if (!player.addItem(new ItemStack(scaffold))) {
						level.addFreshEntity(new ItemEntity(player.level, (int) player.getX(), (int) player.getY(), (int) player.getZ(), new ItemStack(scaffold)));
					}
				}

				return;
			}

		}

		super.onRotate(stack, pos, player);

	}

}
