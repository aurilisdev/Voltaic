package voltaic.prefab.properties.variant;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.function.TriConsumer;
import voltaic.Voltaic;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.IPropertyType;
import voltaic.prefab.properties.types.ListPropertyType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListProperty<T> extends AbstractProperty<List<T>, ListPropertyType<T, ?>> {

    private boolean alreadySynced = false;

    //This fires when the property has had a value set and that value is different from the value the property currently has
    //The property contains the new value and val represents the old value. Level may or may not be present.

    private TriConsumer<ListProperty<T>, List<T>, Integer> onChange = (prop, val, index) -> {
    };
    //this fires when the owning tile has been loaded. This fires on both the client and server-side, and Level is present
    private Consumer<ListProperty<T>> onTileLoaded = (prop) -> {
    };

    public ListProperty(ListPropertyType<T, ?> type, String name, List<T> defaultValue) {
        super(type, name, defaultValue);
    }

    @Override
    public void onTileLoaded() {
        onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<List<T>, ListPropertyType<T, ?>> prop, List<T> loadedValue) {
        onChange.accept((ListProperty<T>) prop, loadedValue, -1);
    }

    public ListProperty<T> onChange(TriConsumer<ListProperty<T>, List<T>, Integer> event) {
        onChange = onChange.andThen(event);
        return this;
    }

    public ListProperty<T> onTileLoaded(Consumer<ListProperty<T>> event) {
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
        List<T> old = new ArrayList<>(getValue());
        value.set(index, (T) updated);
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

    public void addValue(Object updated) {
        if (alreadySynced || updated == null) {
            return;
        }

        List<T> old = new ArrayList<>(getValue());

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
            onChange.accept(this, old, -1);
        }

    }

    public void addValue(Object updated, int index) {

        if (alreadySynced || updated == null) {
            return;
        }

        List<T> old = new ArrayList<>(getValue());

        getValue().add(index, (T) updated);

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
            onChange.accept(this, old, index);
        }

    }

    public void addValues(List<?> updated) {
        if (alreadySynced || updated == null) {
            return;
        }

        List<T> old = new ArrayList<>(getValue());

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
            onChange.accept(this, old, -1);
        }

    }

    public void removeValue(int index) {

        if (alreadySynced || index >= getValue().size()) {
            return;
        }

        List<T> old = new ArrayList<>(getValue());

        getValue().remove(index);

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
            onChange.accept(this, old, index);
        }

    }

    public void removeValue(T value) {

        if (alreadySynced || !getValue().contains(value)) {
            return;
        }

        List<T> old = new ArrayList<>(getValue());

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
            onChange.accept(this, old, -1);
        }

    }

    public void wipeList() {

        PropertyManager manager = getPropertyManager();

        List<T> old = new ArrayList<>(getValue());

        overwriteValue(new ArrayList<>());

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
            onChange.accept(this, old, -1);
        }
    }


    public void copy(ListProperty<T> other) {
        List<T> otherVal = other.getValue();
        if (otherVal == null) {
            return;
        }
        overwriteValue(otherVal);
    }

    private boolean checkForChange(T updated, int index) {
        boolean shouldUpdate = value.get(index) == null && updated != null;
        if (value.get(index) != null && updated != null) {
            shouldUpdate = !getType().isSingleEqual(value.get(index), updated);
        }
        if (shouldUpdate) {
            setDirty();
        }
        return shouldUpdate;
    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
        try {
            List<T> data = (List<T>) getType().readFromTag(new IPropertyType.TagReader(this, tag, registries));
            if (data != null) {
                value = data;
                onLoadedFromTag(this, value);
            }
        } catch (Exception e) {
            Voltaic.LOGGER.info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
        }
    }

}
