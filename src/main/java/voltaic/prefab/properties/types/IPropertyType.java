package voltaic.prefab.properties.types;

import net.minecraft.nbt.CompoundTag;
import voltaic.api.codec.StreamCodec;
import voltaic.prefab.properties.variant.AbstractProperty;

/**
 * Interface to allow for custom property types in dependent mods
 *
 * @author skip999
 */

public interface IPropertyType<TYPE, BUFFERTYPE> {

    default boolean isEqual(TYPE currentValue, TYPE newValue) {
        return currentValue.equals(newValue);
    }

    public StreamCodec<BUFFERTYPE, TYPE> getPacketCodec();

    public void writeToTag(TagWriter<TYPE> writer);

    public TYPE readFromTag(TagReader<TYPE> reader);

    public static final record TagWriter<TYPE>(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundTag tag) {


    }
    public static final record TagReader<TYPE>(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundTag tag) {

    }

}
