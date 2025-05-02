package voltaic.prefab.properties.types;

import java.util.Objects;
import java.util.UUID;

import com.mojang.serialization.Codec;

import voltaic.api.codec.StreamCodec;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.CodecUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PropertyTypes {

    public static final SinglePropertyType<Byte, ByteBuf> BYTE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BYTE,
            //
            Codec.BYTE
            //
    );

    public static final SinglePropertyType<Boolean, ByteBuf> BOOLEAN = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BOOL,
            //
            Codec.BOOL
            //
    );

    public static final ArrayPropertyType<Boolean, ByteBuf> BOOLEAN_ARRAY = new ArrayPropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BOOL,
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
            StreamCodec.INT,
            //
            Codec.INT
            //
    );

    public static final ListPropertyType<Integer, ByteBuf> INTEGER_LIST = new ListPropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.INT,
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
            StreamCodec.INT,
            //
            Codec.INT
            //
    );

    public static final SinglePropertyType<Long, ByteBuf> LONG = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.LONG,
            //
            Codec.LONG
            //
    );

    public static final SinglePropertyType<Float, ByteBuf> FLOAT = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.FLOAT,
            //
            Codec.FLOAT
            //
    );

    public static final SinglePropertyType<Double, ByteBuf> DOUBLE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.DOUBLE,
            //
            Codec.DOUBLE
            //
    );

    public static final ArrayPropertyType<Double, ByteBuf> DOUBLE_ARRAY = new ArrayPropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.DOUBLE,
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
            StreamCodec.STRING,
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
            StreamCodec.UUID,
            //
            CodecUtils.UUID_CODEC
            //
    );

    public static final SinglePropertyType<CompoundTag, ByteBuf> COMPOUND_TAG = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.COMPOUND_TAG,
            //
            CompoundTag.CODEC
            //
    );

    public static final SinglePropertyType<BlockPos, ByteBuf> BLOCK_POS = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BLOCK_POS,
            //
            BlockPos.CODEC
            //
    );

    public static final ListPropertyType<BlockPos, ByteBuf> BLOCK_POS_LIST = new ListPropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BLOCK_POS,
            //
            BlockPos.CODEC,
            //
            BlockEntityUtils.OUT_OF_REACH
            //
    );

    public static final SinglePropertyType<FluidStack, FriendlyByteBuf> FLUID_STACK = new SinglePropertyType<>(
            //
            (thisStack, otherStack) -> {
                if (thisStack.getAmount() != otherStack.getAmount()) {
                    return false;
                }
                return thisStack.getFluid().isSame(otherStack.getFluid());
            },
            //
            StreamCodec.FLUID_STACK,
            //
            FluidStack.CODEC
            //
    );

    public static final SinglePropertyType<Location, ByteBuf> LOCATION = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            Location.STREAM_CODEC,
            //
            Location.CODEC
            //
    );

    public static final SinglePropertyType<ItemStack, FriendlyByteBuf> ITEM_STACK = new SinglePropertyType<>(
            //
            ItemStack::matches,
            //
            StreamCodec.ITEM_STACK,
            //
            ItemStack.CODEC
            //
    );

    public static final ListPropertyType<ItemStack, FriendlyByteBuf> ITEM_STACK_LIST = new ListPropertyType<>(
            //
            ItemStack::isSameItemSameTags,
            //
            StreamCodec.ITEM_STACK,
            //
            ItemStack.CODEC,
            //
            ItemStack.EMPTY
            //
    );

    public static final SinglePropertyType<Block, FriendlyByteBuf> BLOCK = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BLOCK,
            //
            ForgeRegistries.BLOCKS.getCodec()
            //
    );

    public static final SinglePropertyType<BlockState, FriendlyByteBuf> BLOCK_STATE = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.BLOCK_STATE,
            //
            BlockState.CODEC
            //
    );

    public static final SinglePropertyType<TransferPack, ByteBuf> TRANSFER_PACK = new SinglePropertyType<>(
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
            StreamCodec.RESOURCE_LOCATION,
            //
            ResourceLocation.CODEC
            //
    );

    public static final SinglePropertyType<Vec3, ByteBuf> VEC3 = new SinglePropertyType<>(
            //
            Objects::equals,
            //
            StreamCodec.VEC3,
            //
            CodecUtils.VEC3_CODEC
            //
    );


}
