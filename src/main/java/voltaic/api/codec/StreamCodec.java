package voltaic.api.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public interface StreamCodec<A, T> {

	void encode(A buffer, T value);

	T decode(A buffer);

	public static final StreamCodec<ByteBuf, Byte> BYTE = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Byte value) {
			buffer.writeByte(value);
		}

		@Override
		public Byte decode(ByteBuf buffer) {
			return buffer.readByte();
		}

	};

	public static final StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Boolean value) {
			buffer.writeBoolean(value);
		}

		@Override
		public Boolean decode(ByteBuf buffer) {
			return buffer.readBoolean();
		}

	};

	public static final StreamCodec<ByteBuf, Integer> INT = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Integer value) {
			buffer.writeInt(value);
		}

		@Override
		public Integer decode(ByteBuf buffer) {
			return buffer.readInt();
		}

	};

	public static final StreamCodec<ByteBuf, Long> LONG = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Long value) {
			buffer.writeLong(value);
		}

		@Override
		public Long decode(ByteBuf buffer) {
			return buffer.readLong();
		}

	};

	public static final StreamCodec<ByteBuf, Float> FLOAT = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Float value) {
			buffer.writeFloat(value);
		}

		@Override
		public Float decode(ByteBuf buffer) {
			return buffer.readFloat();
		}

	};

	public static final StreamCodec<ByteBuf, Double> DOUBLE = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, Double value) {
			buffer.writeDouble(value);
		}

		@Override
		public Double decode(ByteBuf buffer) {
			return buffer.readDouble();
		}

	};

	public static final StreamCodec<ByteBuf, String> STRING = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, String value) {
			buffer.writeInt(value.length());
			for (char c : value.toCharArray()) {
				buffer.writeChar(c);
			}
		}

		@Override
		public String decode(ByteBuf buffer) {
			int length = buffer.readInt();
			String str = "";
			for (int i = 0; i < length; i++) {
				str = str + buffer.readChar();
			}
			return str;
		}

	};

	public static final StreamCodec<ByteBuf, java.util.UUID> UUID = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, java.util.UUID value) {
			buffer.writeLong(value.getMostSignificantBits());
			buffer.writeLong(value.getLeastSignificantBits());
		}

		@Override
		public java.util.UUID decode(ByteBuf buffer) {
			return new java.util.UUID(buffer.readLong(), buffer.readLong());
		}

	};

	public static final StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, CompoundTag value) {
			if (value == null) {
				buffer.writeByte(0);
			} else {
				try {
					NbtIo.write(value, new ByteBufOutputStream(buffer));
				} catch (IOException ioexception) {
					throw new EncoderException(ioexception);
				}
			}
		}

		@Override
		public CompoundTag decode(ByteBuf buffer) {
			int i = buffer.readerIndex();
			byte b0 = buffer.readByte();
			if (b0 == 0) {
				return null;
			} else {
				buffer.readerIndex(i);

				try {
					return NbtIo.read(new ByteBufInputStream(buffer), new NbtAccounter(2097152L));
				} catch (IOException ioexception) {
					throw new EncoderException(ioexception);
				}
			}
		}

	};
	
	public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, BlockPos value) {
			buffer.writeLong(value.asLong());
		}

		@Override
		public BlockPos decode(ByteBuf buffer) {
			return BlockPos.of(buffer.readLong());
		}

	};
	
	public static final StreamCodec<FriendlyByteBuf, FluidStack> FLUID_STACK = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buffer, FluidStack value) {
			buffer.writeFluidStack(value);
		}

		@Override
		public FluidStack decode(FriendlyByteBuf buffer) {
			return buffer.readFluidStack();
		}

	};
	
	public static final StreamCodec<FriendlyByteBuf, ItemStack> ITEM_STACK = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buffer, ItemStack value) {
			buffer.writeItemStack(value, false);
		}

		@Override
		public ItemStack decode(FriendlyByteBuf buffer) {
			return buffer.readItem();
		}

	};
	
	public static final StreamCodec<FriendlyByteBuf, Block> BLOCK = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buffer, Block value) {
			buffer.writeId(Block.BLOCK_STATE_REGISTRY, value.defaultBlockState());
		}

		@Override
		public Block decode(FriendlyByteBuf buffer) {
			return buffer.readById(Block.BLOCK_STATE_REGISTRY).getBlock();
		}

	};
	
	public static final StreamCodec<FriendlyByteBuf, BlockState> BLOCK_STATE = new StreamCodec<>() {

		@Override
		public void encode(FriendlyByteBuf buffer, BlockState value) {
			buffer.writeId(Block.BLOCK_STATE_REGISTRY, value);
		}

		@Override
		public BlockState decode(FriendlyByteBuf buffer) {
			return buffer.readById(Block.BLOCK_STATE_REGISTRY);
		}

	};
	
	public static final StreamCodec<ByteBuf, ResourceLocation> RESOURCE_LOCATION = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf buffer, ResourceLocation value) {
			String str = value.toString();
			buffer.writeInt(str.length());
			for (char c : str.toCharArray()) {
				buffer.writeChar(c);
			}
		}

		@Override
		public ResourceLocation decode(ByteBuf buffer) {
			int length = buffer.readInt();
			String str = "";
			for (int i = 0; i < length; i++) {
				str = str + buffer.readChar();
			}
			return new ResourceLocation(str);
		}

	};
	
	public static final StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
        @Override
        public Vec3 decode(ByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public void encode(ByteBuf buffer, Vec3 value) {
            buffer.writeDouble(value.x);
            buffer.writeDouble(value.y);
            buffer.writeDouble(value.z);
        }
    };

}
