package voltaic.prefab.properties.variant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import voltaic.Voltaic;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.IPropertyType;
import voltaic.prefab.properties.types.SetPropertyType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SetProperty<T> extends AbstractProperty<HashSet<T>, SetPropertyType<T, ?>> {

    private boolean alreadySynced = false;

    //This fires when the property has had a value set and that value is different from the value the property currently has
    //The property contains the new value and val represents the old value. Level may or may not be present.

    private BiConsumer<SetProperty<T>, HashSet<T>> onChange = (prop, val) -> {
    };
    //this fires when the owning tile has been loaded. This fires on both the client and server-side, and Level is present
    private Consumer<SetProperty<T>> onTileLoaded = (prop) -> {
    };

    public SetProperty(SetPropertyType<T, ?> type, String name, HashSet<T> defaultValue) {
        super(type, name, defaultValue);
    }

    @Override
    public void onTileLoaded() {
        onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<HashSet<T>, SetPropertyType<T, ?>> prop, HashSet<T> loadedValue) {
        onChange.accept((SetProperty<T>) prop, loadedValue);
    }

    public void addValue(Object updated) {
        if (alreadySynced || updated == null || getValue().contains(updated)) {
            return;
        }

        HashSet<T> old = new HashSet<>(getValue());

        getValue().add((T) updated);

        PropertyManager manager = getPropertyManager();

        setDirty();

        if (manager.getOwner().getLevel() != null) {
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

    public void addValues(Set<?> updated) {

        if (alreadySynced || updated == null) {
            return;
        }

        HashSet<T> old = new HashSet<>(getValue());

        for(Object obj : updated) {
            getValue().add((T) obj);
        }

        PropertyManager manager = getPropertyManager();

        setDirty();

        if (manager.getOwner().getLevel() != null) {
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

    public void removeValue(T value) {

        if (alreadySynced || !getValue().contains(value)) {
            return;
        }

        HashSet<T> old = new HashSet<>(getValue());

        getValue().remove(value);

        PropertyManager manager = getPropertyManager();

        setDirty();

        if (manager.getOwner().getLevel() != null) {
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

    public void wipeSet() {

        PropertyManager manager = getPropertyManager();

        HashSet<T> old = new HashSet<>(getValue());

        overwriteValue(new HashSet<>());

        setDirty();

        if (manager.getOwner().getLevel() != null) {
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


    public void copy(SetProperty<T> other) {
        HashSet<T> otherVal = other.getValue();
        if (otherVal == null) {
            return;
        }
        overwriteValue(otherVal);
    }

    public void loadFromTag(CompoundTag tag) {
        try {
            HashSet<T> data = (HashSet<T>) getType().readFromTag(new IPropertyType.TagReader(this, tag));
            if (data != null) {
                value = data;
                onLoadedFromTag(this, value);
            }
        } catch (Exception e) {
            Voltaic.LOGGER.info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
        }
    }

}
