package voltaic.prefab.properties.types;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import voltaic.api.codec.StreamCodec;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListPropertyType <TYPE, BUFFERTYPE extends ByteBuf> implements IPropertyType<List<TYPE>, BUFFERTYPE> {

    private final BiPredicate<TYPE, TYPE> singleComparison;
    private final BiPredicate<List<TYPE>, List<TYPE>> comparison;
    private final StreamCodec<BUFFERTYPE, List<TYPE>> packetCodec;
    private final Consumer<TagWriter<List<TYPE>>> writeToNbt;
    private final Function<TagReader<List<TYPE>>, List<TYPE>> readFromNbt;

    public ListPropertyType(@Nonnull BiPredicate<TYPE, TYPE> singleComparison, StreamCodec<BUFFERTYPE, TYPE> singlePacketCodec, Codec<TYPE> singleNbtCodec, TYPE defaultValue) {

        this.singleComparison = singleComparison;

        this.comparison = (list1, list2) -> {

            if (list1 == null || list2 == null) {
                return false;
            }

            if (list1.size() != list2.size()) {
                return false;
            }

            for (int i = 0; i < list1.size(); i++) {

                if (!singleComparison.test(list1.get(i), list2.get(i))) {
                    return false;
                }

            }

            return true;

        };

        packetCodec = new StreamCodec<>() {

            @Override
            public List<TYPE> decode(BUFFERTYPE buffer) {

                int size = buffer.readInt();

                List<TYPE> list = new ArrayList<>(size);

                Collections.fill(list, defaultValue);

                for (int i = 0; i < size; i++) {

                    list.set(i, singlePacketCodec.decode(buffer));

                }

                return list;
            }

            @Override
            public void encode(BUFFERTYPE buffer, List<TYPE> value) {

                buffer.writeInt(value.size());

                for (int i = 0; i < value.size(); i++) {

                    singlePacketCodec.encode(buffer, value.get(i));

                }

            }
        };

        writeToNbt = writer -> {

            CompoundTag tag = new CompoundTag();

            List<TYPE> list = writer.prop().getValue();

            tag.putInt("size", list.size());

            for (int i = 0; i < list.size(); i++) {

                final int index = i;

                singleNbtCodec.encode(list.get(i), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).result().ifPresent(nbt -> tag.put("" + index, nbt));

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

            List<TYPE> list = new ArrayList<>(size);

            for(int i = 0; i < size; i++) {
                list.add(defaultValue);
            }

            for (int i = 0; i < size; i++) {

                final int index = i;

                singleNbtCodec.decode(NbtOps.INSTANCE, data.get("" + i)).result().ifPresent(pair -> list.set(index, pair.getFirst()));

            }

            return list;

        };

    }

    @Override
    public StreamCodec<BUFFERTYPE, List<TYPE>> getPacketCodec() {
        return packetCodec;
    }

    @Override
    public void writeToTag(TagWriter<List<TYPE>> writer) {
        writeToNbt.accept(writer);
    }

    @Override
    public List<TYPE> readFromTag(TagReader<List<TYPE>> reader) {
        return readFromNbt.apply(reader);
    }

    @Override
    public boolean isEqual(List<TYPE> currentValue, List<TYPE> newValue) {
        return comparison.test(currentValue, newValue);
    }

    public boolean isSingleEqual(TYPE val1, TYPE val2) {
        return singleComparison.test(val1, val2);
    }

}    
