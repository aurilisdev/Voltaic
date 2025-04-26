package voltaic.prefab.properties.variant;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.function.TriConsumer;
import voltaic.Voltaic;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.ArrayPropertyType;
import voltaic.prefab.properties.types.IPropertyType;

import java.util.Arrays;
import java.util.function.Consumer;

public class ArrayProperty<T> extends AbstractProperty<T[], ArrayPropertyType<T, ?>> {

    private boolean alreadySynced = false;

    //This fires when the property has had a value set and that value is different from the value the property currently has
    //The property contains the new value and val represents the old value. Level may or may not be present.
    private TriConsumer<ArrayProperty<T>, T[], Integer> onChange = (prop, val, index) -> {
    };
    //this fires when the owning tile has been loaded. This fires on both the client and server-side, and Level is present
    private Consumer<ArrayProperty<T>> onTileLoaded = (prop) -> {
    };

    public ArrayProperty(ArrayPropertyType<T, ?> type, String name, T[] defaultValue) {
        super(type, name, defaultValue);
    }

    @Override
    public void onTileLoaded() {
        onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<T[], ArrayPropertyType<T, ?>> prop, T[] loadedValue) {
        onChange.accept((ArrayProperty<T>) prop, loadedValue, -1);
    }

    public ArrayProperty<T> onChange(TriConsumer<ArrayProperty<T>, T[], Integer> event) {
        onChange = onChange.andThen(event);
        return this;
    }

    public ArrayProperty<T> onTileLoaded(Consumer<ArrayProperty<T>> event) {
        onTileLoaded = onTileLoaded.andThen(event);
        return this;
    }

    public void setValue(Object updated, int index) {

        if (alreadySynced) {
            return;
        }
        /*
        if (!updated.getClass().equals(value.getClass())) {
            throw new RuntimeException("Value " + updated + " being set for " + getName() + " on tile " + getPropertyManager().getOwner() + " is an invalid data type!");
        }

         */
        checkForChange((T) updated, index);
        T[] old = Arrays.copyOf(getValue(), getValue().length);
        value[index] = (T) updated;
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
            onChange.accept(this, old, index);
        }
    }



    public void copy(ArrayProperty<T> other) {
        T[] otherVal = other.getValue();
        if (otherVal == null) {
            return;
        }
        overwriteValue(otherVal);
    }

    private boolean checkForChange(T updated, int index) {
        boolean shouldUpdate = value[index] == null && updated != null;
        if (value[index] != null && updated != null) {
            shouldUpdate = !getType().isSingleEqual(value[index], updated);
        }
        if (shouldUpdate) {
            setDirty();
        }
        return shouldUpdate;
    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
        try {
            T[] data = (T[]) getType().readFromTag(new IPropertyType.TagReader(this, tag, registries));
            if (data != null) {
                value = data;
                onLoadedFromTag(this, value);
            }
        } catch (Exception e) {
            Voltaic.LOGGER.info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
        }
    }

}
