package voltaic.registers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import voltaic.Voltaic;
import voltaic.api.radiation.RadiationManager;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.BlockPosVolume;
import voltaic.api.radiation.util.IRadiationManager;
import voltaic.common.settings.VoltaicConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VoltaicAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Voltaic.ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, SimpleRadiationSource>>> PERMANENT_RADIATION_SOURCES = ATTACHMENT_TYPES.register("permanentradiationsources", () -> AttachmentType.builder(() -> new HashMap<BlockPos, SimpleRadiationSource>()).serialize(new IAttachmentSerializer<CompoundTag, HashMap<BlockPos, SimpleRadiationSource>>() {
        @Override
        public HashMap<BlockPos, SimpleRadiationSource> read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
            HashMap<BlockPos, SimpleRadiationSource> data = new HashMap<>();

            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {

                CompoundTag stored = tag.getCompound("" + i);
                data.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), SimpleRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).getOrThrow());
            }


            return data;
        }

        @Override
        public @Nullable CompoundTag write(HashMap<BlockPos, SimpleRadiationSource> attachment, HolderLookup.Provider provider) {
            CompoundTag data = new CompoundTag();
            int size = attachment.size();
            data.putInt("size", size);
            int i = 0;
            for (Map.Entry<BlockPos, SimpleRadiationSource> entry : attachment.entrySet()) {
                CompoundTag store = new CompoundTag();
                BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).ifSuccess(tag -> store.put("pos", tag));
                SimpleRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(tag -> store.put("radiation", tag));
                data.put(i + "", store);
                i++;
            }
            return data;
        }
    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource>>> TEMPORARY_RADIATION_SOURCES = ATTACHMENT_TYPES.register("temporaryradiationsources", () -> AttachmentType.builder(() -> new HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource>()).serialize(new IAttachmentSerializer<CompoundTag, HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource>>() {
        @Override
        public HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource> read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
            HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource> data = new HashMap<>();

            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {

                CompoundTag stored = tag.getCompound("" + i);
                data.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), IRadiationManager.TemporaryRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).getOrThrow());
            }


            return data;
        }

        @Override
        public @Nullable CompoundTag write(HashMap<BlockPos, IRadiationManager.TemporaryRadiationSource> attachment, HolderLookup.Provider provider) {
            CompoundTag data = new CompoundTag();
            int size = attachment.size();
            data.putInt("size", size);
            int i = 0;
            for (Map.Entry<BlockPos, IRadiationManager.TemporaryRadiationSource> entry : attachment.entrySet()) {
                CompoundTag store = new CompoundTag();
                BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).ifSuccess(tag -> store.put("pos", tag));
                IRadiationManager.TemporaryRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(tag -> store.put("radiation", tag));
                data.put(i + "", store);
                i++;
            }
            return data;
        }
    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPos, IRadiationManager.FadingRadiationSource>>> FADING_RADIATION_SOURCES = ATTACHMENT_TYPES.register("fadingradiationsources", () -> AttachmentType.builder(() -> new HashMap<BlockPos, IRadiationManager.FadingRadiationSource>()).serialize(new IAttachmentSerializer<CompoundTag, HashMap<BlockPos, IRadiationManager.FadingRadiationSource>>() {
        @Override
        public HashMap<BlockPos, IRadiationManager.FadingRadiationSource> read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
            HashMap<BlockPos, IRadiationManager.FadingRadiationSource> data = new HashMap<>();

            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {

                CompoundTag stored = tag.getCompound("" + i);
                data.put(BlockPos.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), IRadiationManager.FadingRadiationSource.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.getCompound("radiation"))).getOrThrow());
            }


            return data;
        }

        @Override
        public @Nullable CompoundTag write(HashMap<BlockPos, IRadiationManager.FadingRadiationSource> attachment, HolderLookup.Provider provider) {
            CompoundTag data = new CompoundTag();
            int size = attachment.size();
            data.putInt("size", size);
            int i = 0;
            for (Map.Entry<BlockPos, IRadiationManager.FadingRadiationSource> entry : attachment.entrySet()) {
                CompoundTag store = new CompoundTag();
                BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).ifSuccess(tag -> store.put("pos", tag));
                IRadiationManager.FadingRadiationSource.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(tag -> store.put("radiation", tag));
                data.put(i + "", store);
                i++;
            }
            return data;
        }
    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<BlockPosVolume, Double>>> LOCALIZED_DISSIPATIONS = ATTACHMENT_TYPES.register("localizeddissipations", () -> AttachmentType.builder(() -> new HashMap<BlockPosVolume, Double>()).serialize(new IAttachmentSerializer<CompoundTag, HashMap<BlockPosVolume, Double>>() {
        @Override
        public HashMap<BlockPosVolume, Double> read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
            HashMap<BlockPosVolume, Double> data = new HashMap<>();

            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {

                CompoundTag stored = tag.getCompound("" + i);
                data.put(BlockPosVolume.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, stored.get("pos"))).result().get(), stored.getDouble("amount"));
            }


            return data;
        }

        @Override
        public @Nullable CompoundTag write(HashMap<BlockPosVolume, Double> attachment, HolderLookup.Provider provider) {
            CompoundTag data = new CompoundTag();
            int size = attachment.size();
            data.putInt("size", size);
            int i = 0;
            for (Map.Entry<BlockPosVolume, Double> entry : attachment.entrySet()) {
                CompoundTag store = new CompoundTag();
                BlockPosVolume.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).ifSuccess(tag -> store.put("pos", tag));
                store.putDouble("amount", entry.getValue());
                data.put(i + "", store);
                i++;
            }
            return data;
        }
    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> DEFAULT_DISSIPATION = ATTACHMENT_TYPES.register("defaultdissipation", () -> AttachmentType.builder(() -> VoltaicConstants.BACKROUND_RADIATION_DISSIPATION).serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RadiationManager>> RADIATION_MANAGER = ATTACHMENT_TYPES.register("radiationmanager", () -> AttachmentType.builder(RadiationManager::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> RECIEVED_RADIATIONAMOUNT = ATTACHMENT_TYPES.register("recievedradiationamount", () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> RECIEVED_RADIATIONSTRENGTH = ATTACHMENT_TYPES.register("recievedradiationstrength", () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> OLD_RECIEVED_RADIATIONAMOUNT = ATTACHMENT_TYPES.register("oldrecievedradiationamount", () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> OLD_RECIEVED_RADIATIONSTRENGTH = ATTACHMENT_TYPES.register("oldrecievedradiationstrength", () -> AttachmentType.builder(() -> Double.valueOf(0.0)).serialize(Codec.DOUBLE).build());

}
