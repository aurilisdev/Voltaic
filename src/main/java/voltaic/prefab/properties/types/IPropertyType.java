package voltaic.prefab.properties.types;

import net.minecraft.nbt.CompoundNBT;
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

	public static final class TagWriter<TYPE> {

		private final AbstractProperty<TYPE, ? extends IPropertyType> prop;
		private final CompoundNBT tag;

		public TagWriter(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundNBT tag) {
			this.prop = prop;
			this.tag = tag;
		}

		public AbstractProperty<TYPE, ? extends IPropertyType> prop() {
			return prop;
		}

		public CompoundNBT tag() {
			return tag;
		}

	}

	public static final class TagReader<TYPE> {

		private final AbstractProperty<TYPE, ? extends IPropertyType> prop;
		private final CompoundNBT tag;

		public TagReader(AbstractProperty<TYPE, ? extends IPropertyType> prop, CompoundNBT tag) {
			this.prop = prop;
			this.tag = tag;
		}

		public AbstractProperty<TYPE, ? extends IPropertyType> prop() {
			return prop;
		}

		public CompoundNBT tag() {
			return tag;
		}

	}

}
