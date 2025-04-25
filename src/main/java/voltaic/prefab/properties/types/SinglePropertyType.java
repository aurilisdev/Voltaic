package voltaic.prefab.properties.types;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import voltaic.api.codec.StreamCodec;

public class SinglePropertyType<TYPE, BUFFERTYPE> implements IPropertyType<TYPE, BUFFERTYPE> {

    //.ifSuccess(tag -> writer.tag().put(writer.prop().getName(), tag))

    private final BiPredicate<TYPE, TYPE> comparison;

    private final Consumer<TagWriter<TYPE>> writeToNbt;

    private final Function<TagReader<TYPE>, TYPE> readFromNbt;
    private final StreamCodec<BUFFERTYPE, TYPE> packetCodec;

    public SinglePropertyType(@Nonnull BiPredicate<TYPE, TYPE> comparison, StreamCodec<BUFFERTYPE, TYPE> packetCodec, Codec<TYPE> nbtCodec) {

        this(
                comparison,
                //
                packetCodec,
                //
                writer -> nbtCodec.encode(writer.prop().getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).result().ifPresent(tag -> writer.tag().put(writer.prop().getName(), tag)),
                //
                reader -> {
                    DataResult<Pair<TYPE, Tag>> result = nbtCodec.decode(NbtOps.INSTANCE, reader.tag().get(reader.prop().getName()));

                    return result.result().isPresent() ? result.result().get().getFirst() : reader.prop().getValue();
                }
        );


    }

    public SinglePropertyType(@Nonnull BiPredicate<TYPE, TYPE> comparison, StreamCodec<BUFFERTYPE, TYPE> packetCodec, Consumer<TagWriter<TYPE>> tagWriter, Function<TagReader<TYPE>, TYPE> tagReader) {

        this.comparison = comparison;
        this.packetCodec = packetCodec;
        this.writeToNbt = tagWriter;
        this.readFromNbt = tagReader;
    }

    @Override
    public StreamCodec<BUFFERTYPE, TYPE> getPacketCodec() {
        return packetCodec;
    }

    @Override
    public void writeToTag(TagWriter<TYPE> writer) {
        writeToNbt.accept(writer);
    }

    @Override
    public TYPE readFromTag(TagReader<TYPE> reader) {
        return readFromNbt.apply(reader);
    }

    @Override
    public boolean isEqual(TYPE currentValue, TYPE newValue) {
        return comparison.test(currentValue, newValue);
    }

}
