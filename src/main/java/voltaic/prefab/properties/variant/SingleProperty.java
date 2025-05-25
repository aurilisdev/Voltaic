package voltaic.prefab.properties.variant;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.world.level.block.Block;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.SinglePropertyType;

/**
 * A wrapper class designed to monitor a value and take action when it changes
 * This variant assumes a single variable instead of a group of variables such as an array or list
 *
 * @param <T> The type of the property
 * @author skip999
 * @author AurilisDev
 */
public class SingleProperty<T> extends AbstractProperty<T, SinglePropertyType<T, ?>> {

    private boolean alreadySynced = false;

    //This fires when the property has had a value set and that value is different from the value the property currently has
    //The property contains the new value and val represents the old value. Level may or may not be present.
    private BiConsumer<SingleProperty<T>, T> onChange = (prop, val) -> {
    };
    //this fires when the owning tile has been loaded. This fires on both the client and server-side, and Level is present
    private Consumer<SingleProperty<T>> onTileLoaded = prop -> {
    };

    public SingleProperty(SinglePropertyType<T, ?> type, String name, T defaultValue) {
        super(type, name, defaultValue);
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

        if (alreadySynced) {
            return;
        }
        /*
        if (!updated.getClass().equals(value.getClass())) {
            throw new RuntimeException("Value " + updated + " being set for " + getName() + " on tile " + getPropertyManager().getOwner() + " is an invalid data type!");
        }

         */
        checkForChange((T) updated);
        T old = getValue();
        value = (T) updated;
        PropertyManager manager = getPropertyManager();
        if (isDirty() && manager.getOwner().getLevel() != null) {
            if (!manager.getOwner().getLevel().isClientSide()) {
                if (shouldUpdateOnChange()) {
                    alreadySynced = true;
                    manager.getOwner().getLevel().sendBlockUpdated(manager.getOwner().getBlockPos(), manager.getOwner().getBlockState(), manager.getOwner().getBlockState(), Block.UPDATE_CLIENTS);
                    manager.getOwner().setChanged();
                    alreadySynced = false;
                }
                manager.setDirty(this);
            } else if(shouldUpdateServer()) {
                updateServer();
            }
            onChange.accept(this, old);
        }
    }



    public void copy(SingleProperty<T> other) {
        T otherVal = other.getValue();
        if (otherVal == null) {
            return;
        }
        setValue(otherVal);
    }

    private boolean checkForChange(T updated) {
        boolean shouldUpdate = value == null && updated != null;
        if (value != null && updated != null) {
            shouldUpdate = !getType().isEqual(value, updated);
        }
        if (shouldUpdate) {
            setDirty();
        }
        return shouldUpdate;
    }

    /**
     * This method is called by the tile on the server once Level is present
     */
    public void onTileLoaded() {
        onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<T, SinglePropertyType<T, ?>> prop, T loadedValue) {
        onChange.accept((SingleProperty<T>) prop, loadedValue);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }

}
