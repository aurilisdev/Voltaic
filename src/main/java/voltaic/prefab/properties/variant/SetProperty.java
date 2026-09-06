package voltaic.prefab.properties.variant;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import voltaic.Voltaic;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.IPropertyType.TagReader;
import voltaic.prefab.properties.types.SetPropertyType;
import voltaic.prefab.tile.GenericTile;

public class SetProperty<T> extends AbstractProperty<HashSet<T>, SetPropertyType<T, ?>> {

    private boolean alreadySynced = false;

    // This fires when the property has had a value set and that value is different
    // from the value the property currently has
    // The property contains the new value and val represents the old value. Level
    // may or may not be present.

    private BiConsumer<SetProperty<T>, HashSet<T>> onChange = (prop, val) -> {};
    // this fires when the owning tile has been loaded. This fires on both the
    // client and server-side, and Level is present
    private Consumer<SetProperty<T>> onTileLoaded = prop -> {};

    public SetProperty(PropertyManager manager, SetPropertyType<T, ?> type, String name, HashSet<T> defaultValue) {
	super(manager, type, name, defaultValue);
    }

    @Override
    public void onTileLoaded() {
	onTileLoaded.accept(this);
    }

    @Override
    public void onLoadedFromTag(AbstractProperty<HashSet<T>, SetPropertyType<T, ?>> prop, HashSet<T> loadedValue) {
	onChange.accept((SetProperty<T>) prop, loadedValue);
    }

    public SetProperty<T> onChange(BiConsumer<SetProperty<T>, HashSet<T>> event) {
	onChange = onChange.andThen(event);
	return this;
    }

    public SetProperty<T> onTileLoaded(Consumer<SetProperty<T>> event) {
	onTileLoaded = onTileLoaded.andThen(event);
	return this;
    }

    @Override
    public void setValue(Object updated) {
	if (alreadySynced)
	    return;

	HashSet<T> newValue = (HashSet<T>) updated;

	if (!markDirtyIfChanged(newValue))
	    return;

	HashSet<T> old = new HashSet<>(getValue());
	value = newValue;
	applyChange(old);
    }

    public void addValue(Object updated) {
	if (alreadySynced)
	    return;

	T typedValue = (T) updated;

	if (getValue().contains(typedValue))
	    return;

	HashSet<T> old = new HashSet<>(getValue());
	getValue().add(typedValue);
	applyChange(old);
    }

    public void addValues(Set<?> updated) {
	if (alreadySynced || updated.isEmpty())
	    return;

	HashSet<T> old = new HashSet<>(getValue());
	boolean changed = false;

	for (Object object : updated) {
	    if (object != null) {
		changed |= getValue().add((T) object);
	    }
	}

	if (changed) {
	    applyChange(old);
	}
    }

    public void removeValue(T value) {
	if (alreadySynced || !getValue().contains(value))
	    return;

	HashSet<T> old = new HashSet<>(getValue());
	getValue().remove(value);
	applyChange(old);
    }

    public void wipeSet() {
	if (alreadySynced || getValue().isEmpty())
	    return;

	HashSet<T> old = new HashSet<>(getValue());
	overwriteValue(new HashSet<>());
	applyChange(old);
    }

    public void copy(SetProperty<T> other) {
	HashSet<T> otherVal = other.getValue();
	overwriteValue(new HashSet<>(otherVal));
    }

    private boolean markDirtyIfChanged(HashSet<T> updated) {
	boolean changed = !getType().isEqual(value, updated);

	if (changed) {
	    setDirty();
	}

	return changed;
    }

    private void applyChange(HashSet<T> old) {
	setDirty();

	PropertyManager manager = getPropertyManager();
	GenericTile owningEntity = manager.getOwner();
	Level level = owningEntity.getLevel();
	if (level == null)
	    return;

	if (!level.isClientSide()) {
	    manager.setDirty(this);

	    if (shouldUpdateOnChange()) {
		alreadySynced = true;

		try {
		    level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			    owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		    owningEntity.setChanged();
		} finally {
		    alreadySynced = false;
		}
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}

	onChange.accept(this, old);
    }

    @Override
    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
	try {
	    HashSet<T> data = value = getType().readFromTag(new TagReader<>(this, tag, registries));
	    onLoadedFromTag(this, data);
	} catch (Exception e) {
	    Voltaic.LOGGER
		    .info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
	}
    }

}
