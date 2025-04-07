package electrodynamics.prefab.properties;

import static net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import electrodynamics.api.gas.GasStack;
import electrodynamics.prefab.utilities.object.Location;
import electrodynamics.prefab.utilities.object.TransferPack;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class PropertyTypes {

    public static final PropertyType<Byte, ByteBuf> BYTE = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.BYTE,
            //
            writer -> writer.tag().putByte(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getByte(reader.prop().getName())
            //
    );

    public static final PropertyType<Boolean, ByteBuf> BOOLEAN = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.BOOL,
            //
            writer -> writer.tag().putBoolean(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getBoolean(reader.prop().getName())
            //
    );

    public static final PropertyType<Integer, ByteBuf> INTEGER = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.INT,
            //
            writer -> writer.tag().putInt(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getInt(reader.prop().getName())
            //
    );

    public static final PropertyType<Long, ByteBuf> LONG = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.VAR_LONG,
            //
            writer -> writer.tag().putLong(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getLong(reader.prop().getName())
            //
    );

    public static final PropertyType<Float, ByteBuf> FLOAT = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.FLOAT,
            //
            writer -> writer.tag().putFloat(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getFloat(reader.prop().getName())
            //
    );

    public static final PropertyType<Double, ByteBuf> DOUBLE = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.DOUBLE,
            //
            writer -> writer.tag().putDouble(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getDouble(reader.prop().getName())
            //
    );

    public static final PropertyType<UUID, ByteBuf> UUID = new PropertyType<>(
            //
            Objects::equals,
            //
            UUIDUtil.STREAM_CODEC,
            //
            writer -> writer.tag().putUUID(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getUUID(reader.prop().getName())
            //
    );

    public static final PropertyType<CompoundTag, ByteBuf> COMPOUND_TAG = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.fromCodec(CompoundTag.CODEC),
            //
            writer -> writer.tag().put(writer.prop().getName(), writer.prop().get()),
            //
            reader -> reader.tag().getCompound(reader.prop().getName())
            //
    );

    public static final PropertyType<BlockPos, ByteBuf> BLOCK_POS = new PropertyType<>(
            //
            Objects::equals,
            //
            BlockPos.STREAM_CODEC,
            //
            writer -> writer.tag().put(writer.prop().getName(), NbtUtils.writeBlockPos((writer.prop().get()))),
            //
            reader -> NbtUtils.readBlockPos(reader.tag(), reader.prop().getName()).get()
            //
    );

    public static final PropertyType<NonNullList<ItemStack>, ByteBuf> INVENTORY_ITEMS = new PropertyType<>(
            //
            (thisList, otherList) -> {
                if (thisList.size() != otherList.size()) {
                    return false;
                }
                ItemStack a, b;
                for (int i = 0; i < thisList.size(); i++) {
                    a = thisList.get(i);
                    b = otherList.get(i);
                    if (!ItemStack.isSameItemSameComponents(a, b)) {
                        return false;
                    }
                }
                return true;
            },
            //
            ByteBufCodecs.fromCodec(NonNullList.codecOf(ItemStack.CODEC)),
            //
            writer -> {
                //
                CompoundTag data = new CompoundTag();
                NonNullList<ItemStack> list = writer.prop().get();
                data.putInt(writer.prop().getName() + "_size", list.size());
                ContainerHelper.saveAllItems(data, list, true, writer.registries());
                writer.tag().put(writer.prop().getName(), data);
            },
            //
            reader -> {
                //
                CompoundTag data = reader.tag().getCompound(reader.prop().getName());
                int size = data.getInt(reader.prop().getName() + "_size");
                if (size == 0 || reader.prop().get().size() != size) {
                    return null; // null is handled in function method caller and signals a bad writer.value() to be ignored
                }
                NonNullList<ItemStack> toBeFilled = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(data, toBeFilled, reader.registries());
                return toBeFilled;
            }
            //
    );

    public static final PropertyType<FluidStack, RegistryFriendlyByteBuf> FLUID_STACK = new PropertyType<>(
            //
            (thisStack, otherStack) -> {
                if (thisStack.getAmount() != otherStack.getAmount()) {
                    return false;
                }
                return thisStack.getFluid().isSame(otherStack.getFluid());
            },
            //
            FluidStack.OPTIONAL_STREAM_CODEC,
            //
            writer -> {
                Tag fluidTag = new CompoundTag();
                fluidTag = FluidStack.OPTIONAL_CODEC.encode(writer.prop().get(), NbtOps.INSTANCE, fluidTag).getOrThrow();
                writer.tag().put(writer.prop().getName(), fluidTag);
            },
            //
            reader -> FluidStack.OPTIONAL_CODEC.decode(NbtOps.INSTANCE, reader.tag().getCompound(reader.prop().getName())).getOrThrow().getFirst()
            //
    );

    public static final PropertyType<List<BlockPos>, ByteBuf> BLOCK_POS_LIST = new PropertyType<>(
            //
            (thisList, otherList) -> {
                if (thisList.size() != otherList.size()) {
                    return false;
                }
                BlockPos a, b;
                for (int i = 0; i < thisList.size(); i++) {
                    a = thisList.get(i);
                    b = otherList.get(i);
                    if (!a.equals(b)) {
                        return false;
                    }
                }
                return true;
            },
            //
            ByteBufCodecs.fromCodec(BlockPos.CODEC.listOf()),
            //
            writer -> {
                List<BlockPos> posList = writer.prop().get();
                CompoundTag data = new CompoundTag();
                data.putInt("size", posList.size());
                for (int i = 0; i < posList.size(); i++) {
                    data.put("pos" + i, NbtUtils.writeBlockPos(posList.get(i)));
                }
                writer.tag().put(writer.prop().getName(), data);
            },
            //
            reader -> {
                List<BlockPos> list = new ArrayList<>();
                CompoundTag data = reader.tag().getCompound(reader.prop().getName());
                int size = data.getInt("size");
                for (int i = 0; i < size; i++) {
                    list.add(NbtUtils.readBlockPos(data, "pos" + i).get());
                }
                return list;
            }
            //
    );

    public static final PropertyType<Location, FriendlyByteBuf> LOCATION = new PropertyType<>(
            //
            Objects::equals,
            //
            Location.STREAM_CODEC,
            //
            writer -> {
                CompoundTag tag = new CompoundTag();
                writer.prop().get().writeToNBT(tag, "");
                writer.tag().put(writer.prop().getName(), tag);
            },
            //
            reader -> Location.readFromNBT(reader.tag().getCompound(reader.prop().getName()), "")
            //
    );

    public static final PropertyType<GasStack, RegistryFriendlyByteBuf> GAS_STACK = new PropertyType<>(
            //
            Objects::equals,
            //
            GasStack.STREAM_CODEC,
            //
            writer -> writer.tag().put(writer.prop().getName(), writer.prop().get().writeToNbt()),
            //
            reader -> GasStack.readFromNbt(reader.tag().getCompound(reader.prop().getName()))
            //
    );

    public static final PropertyType<ItemStack, RegistryFriendlyByteBuf> ITEM_STACK = new PropertyType<>(
            //
            ItemStack::matches,
            //
            ItemStack.STREAM_CODEC,
            //
            writer -> writer.tag().put(writer.prop().getName(), writer.prop().get().save(writer.registries())),
            //
            reader -> ItemStack.CODEC.decode(NbtOps.INSTANCE, reader.tag().getCompound(reader.prop().getName())).getOrThrow().getFirst()
            //
    );

    public static final PropertyType<Block, ByteBuf> BLOCK = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.fromCodec(BuiltInRegistries.BLOCK.byNameCodec()),
            //
            writer -> {
                writer.tag().put(writer.prop().getName(), new ItemStack(writer.prop().get().asItem()).save(writer.registries()));
            },
            //
            reader -> {
                ItemStack stack = ItemStack.CODEC.decode(NbtOps.INSTANCE, reader.tag().getCompound(reader.prop().getName())).getOrThrow().getFirst();
                if (stack.isEmpty()) {
                    return Blocks.AIR;
                }
                return ((BlockItem) stack.getItem()).getBlock();
            }
            //
    );

    public static final PropertyType<BlockState, ByteBuf> BLOCK_STATE = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.fromCodec(BlockState.CODEC),
            //
            writer -> writer.tag().putInt(writer.prop().getName(), BLOCK_STATE_REGISTRY.getId(writer.prop().get())),
            //
            reader -> BLOCK_STATE_REGISTRY.byId(reader.tag().getInt(reader.prop().getName()))
            //
    );

    public static final PropertyType<TransferPack, FriendlyByteBuf> TRANSFER_PACK = new PropertyType<>(
            //
            Objects::equals,
            //
            TransferPack.STREAM_CODEC,
            //
            writer -> writer.tag().put(writer.prop().getName(), writer.prop().get().writeToTag()),
            //
            reader -> TransferPack.readFromTag(reader.tag().getCompound(reader.prop().getName()))
            //
    );

    public static final PropertyType<ResourceLocation, ByteBuf> RESOURCE_LOCATION = new PropertyType<>(
            //
            Objects::equals,
            //
            ResourceLocation.STREAM_CODEC,
            //
            writer -> writer.tag().putString(writer.prop().getName(), writer.prop().get().toString()),
            //
            reader -> ResourceLocation.parse(reader.tag().getString(reader.prop().getName()))
            //
    );

    public static final PropertyType<Vec3, ByteBuf> VEC3 = new PropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.fromCodec(Vec3.CODEC),
            //
            writer -> Vec3.CODEC.encodeStart(NbtOps.INSTANCE, writer.prop().get()).ifSuccess(tag -> writer.tag().put(writer.prop().getName(), tag)),
            //
            reader -> Vec3.CODEC.decode(NbtOps.INSTANCE, reader.tag().get(reader.prop().getName())).getOrThrow().getFirst()
            //
    );


}
