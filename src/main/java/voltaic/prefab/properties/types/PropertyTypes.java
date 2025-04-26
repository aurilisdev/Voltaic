package voltaic.prefab.properties.types;

import java.util.Objects;
import java.util.UUID;

import com.mojang.serialization.Codec;
import voltaic.api.gas.GasStack;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.CodecUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class PropertyTypes {

    public static final SinglePropertyType<Byte, ByteBuf> BYTE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.BYTE,
            //
            Codec.BYTE
            //
    );

    public static final SinglePropertyType<Boolean, ByteBuf> BOOLEAN = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.BOOL,
            //
            Codec.BOOL
            //
    );

    public static final ArrayPropertyType<Boolean, ByteBuf> BOOLEAN_ARRAY = new ArrayPropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.BOOL,
            //
            Codec.BOOL,
            //
            new Boolean[0],
            //
            false
            //
    );

    public static final SinglePropertyType<Integer, ByteBuf> INTEGER = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.INT,
            //
            Codec.INT
            //
    );

    public static final ListPropertyType<Integer, ByteBuf> INTEGER_LIST = new ListPropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.INT,
            //
            Codec.INT,
            //
            0
            //
    );

    public static final SetPropertyType<Integer, ByteBuf> INTEGER_SET = new SetPropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.INT,
            //
            Codec.INT
            //
    );

    public static final SinglePropertyType<Long, ByteBuf> LONG = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.VAR_LONG,
            //
            Codec.LONG
            //
    );

    public static final SinglePropertyType<Float, ByteBuf> FLOAT = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.FLOAT,
            //
            Codec.FLOAT
            //
    );

    public static final SinglePropertyType<Double, ByteBuf> DOUBLE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.DOUBLE,
            //
            Codec.DOUBLE
            //
    );

    public static final ArrayPropertyType<Double, ByteBuf> DOUBLE_ARRAY = new ArrayPropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.DOUBLE,
            //
            Codec.DOUBLE,
            //
            new Double[0],
            //
            0.0D
            //
    );

    public static final ListPropertyType<String, ByteBuf> STRING_LIST = new ListPropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.STRING_UTF8,
            //
            Codec.STRING,
            //
            ""
            //
    );

    public static final SinglePropertyType<UUID, ByteBuf> UUID = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            UUIDUtil.STREAM_CODEC,
            //
            UUIDUtil.CODEC
            //
    );

    public static final SinglePropertyType<CompoundTag, ByteBuf> COMPOUND_TAG = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.COMPOUND_TAG,
            //
            CompoundTag.CODEC
            //
    );

    public static final SinglePropertyType<BlockPos, ByteBuf> BLOCK_POS = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            BlockPos.STREAM_CODEC,
            //
            BlockPos.CODEC
            //
    );

    public static final ListPropertyType<BlockPos, ByteBuf> BLOCK_POS_LIST = new ListPropertyType<>(
            //
            Objects::equals,
            //
            BlockPos.STREAM_CODEC,
            //
            BlockPos.CODEC,
            //
            BlockEntityUtils.OUT_OF_REACH
            //
    );

    public static final SinglePropertyType<FluidStack, RegistryFriendlyByteBuf> FLUID_STACK = new SinglePropertyType<>(
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
            FluidStack.OPTIONAL_CODEC
            //
    );

    public static final SinglePropertyType<Location, FriendlyByteBuf> LOCATION = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            Location.STREAM_CODEC,
            //
            Location.CODEC
            //
    );

    public static final SinglePropertyType<GasStack, RegistryFriendlyByteBuf> GAS_STACK = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            GasStack.STREAM_CODEC,
            //
            GasStack.CODEC
            //
    );

    public static final SinglePropertyType<ItemStack, RegistryFriendlyByteBuf> ITEM_STACK = new SinglePropertyType<>(
            //
            ItemStack::matches,
            //
            ItemStack.OPTIONAL_STREAM_CODEC,
            //
            ItemStack.OPTIONAL_CODEC
            //
    );

    public static final ListPropertyType<ItemStack, RegistryFriendlyByteBuf> ITEM_STACK_LIST = new ListPropertyType<>(
            //
            ItemStack::isSameItemSameComponents,
            //
            ItemStack.OPTIONAL_STREAM_CODEC,
            //
            ItemStack.OPTIONAL_CODEC,
            //
            ItemStack.EMPTY
            //
    );

    public static final SinglePropertyType<Block, RegistryFriendlyByteBuf> BLOCK = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.registry(Registries.BLOCK),
            //
            BuiltInRegistries.BLOCK.byNameCodec()
            //
    );

    public static final SinglePropertyType<BlockState, ByteBuf> BLOCK_STATE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ByteBufCodecs.fromCodec(BlockState.CODEC),
            //
            BlockState.CODEC
            //
    );

    public static final SinglePropertyType<TransferPack, FriendlyByteBuf> TRANSFER_PACK = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            TransferPack.STREAM_CODEC,
            //
            TransferPack.CODEC
            //
    );

    public static final SinglePropertyType<ResourceLocation, ByteBuf> RESOURCE_LOCATION = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            ResourceLocation.STREAM_CODEC,
            //
            ResourceLocation.CODEC
            //
    );

    public static final SinglePropertyType<Vec3, ByteBuf> VEC3 = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            CodecUtils.VEC3_STREAM_CODEC,
            //
            Vec3.CODEC
            //
    );


}
