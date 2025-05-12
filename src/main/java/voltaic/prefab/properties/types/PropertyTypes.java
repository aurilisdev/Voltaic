package voltaic.prefab.properties.types;

import java.util.Objects;
import java.util.UUID;

import com.mojang.serialization.Codec;

import voltaic.api.codec.StreamCodec;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.CodecUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fluids.FluidStack;

public class PropertyTypes {

	public static final SinglePropertyType<Byte, PacketBuffer> BYTE = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.BYTE,
			//
			Codec.BYTE
	//
	);

	public static final SinglePropertyType<Boolean, PacketBuffer> BOOLEAN = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.BOOL,
			//
			Codec.BOOL
	//
	);

	public static final ArrayPropertyType<Boolean, PacketBuffer> BOOLEAN_ARRAY = new ArrayPropertyType<>(
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

	public static final SinglePropertyType<Integer, PacketBuffer> INTEGER = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.INT,
			//
			Codec.INT
	//
	);

	public static final ListPropertyType<Integer, PacketBuffer> INTEGER_LIST = new ListPropertyType<>(
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

	public static final SetPropertyType<Integer, PacketBuffer> INTEGER_SET = new SetPropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.INT,
			//
			Codec.INT
	//
	);

	public static final SinglePropertyType<Long, PacketBuffer> LONG = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.LONG,
			//
			Codec.LONG
	//
	);

	public static final SinglePropertyType<Float, PacketBuffer> FLOAT = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.FLOAT,
			//
			Codec.FLOAT
	//
	);

	public static final SinglePropertyType<Double, PacketBuffer> DOUBLE = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.DOUBLE,
			//
			Codec.DOUBLE
	//
	);

	public static final ArrayPropertyType<Double, PacketBuffer> DOUBLE_ARRAY = new ArrayPropertyType<>(
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

	public static final ListPropertyType<String, PacketBuffer> STRING_LIST = new ListPropertyType<>(
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

	public static final SinglePropertyType<UUID, PacketBuffer> UUID = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.UUID,
			//
			CodecUtils.UUID_CODEC
	//
	);

	public static final SinglePropertyType<CompoundNBT, PacketBuffer> COMPOUND_TAG = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.COMPOUND_TAG,
			//
			CompoundNBT.CODEC
	//
	);

	public static final SinglePropertyType<BlockPos, PacketBuffer> BLOCK_POS = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.BLOCK_POS,
			//
			BlockPos.CODEC
	//
	);

	public static final ListPropertyType<BlockPos, PacketBuffer> BLOCK_POS_LIST = new ListPropertyType<>(
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

	public static final SinglePropertyType<FluidStack, PacketBuffer> FLUID_STACK = new SinglePropertyType<>(
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

	public static final SinglePropertyType<Location, PacketBuffer> LOCATION = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			Location.STREAM_CODEC,
			//
			Location.CODEC
	//
	);

	public static final SinglePropertyType<ItemStack, PacketBuffer> ITEM_STACK = new SinglePropertyType<>(
			//
			ItemStack::matches,
			//
			StreamCodec.ITEM_STACK,
			//
			ItemStack.CODEC
	//
	);

	public static final ListPropertyType<ItemStack, PacketBuffer> ITEM_STACK_LIST = new ListPropertyType<>(
			//
			ItemStack::isSame,
			//
			StreamCodec.ITEM_STACK,
			//
			ItemStack.CODEC,
			//
			ItemStack.EMPTY
	//
	);

	public static final SinglePropertyType<Block, PacketBuffer> BLOCK = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.BLOCK,
			//
			writer -> writer.tag().putInt(writer.prop().getName(), Block.BLOCK_STATE_REGISTRY.getId(writer.prop().getValue().defaultBlockState())),
			//
			reader -> Block.BLOCK_STATE_REGISTRY.byId(reader.tag().getInt(reader.prop().getName())).getBlock()
	//
	);

	public static final SinglePropertyType<BlockState, PacketBuffer> BLOCK_STATE = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.BLOCK_STATE,
			//
			BlockState.CODEC
	//
	);

	public static final SinglePropertyType<TransferPack, PacketBuffer> TRANSFER_PACK = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			TransferPack.STREAM_CODEC,
			//
			TransferPack.CODEC
	//
	);

	public static final SinglePropertyType<ResourceLocation, PacketBuffer> RESOURCE_LOCATION = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.RESOURCE_LOCATION,
			//
			ResourceLocation.CODEC
	//
	);

	public static final SinglePropertyType<Vector3d, PacketBuffer> VEC3 = new SinglePropertyType<>(
			//
			Objects::equals,
			//
			StreamCodec.VEC3,
			//
			CodecUtils.VEC3_CODEC
	//
	);

}
