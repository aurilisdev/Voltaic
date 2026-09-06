package voltaic.prefab.properties.variant;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.SinglePropertyType;
import voltaic.prefab.tile.GenericTile;

/**
 * A wrapper class designed to monitor a value and take action when it changes
 * This variant assumes a single variable instead of a group of variables such
 * as an array or list
 *
 * @param <T> The type of the property
 * @author skip999
 * @author AurilisDev
 */
public class SingleProperty<T> extends AbstractProperty<T, SinglePropertyType<T, ?>> {

    private boolean alreadySynced = false;

    // This fires when the property has had a value set and that value is different
    // from the value the property currently has
    // The property contains the new value and val represents the old value. Level
    // may or may not be present.
    private BiConsumer<SingleProperty<T>, T> onChange = (prop, val) -> {};
    // this fires when the owning tile has been loaded. This fires on both the
    // client and server-side, and Level is present
    private Consumer<SingleProperty<T>> onTileLoaded = prop -> {};

    public SingleProperty(PropertyManager manager, SinglePropertyType<T, ?> type, String name, T defaultValue) {
	super(manager, type, name, defaultValue);
    }

    public SingleProperty<T> onChange(BiConsumer<SingleProperty<T>, T> event) {
	onChange = onChange.andThen(event);
	return this;
    }

    public SingleProperty<T> onTileLoaded(Consumer<SingleProperty<T>> event) {
	onTileLoaded = onTileLoaded.andThen(event);
	return this;
    }

    @Override
    public void setValue(Object updated) {
	if (alreadySynced || !markDirtyIfChanged((T) updated))
	    return;

	T old = getValue();
	value = (T) updated;
	PropertyManager manager = getPropertyManager();

	GenericTile owningEntity = manager.getOwner();
	Level level = owningEntity.getLevel();
	if (level == null)
	    return;

	if (!level.isClientSide()) {
	    manager.setDirty(this);
	    if (shouldUpdateOnChange()) {
		alreadySynced = true;
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old);
    }

    public void copy(SingleProperty<T> other) {
	T otherVal = other.getValue();
	setValue(otherVal);
    }

    private boolean markDirtyIfChanged(T updated) {
	if (!getType().isEqual(value, updated)) {
	    setDirty();
	}
	return isDirty();
    }

    @Override
    public void onTileLoaded() {
	onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<T, SinglePropertyType<T, ?>> prop, T loadedValue) {
	onChange.accept((SingleProperty<T>) prop, loadedValue);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
	return super.equals(obj);
    }

    @Override
    public String toString() {
	return value.toString();
    }

}
