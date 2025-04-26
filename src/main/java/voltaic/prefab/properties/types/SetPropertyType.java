package voltaic.prefab.properties.types;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import voltaic.api.codec.StreamCodec;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class SetPropertyType<TYPE, BUFFERTYPE extends ByteBuf> implements IPropertyType<HashSet<TYPE>, BUFFERTYPE> {

    private final BiPredicate<TYPE, TYPE> singleComparison;
    private final BiPredicate<HashSet<TYPE>, HashSet<TYPE>> comparison;
    private final StreamCodec<BUFFERTYPE, HashSet<TYPE>> packetCodec;
    private final Consumer<TagWriter<HashSet<TYPE>>> writeToNbt;
    private final Function<TagReader<HashSet<TYPE>>, HashSet<TYPE>> readFromNbt;

    public SetPropertyType(@Nonnull BiPredicate<TYPE, TYPE> singleComparison, StreamCodec<BUFFERTYPE, TYPE> singlePacketCodec, Codec<TYPE> singleNbtCodec) {

        this.singleComparison = singleComparison;

        this.comparison = (set1, set2) -> {

            if (set1 == null || set2 == null) {
                return false;
            }

            if (set1.size() != set2.size()) {
                return false;
            }

            return set1.equals(set2);

        };

        packetCodec = new StreamCodec<BUFFERTYPE, HashSet<TYPE>>() {

            @Override
            public HashSet<TYPE> decode(BUFFERTYPE buffer) {

                int size = buffer.readInt();

                HashSet<TYPE> newSet = new HashSet<>();

                for (int i = 0; i < size; i++) {

                    newSet.add(singlePacketCodec.decode(buffer));

                }

                return newSet;
            }

            @Override
            public void encode(BUFFERTYPE buffer, HashSet<TYPE> value) {

                buffer.writeInt(value.size());

                for (TYPE val : value) {
                    singlePacketCodec.encode(buffer, val);
                }

            }
        };

        writeToNbt = writer -> {

            CompoundTag tag = new CompoundTag();

            HashSet<TYPE> set = writer.prop().getValue();

            tag.putInt("size", set.size());

            int index = 0;

            for (TYPE val : set) {

                final int indx = index;

                singleNbtCodec.encode(val, NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).result().ifPresent(nbt -> tag.put("" + indx, nbt));

                index++;

            }

            writer.tag().put(writer.prop().getName(), tag);

        };

        readFromNbt = reader -> {

            CompoundTag data = reader.tag().getCompound(reader.prop().getName());

            if (!data.contains("size")) {
                return reader.prop().getValue();
            }

            int size = data.getInt("size");

            if (size <= 0) {
                return reader.prop().getValue();
            }

            HashSet<TYPE> set = new HashSet<>();

            for (int i = 0; i < size; i++) {

                singleNbtCodec.decode(NbtOps.INSTANCE, data.get("" + i)).result().ifPresent(pair -> set.add(pair.getFirst()));

            }

            return set;

        };

    }

    @Override
    public StreamCodec<BUFFERTYPE, HashSet<TYPE>> getPacketCodec() {
        return packetCodec;
    }

    @Override
    public void writeToTag(TagWriter<HashSet<TYPE>> writer) {
        writeToNbt.accept(writer);
    }

    @Override
    public HashSet<TYPE> readFromTag(TagReader<HashSet<TYPE>> reader) {
        return readFromNbt.apply(reader);
    }

    @Override
    public boolean isEqual(HashSet<TYPE> currentValue, HashSet<TYPE> newValue) {
        return comparison.test(currentValue, newValue);
    }

    public boolean isSingleEqual(TYPE val1, TYPE val2) {
        return singleComparison.test(val1, val2);
    }

}
