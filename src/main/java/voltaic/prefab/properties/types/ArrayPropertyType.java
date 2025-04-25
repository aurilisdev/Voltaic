package voltaic.prefab.properties.types;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import voltaic.api.codec.StreamCodec;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArrayPropertyType<TYPE, BUFFERTYPE extends ByteBuf> implements IPropertyType<TYPE[], BUFFERTYPE> {

    private final BiPredicate<TYPE, TYPE> singleComparison;
    private final BiPredicate<TYPE[], TYPE[]> comparison;
    private final StreamCodec<BUFFERTYPE, TYPE[]> packetCodec;
    private final Consumer<TagWriter<TYPE[]>> writeToNbt;
    private final Function<TagReader<TYPE[]>, TYPE[]> readFromNbt;

    public ArrayPropertyType(@Nonnull BiPredicate<TYPE, TYPE> singleComparison, StreamCodec<BUFFERTYPE, TYPE> singlePacketCodec, Codec<TYPE> singleNbtCodec, TYPE[] defaultArr, TYPE defaultValue) {

        this.singleComparison = singleComparison;

        this.comparison = (arr1, arr2) -> {

            if(arr1 == null || arr2 == null) {
                return false;
            }

            if(arr1.length != arr2.length) {
                return false;
            }

            for(int i = 0; i < arr1.length; i++) {

                if(!singleComparison.test(arr1[i], arr2[i])) {
                    return false;
                }

            }

            return true;

        };

        packetCodec = new StreamCodec<BUFFERTYPE, TYPE[]>() {

            @Override
            public TYPE[] decode(BUFFERTYPE buffer) {

                int size = buffer.readInt();

                TYPE[] newArr = Arrays.copyOf(defaultArr, size);

                Arrays.fill(newArr, defaultValue);

                for(int i = 0; i < size; i++) {

                    newArr[i] = singlePacketCodec.decode(buffer);

                }

                return newArr;
            }

            @Override
            public void encode(BUFFERTYPE buffer, TYPE[] value) {

                buffer.writeInt(value.length);

                for(int i = 0; i < value.length; i++) {

                    singlePacketCodec.encode(buffer, value[i]);

                }

            }
        };

        writeToNbt = writer -> {

            CompoundTag tag = new CompoundTag();

            TYPE[] arr = writer.prop().getValue();

            tag.putInt("size", arr.length);

            for(int i = 0; i < arr.length; i++) {

                final int index = i;

                singleNbtCodec.encode(arr[i], NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).result().ifPresent(nbt -> tag.put("" + index, nbt));

            }

            writer.tag().put(writer.prop().getName(), tag);

        };

        readFromNbt = reader -> {

            CompoundTag data = reader.tag().getCompound(reader.prop().getName());

            if(!data.contains("size")) {
                return reader.prop().getValue();
            }

            int size = data.getInt("size");

            if(size <= 0) {
                return reader.prop().getValue();
            }

            TYPE[] newArr = Arrays.copyOf(defaultArr, size);

            Arrays.fill(newArr, defaultValue);

            for(int i = 0; i < size; i++) {

                final int index = i;

                singleNbtCodec.decode(NbtOps.INSTANCE, data.get("" + i)).result().ifPresent(pair -> newArr[index] = pair.getFirst());

            }

            return newArr;

        };

    }

    @Override
    public StreamCodec<BUFFERTYPE, TYPE[]> getPacketCodec() {
        return packetCodec;
    }

    @Override
    public void writeToTag(TagWriter<TYPE[]> writer) {
        writeToNbt.accept(writer);
    }

    @Override
    public TYPE[] readFromTag(TagReader<TYPE[]> reader) {
        return readFromNbt.apply(reader);
    }

    @Override
    public boolean isEqual(TYPE[] currentValue, TYPE[] newValue) {
        return comparison.test(currentValue, newValue);
    }

    public boolean isSingleEqual(TYPE val1, TYPE val2) {
        return singleComparison.test(val1, val2);
    }

}
