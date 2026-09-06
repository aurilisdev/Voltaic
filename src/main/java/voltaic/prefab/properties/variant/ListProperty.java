package voltaic.prefab.properties.variant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriConsumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import voltaic.Voltaic;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.IPropertyType.TagReader;
import voltaic.prefab.properties.types.ListPropertyType;
import voltaic.prefab.tile.GenericTile;

public class ListProperty<T> extends AbstractProperty<List<T>, ListPropertyType<T, ?>> {

    private boolean alreadySynced = false;

    // This fires when the property has had a value set and that value is different
    // from the value the property currently has
    // The property contains the new value and val represents the old value. Level
    // may or may not be present.

    private TriConsumer<ListProperty<T>, List<T>, Integer> onChange = (prop, val, index) -> {};
    // this fires when the owning tile has been loaded. This fires on both the
    // client and server-side, and Level is present
    private Consumer<ListProperty<T>> onTileLoaded = prop -> {};

    public ListProperty(PropertyManager manager, ListPropertyType<T, ?> type, String name, List<T> defaultValue) {
	super(manager, type, name, defaultValue);
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

    @Override
    public void setValue(Object updated) {
	if (alreadySynced || !markDirtyIfChanged((List<T>) updated))
	    return;

	List<T> old = new ArrayList<>(getValue());
	value = (List<T>) updated;
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
		manager.getOwner().setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, -1);
    }

    public void setValue(Object updated, int index) {
	if (alreadySynced || !markDirtyIfChanged((T) updated, index))
	    return;

	List<T> old = new ArrayList<>(getValue());
	value.set(index, (T) updated);
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
		manager.getOwner().setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, index);
    }

    public void addValue(Object updated) {
	if (alreadySynced)
	    return;
	List<T> old = new ArrayList<>(getValue());
	getValue().add((T) updated);
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		manager.getOwner().setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, -1);

    }

    public void addValue(Object updated, int index) {
	if (alreadySynced)
	    return;
	List<T> old = new ArrayList<>(getValue());
	getValue().add(index, (T) updated);
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, index);

    }

    public void addValues(List<?> updated) {
	if (alreadySynced)
	    return;

	List<T> old = new ArrayList<>(getValue());
	for (Object obj : updated) {
	    getValue().add((T) obj);
	}
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, -1);
    }

    public void removeValue(int index) {
	if (alreadySynced || index >= getValue().size())
	    return;

	List<T> old = new ArrayList<>(getValue());
	getValue().remove(index);
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, index);
    }

    public void removeValue(T value) {
	if (alreadySynced || !getValue().contains(value))
	    return;

	List<T> old = new ArrayList<>(getValue());
	getValue().remove(value);
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, -1);
    }

    public void wipeList() {
	List<T> old = new ArrayList<>(getValue());
	overwriteValue(new ArrayList<>());
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
		level.sendBlockUpdated(owningEntity.getBlockPos(), owningEntity.getBlockState(),
			owningEntity.getBlockState(), Block.UPDATE_CLIENTS);
		owningEntity.setChanged();
		alreadySynced = false;
	    }
	} else if (shouldUpdateServer()) {
	    updateServer();
	}
	onChange.accept(this, old, -1);
    }

    public void copy(ListProperty<T> other) {
	List<T> otherVal = other.getValue();
	overwriteValue(otherVal);
    }

    private boolean markDirtyIfChanged(List<T> updated) {
	if (!getType().isEqual(value, updated)) {
	    setDirty();
	}
	return isDirty();
    }

    private boolean markDirtyIfChanged(T updated, int index) {
	if (!getType().isSingleEqual(value.get(index), updated)) {
	    setDirty();
	}
	return isDirty();
    }

    @Override
    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
	try {
	    List<T> data = value = getType().readFromTag(new TagReader<>(this, tag, registries));
	    onLoadedFromTag(this, data);
	} catch (Exception e) {
	    Voltaic.LOGGER
		    .info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
	}
    }

}
