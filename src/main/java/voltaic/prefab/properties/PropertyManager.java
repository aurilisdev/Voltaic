package voltaic.prefab.properties;

import java.util.ArrayList;
import java.util.HashSet;

import voltaic.Voltaic;
import voltaic.prefab.properties.types.IPropertyType;
import voltaic.prefab.properties.variant.AbstractProperty;
import voltaic.prefab.tile.GenericTile;
import net.minecraft.nbt.CompoundTag;

/**
 * A wrapper class designed to manage data properties on a tile
 * 
 * @author AurilisDev
 * @author skip999
 *
 */
public class PropertyManager {

	public static final String NBT_KEY = "propertydata";

	private final GenericTile owner;

	private ArrayList<AbstractProperty> properties = new ArrayList<>();

	//private HashSet<PropertyWrapper> dirtyProperties = new HashSet<>();
	private HashSet<AbstractProperty> dirtyPropertiesDirect = new HashSet<>();

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

	public void saveDirtyPropsToTag(CompoundTag tag) {
		for(AbstractProperty prop : dirtyPropertiesDirect){
			prop.saveToTag(tag);
		}
	}

	public void saveAllPropsForClientSync(CompoundTag tag) {
		for(AbstractProperty prop : properties){
			if(prop.shouldUpdateClient()) {
				prop.saveToTag(tag);
			}
		}
	}

	public void clean() {
		isDirty = false;
		/*
		dirtyProperties.forEach(wrapper -> {
			wrapper.property.clean();
		});
		 */
		for (AbstractProperty property : dirtyPropertiesDirect) {
			property.clean();
		}
		//dirtyProperties.clear();
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

	public void saveToTag(CompoundTag tag) {
		for (AbstractProperty prop : getProperties()) {
			if (prop.shouldSave()) {
				prop.saveToTag(tag);
			}
		}
	}

	public void loadFromTag(CompoundTag tag) {
		for (AbstractProperty prop : getProperties()) {
			if (prop.shouldSave() && tag.contains(prop.getName())) {
				prop.loadFromTag(tag);

				tag.remove(prop.getName());
			}
		}
	}

	public void loadDataFromClient(int index, CompoundTag data) {
		if(index >= properties.size()) {
			Voltaic.LOGGER.error("The tile at " + owner.getBlockPos() + " has a differently sized property list than what was declared by the packet");
			return;
		}
		AbstractProperty prop = properties.get(index);
		if(owner == null) {
			Voltaic.LOGGER.info("The property " + prop.getName() + " is sending data to a null tile");
			return;
		}
		if(owner.getLevel() == null){
			Voltaic.LOGGER.info("The property " + prop.getName() + " that sent data to the tile at " + owner.getBlockPos() + " encountered a null level. The data was not loaded");
			return;
		}
		prop.overwriteValue(prop.getType().readFromTag(new IPropertyType.TagReader(prop, data)));
		owner.setChanged();
	}

	public void onTileLoaded() {
		for (AbstractProperty property : properties) {
			property.onTileLoaded();
		}
	}

	/*
	public static record PropertyWrapper(int index, IPropertyType type, Object value, @Nullable Property<?> property) {

	}

	 */
}
