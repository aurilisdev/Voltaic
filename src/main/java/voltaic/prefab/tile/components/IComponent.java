package voltaic.prefab.tile.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.tile.GenericTile;

//renamed ever so slightly so it's not confused with the Vanilla class constantly when importing 
public interface IComponent {

    IComponentType getType();

    GenericTile getHolder();

    default void loadFromNBT(CompoundTag nbt) {
    }

    default void saveToNBT(CompoundTag nbt) {
    }

    default void remove() {
    }

    default void onLoad(Level level) {
	refresh(level);
    }

    default void refreshIfUpdate(Level level, BlockState oldState, BlockState newState) {

    }

    default void refresh(Level level) {

    }

}
