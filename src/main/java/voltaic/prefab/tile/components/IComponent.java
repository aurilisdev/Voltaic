package voltaic.prefab.tile.components;

import javax.annotation.Nullable;

import voltaic.prefab.tile.GenericTile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

//renamed ever so slightly so it's not confused with the Vanilla class constantly when importing 
public interface IComponent {

	IComponentType getType();

	default void holder(GenericTile holder) {
	}
	
	default <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side, CapabilityInputType inputType) {
		return LazyOptional.empty();
	}

	@Nullable
	default GenericTile getHolder() {
		return null;
	}

	default void loadFromNBT(CompoundNBT nbt) {
	}

	default void saveToNBT(CompoundNBT nbt) {
	}

	default void remove() {
	}

	default void onLoad() {
		refresh();
	}

	default void refreshIfUpdate(BlockState oldState, BlockState newState) {

	}

	default void refresh() {

	}

}
