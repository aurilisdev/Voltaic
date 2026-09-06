package voltaic.registers;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import voltaic.Voltaic;
import voltaic.api.radiation.RadiationManager;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.common.settings.VoltaicConfig;
import voltaic.prefab.utilities.CodecUtils;

public class VoltaicAttachmentTypes {
    private static final String SIZE = "size";
    private static final String POSITION = "pos";
    private static final String RADIATION = "radiation";
    private static final String AMOUNT = "amount";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
	    .create(NeoForgeRegistries.ATTACHMENT_TYPES, Voltaic.ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, SimpleRadiationSource>>> PERMANENT_RADIATION_SOURCES = ATTACHMENT_TYPES
	    .register("permanentradiationsources",
		    () -> mapAttachment(BlockPos.CODEC, SimpleRadiationSource.CODEC, RADIATION));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource>>> TEMPORARY_RADIATION_SOURCES = ATTACHMENT_TYPES
	    .register("temporaryradiationsources",
		    () -> mapAttachment(BlockPos.CODEC, IRadiationManager.TemporaryRadiationSource.CODEC, RADIATION));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, IRadiationManager.FadingRadiationSource>>> FADING_RADIATION_SOURCES = ATTACHMENT_TYPES
	    .register("fadingradiationsources",
		    () -> mapAttachment(BlockPos.CODEC, IRadiationManager.FadingRadiationSource.CODEC, RADIATION));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<AABB, Double>>> LOCALIZED_DISSIPATIONS = ATTACHMENT_TYPES
	    .register("localizeddissipations", () -> mapAttachment(CodecUtils.AABB_CODEC, Codec.DOUBLE, AMOUNT));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> DEFAULT_DISSIPATION = ATTACHMENT_TYPES
	    .register("defaultdissipation",
		    () -> AttachmentType.builder(() -> VoltaicConfig.INSTANCE.BACKGROUND_RADIATION_DISSIPATION.get())
			    .serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RadiationManager>> RADIATION_MANAGER = ATTACHMENT_TYPES
	    .register("radiationmanager", () -> AttachmentType.builder(RadiationManager::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> RECIEVED_RADIATIONAMOUNT = ATTACHMENT_TYPES
	    .register("recievedradiationamount",
		    () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> RECIEVED_RADIATIONSTRENGTH = ATTACHMENT_TYPES
	    .register("recievedradiationstrength",
		    () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> OLD_RECIEVED_RADIATIONAMOUNT = ATTACHMENT_TYPES
	    .register("oldrecievedradiationamount",
		    () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> OLD_RECIEVED_RADIATIONSTRENGTH = ATTACHMENT_TYPES
	    .register("oldrecievedradiationstrength",
		    () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

    private static <K, V> HashMap<K, V> newHashMap() {
	return new HashMap<>();
    }

    private static <K, V> AttachmentType<HashMap<K, V>> mapAttachment(Codec<K> keyCodec, Codec<V> valueCodec,
	    String valueName) {
	return AttachmentType.<HashMap<K, V>>builder(VoltaicAttachmentTypes::newHashMap)
		.serialize(new MapAttachmentSerializer<>(keyCodec, valueCodec, valueName)).build();
    }

    private static final class MapAttachmentSerializer<K, V>
	    implements IAttachmentSerializer<CompoundTag, HashMap<K, V>> {
	private final Codec<K> keyCodec;
	private final Codec<V> valueCodec;
	private final String valueName;

	private MapAttachmentSerializer(Codec<K> keyCodec, Codec<V> valueCodec, String valueName) {
	    this.keyCodec = keyCodec;
	    this.valueCodec = valueCodec;
	    this.valueName = valueName;
	}

	@Override
	public HashMap<K, V> read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
	    HashMap<K, V> data = newHashMap();
	    int size = tag.getInt(SIZE);

	    for (int i = 0; i < size; i++) {
		CompoundTag entry = tag.getCompound(Integer.toString(i));

		K key = decode(keyCodec, getRequired(entry, POSITION));
		V value = decode(valueCodec, getRequired(entry, valueName));

		data.put(key, value);
	    }

	    return data;
	}

	@Override
	public @Nullable CompoundTag write(HashMap<K, V> attachment, HolderLookup.Provider provider) {
	    CompoundTag tag = new CompoundTag();
	    tag.putInt(SIZE, attachment.size());

	    int index = 0;
	    for (Map.Entry<K, V> entry : attachment.entrySet()) {
		CompoundTag stored = new CompoundTag();

		stored.put(POSITION, encode(keyCodec, entry.getKey()));
		stored.put(valueName, encode(valueCodec, entry.getValue()));

		tag.put(Integer.toString(index++), stored);
	    }

	    return tag;
	}
    }

    private static Tag getRequired(CompoundTag tag, String key) {
	Tag value = tag.get(key);

	if (value == null)
	    throw new IllegalStateException("Missing attachment field: " + key);

	return value;
    }

    private static <T> T decode(Codec<T> codec, Tag tag) {
	return codec.parse(new Dynamic<>(NbtOps.INSTANCE, tag)).getOrThrow();
    }

    private static <T> Tag encode(Codec<T> codec, T value) {
	return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }
}