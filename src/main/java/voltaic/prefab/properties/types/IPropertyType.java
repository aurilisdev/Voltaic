package voltaic.prefab.properties.types;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import voltaic.prefab.properties.variant.AbstractProperty;

public interface IPropertyType<TYPE, BUFFERTYPE> {

    default boolean isEqual(TYPE currentValue, TYPE newValue) {
	return currentValue.equals(newValue);
    }

    public StreamCodec<BUFFERTYPE, TYPE> getPacketCodec();

    public void writeToTag(TagWriter<TYPE> writer);

    @Nullable
    public TYPE readFromTag(TagReader<TYPE> reader);

    public static final record TagWriter<TYPE>(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundTag tag,
	    HolderLookup.Provider registries) {

    }

    public static final record TagReader<TYPE>(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundTag tag,
	    HolderLookup.Provider registries) {

    }

}
