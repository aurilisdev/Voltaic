package voltaic.prefab.properties.variant;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.Voltaic;
import voltaic.common.packet.types.server.PacketSendUpdatePropertiesServer;
import voltaic.prefab.properties.types.IPropertyType;
import voltaic.prefab.properties.PropertyManager;

public abstract class AbstractProperty<T, PROPERTYTYPE extends IPropertyType> {

    private PropertyManager manager;
    private final PROPERTYTYPE type;
    private boolean shouldSave = true;
    private boolean shouldUpdateClient = true;
    //set this if you want to update a property without having a tile tick
    //otherwise the property will be synced to the client upon change at the end of the tile's tick
    private boolean shouldUpdateOnChange = false;
    private boolean shouldUpdateServer = true;
    private final String name;

    private boolean isDirty = true;

    protected T value;

    private int index = 0;

    public AbstractProperty(PROPERTYTYPE type, String name, T defaultValue) {
        this.type = type;
        if (name == null || name.length() == 0) {
            throw new RuntimeException("The property's name cannot be null or empty");
        }
        this.name = name;
        value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean shouldSave() {
        return shouldSave;
    }

    public <A extends AbstractProperty<T, PROPERTYTYPE>> A setNoSave() {
        shouldSave = false;
        return (A) this;
    }

    public boolean shouldUpdateClient() {
        return shouldUpdateClient;
    }

    public <A extends AbstractProperty<T, PROPERTYTYPE>> A setNoUpdateClient() {
        shouldUpdateClient = false;
        return (A) this;
    }

    public boolean shouldUpdateOnChange() {
        return shouldUpdateOnChange;
    }

    public <A extends AbstractProperty<T, PROPERTYTYPE>> A setShouldUpdateOnChange() {
        shouldUpdateOnChange = true;
        return (A) this;
    }

    public boolean shouldUpdateServer() {
        return shouldUpdateServer;
    }

    public <A extends AbstractProperty<T, PROPERTYTYPE>> A setNoUpdateServer() {
        shouldUpdateServer = false;
        return (A) this;
    }

    public PROPERTYTYPE getType() {
        return type;
    }

    public int index() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PropertyManager getPropertyManager() {
        return manager;
    }

    public void setManager(PropertyManager manager) {
        this.manager = manager;
    }

    public void saveToTag(CompoundTag tag, HolderLookup.Provider registries) {
        try {
            getType().writeToTag(new IPropertyType.TagWriter<>(this, tag, registries));
        } catch (Exception e) {
            Voltaic.LOGGER.info("Catching error while saving property " + getName() + " from NBT. Error: " + e.getMessage());
        }
    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
        try {
            T data = (T) getType().readFromTag(new IPropertyType.TagReader(this, tag, registries));
            if (data != null) {
                value = data;
                onLoadedFromTag(this, value);
            }
        } catch (Exception e) {
            Voltaic.LOGGER.info("Catching error while loading property " + getName() + " from NBT. Error: " + e.getMessage());
        }
    }
    public abstract void onTileLoaded();

    public abstract void onLoadedFromTag(AbstractProperty<T, PROPERTYTYPE> prop, T loadedValue);

    public T getValue() {
        return value;
    }

    /**
     * Documentation note: This merely forces the value of the property and does not indicate that it is dirty!
     *
     * @param newVal
     */
    @Deprecated(since = "Be careful when you use this!")
    public void overwriteValue(T newVal) {
        value = newVal;
    }

    public void setDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    /**
     * This method should be used only as a last resort
     * <p>
     * If it is a single object (FluidStack for example), then do NOT used this method
     */
    @Deprecated(since = "This should be used when working with arrays")
    public void forceDirtyForManager() {
        if (!manager.getOwner().getLevel().isClientSide()) {
            manager.setDirty(this);
        } else {
            CompoundTag data = new CompoundTag();
            saveToTag(data, manager.getOwner().getLevel().registryAccess());
            PacketDistributor.sendToServer(new PacketSendUpdatePropertiesServer(data, index(), manager.getOwner().getBlockPos()));
        }
    }

    public void clean() {
        isDirty = false;
    }

    public void updateServer() {
        CompoundTag data = new CompoundTag();
        saveToTag(data, manager.getOwner().getLevel().registryAccess());
        PacketDistributor.sendToServer(new PacketSendUpdatePropertiesServer(data, index(), manager.getOwner().getBlockPos()));
    }

}
