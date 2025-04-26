package voltaic.api.multiblock.assemblybased;

import voltaic.Voltaic;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileReplaceable extends GenericTile {

	protected final SingleProperty<Integer> disguisedBlock = property(new SingleProperty<>(PropertyTypes.INTEGER, "disguisedblock", 0));
	
	public TileReplaceable(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
		super(tileEntityTypeIn, worldPos, blockState);
	}
	
	public void setDisguise(BlockState state) {
		if(state.isAir()){
			Voltaic.LOGGER.info("air");
		}
		disguisedBlock.setValue(Block.BLOCK_STATE_REGISTRY.getId(state));
	}
	
	public BlockState getDisguise() {
		return Block.BLOCK_STATE_REGISTRY.byId(disguisedBlock.getValue());
	}
	

}
