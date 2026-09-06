package voltaic.prefab.properties;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import voltaic.Voltaic;
import voltaic.prefab.properties.types.IPropertyType;
import voltaic.prefab.properties.variant.AbstractProperty;
import voltaic.prefab.tile.GenericTile;

public class PropertyManager {

    public static final String NBT_KEY = "propertydata";

    private final GenericTile owner;

    private final ArrayList<AbstractProperty> properties = new ArrayList<>();

    private final HashSet<AbstractProperty> dirtyPropertiesDirect = new HashSet<>();

    private boolean isDirty = false;

    public PropertyManager(GenericTile owner) {
	this.owner = owner;
    }

    public <T extends AbstractProperty> T addProperty(T prop) {
	properties.add(prop);
	prop.setManager(this);
	prop.setIndex(properties.size() - 1);
	return prop;
    }

    public ArrayList<AbstractProperty> getProperties() {
	return properties;
    }

    public void flushDirtyPropsToTag(CompoundTag tag, HolderLookup.Provider registries) {
	for (AbstractProperty prop : dirtyPropertiesDirect) {
	    prop.saveToTag(tag, registries);
	}
	clean();
    }

    public void saveAllPropsForClientSync(CompoundTag tag, HolderLookup.Provider registries) {
	for (AbstractProperty prop : properties) {
	    if (prop.shouldUpdateClient()) {
		prop.saveToTag(tag, registries);
	    }
	}
    }

    public void clean() {
	isDirty = false;

	for (AbstractProperty property : dirtyPropertiesDirect) {
	    property.clean();
	}
	dirtyPropertiesDirect.clear();
    }

    public boolean isDirty() {
	return isDirty;
    }

    public void setDirty(AbstractProperty dirtyProp) {
	isDirty = true;
	if (dirtyProp.shouldUpdateClient()) {
	    dirtyPropertiesDirect.add(dirtyProp);
	}
    }

    @Override
    public String toString() {
	String string = "";
	for (int i = 0; i < properties.size(); i++) {
	    string = string + i + ": " + properties.get(i).toString() + "\n";
	}
	return string;
    }

    public GenericTile getOwner() {
	return owner;
    }

    public void saveToTag(CompoundTag tag, HolderLookup.Provider registries) {
	for (AbstractProperty prop : getProperties()) {
	    if (prop.shouldSave()) {
		prop.saveToTag(tag, registries);
	    }
	}
    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
	for (AbstractProperty prop : getProperties()) {
	    if (prop.shouldSave() && tag.contains(prop.getName())) {
		prop.loadFromTag(tag, registries);

		tag.remove(prop.getName());
	    }
	}
    }

    public void loadDataFromClient(ServerLevel serverLevel, int index, CompoundTag data) {
	if (index < 0 || index >= properties.size()) {
	    Voltaic.LOGGER.error("The tile at " + owner.getBlockPos()
		    + " has a differently sized property list than what was declared by the packet");
	    return;
	}

	AbstractProperty prop = properties.get(index);
	if (!prop.shouldUpdateServer()) {
	    Voltaic.LOGGER.info("The property " + prop.getName() + " does not accept updates from the client");
	    return;
	}

	if (owner.getLevel() == null) {
	    Voltaic.LOGGER.info("The property " + prop.getName() + " that sent data to the tile at "
		    + owner.getBlockPos() + " encountered a null level. The data was not loaded");
	    return;
	}

	Object value = prop.getType()
		.readFromTag(new IPropertyType.TagReader(prop, data, serverLevel.registryAccess()));
	prop.setValue(value);
    }

    public void onTileLoaded() {
	for (AbstractProperty property : properties) {
	    property.onTileLoaded();
	}
    }
}
