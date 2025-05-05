package voltaic.api.codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fluids.FluidStack;

public interface StreamCodec<A, T> {

	void encode(A buffer, T value);

	T decode(A buffer);

	public static final StreamCodec<PacketBuffer, Byte> BYTE = new StreamCodec<PacketBuffer, Byte>() {

		@Override
		public void encode(PacketBuffer buffer, Byte value) {
			buffer.writeByte(value);
		}

		@Override
		public Byte decode(PacketBuffer buffer) {
			return buffer.readByte();
		}

	};

	public static final StreamCodec<PacketBuffer, Boolean> BOOL = new StreamCodec<PacketBuffer, Boolean>() {

		@Override
		public void encode(PacketBuffer buffer, Boolean value) {
			buffer.writeBoolean(value);
		}

		@Override
		public Boolean decode(PacketBuffer buffer) {
			return buffer.readBoolean();
		}

	};

	public static final StreamCodec<PacketBuffer, Integer> INT = new StreamCodec<PacketBuffer, Integer>() {

		@Override
		public void encode(PacketBuffer buffer, Integer value) {
			buffer.writeInt(value);
		}

		@Override
		public Integer decode(PacketBuffer buffer) {
			return buffer.readInt();
		}

	};

	public static final StreamCodec<PacketBuffer, Long> LONG = new StreamCodec<PacketBuffer, Long>() {

		@Override
		public void encode(PacketBuffer buffer, Long value) {
			buffer.writeLong(value);
		}

		@Override
		public Long decode(PacketBuffer buffer) {
			return buffer.readLong();
		}

	};

	public static final StreamCodec<PacketBuffer, Float> FLOAT = new StreamCodec<PacketBuffer, Float>() {

		@Override
		public void encode(PacketBuffer buffer, Float value) {
			buffer.writeFloat(value);
		}

		@Override
		public Float decode(PacketBuffer buffer) {
			return buffer.readFloat();
		}

	};

	public static final StreamCodec<PacketBuffer, Double> DOUBLE = new StreamCodec<PacketBuffer, Double>() {

		@Override
		public void encode(PacketBuffer buffer, Double value) {
			buffer.writeDouble(value);
		}

		@Override
		public Double decode(PacketBuffer buffer) {
			return buffer.readDouble();
		}

	};

	public static final StreamCodec<PacketBuffer, String> STRING = new StreamCodec<PacketBuffer, String>() {

		@Override
		public void encode(PacketBuffer buffer, String value) {
			buffer.writeInt(value.length());
			for (char c : value.toCharArray()) {
				buffer.writeChar(c);
			}
		}

		@Override
		public String decode(PacketBuffer buffer) {
			int length = buffer.readInt();
			String str = "";
			for (int i = 0; i < length; i++) {
				str = str + buffer.readChar();
			}
			return str;
		}

	};

	public static final StreamCodec<PacketBuffer, java.util.UUID> UUID = new StreamCodec<PacketBuffer, java.util.UUID>() {

		@Override
		public void encode(PacketBuffer buffer, java.util.UUID value) {
			buffer.writeLong(value.getMostSignificantBits());
			buffer.writeLong(value.getLeastSignificantBits());
		}

		@Override
		public java.util.UUID decode(PacketBuffer buffer) {
			return new java.util.UUID(buffer.readLong(), buffer.readLong());
		}

	};

	public static final StreamCodec<PacketBuffer, CompoundNBT> COMPOUND_TAG = new StreamCodec<PacketBuffer, CompoundNBT>() {

		@Override
		public void encode(PacketBuffer buffer, CompoundNBT value) {
			buffer.writeNbt(value);
		}

		@Override
		public CompoundNBT decode(PacketBuffer buffer) {
			return buffer.readNbt();
		}

	};
	
	public static final StreamCodec<PacketBuffer, BlockPos> BLOCK_POS = new StreamCodec<PacketBuffer, BlockPos>() {

		@Override
		public void encode(PacketBuffer buffer, BlockPos value) {
			buffer.writeLong(value.asLong());
		}

		@Override
		public BlockPos decode(PacketBuffer buffer) {
			return BlockPos.of(buffer.readLong());
		}

	};
	
	public static final StreamCodec<PacketBuffer, FluidStack> FLUID_STACK = new StreamCodec<PacketBuffer, FluidStack>() {

		@Override
		public void encode(PacketBuffer buffer, FluidStack value) {
			buffer.writeFluidStack(value);
		}

		@Override
		public FluidStack decode(PacketBuffer buffer) {
			return buffer.readFluidStack();
		}

	};
	
	public static final StreamCodec<PacketBuffer, ItemStack> ITEM_STACK = new StreamCodec<PacketBuffer, ItemStack>() {

		@Override
		public void encode(PacketBuffer buffer, ItemStack value) {
			buffer.writeItemStack(value, false);
		}

		@Override
		public ItemStack decode(PacketBuffer buffer) {
			return buffer.readItem();
		}

	};
	
	public static final StreamCodec<PacketBuffer, Block> BLOCK = new StreamCodec<PacketBuffer, Block>() {

		@Override
		public void encode(PacketBuffer buffer, Block value) {
			buffer.writeRegistryId(value);
		}

		@Override
		public Block decode(PacketBuffer buffer) {
			return buffer.readRegistryId();
		}

	};
	
	public static final StreamCodec<PacketBuffer, BlockState> BLOCK_STATE = new StreamCodec<PacketBuffer, BlockState>() {

		@Override
		public void encode(PacketBuffer buffer, BlockState value) {
			buffer.writeInt(Block.BLOCK_STATE_REGISTRY.getId(value));
		}

		@Override
		public BlockState decode(PacketBuffer buffer) {
			return Block.BLOCK_STATE_REGISTRY.byId(buffer.readInt());
		}

	};
	
	public static final StreamCodec<PacketBuffer, ResourceLocation> RESOURCE_LOCATION = new StreamCodec<PacketBuffer, ResourceLocation>() {

		@Override
		public void encode(PacketBuffer buffer, ResourceLocation value) {
			String str = value.toString();
			buffer.writeInt(str.length());
			for (char c : str.toCharArray()) {
				buffer.writeChar(c);
			}
		}

		@Override
		public ResourceLocation decode(PacketBuffer buffer) {
			int length = buffer.readInt();
			String str = "";
			for (int i = 0; i < length; i++) {
				str = str + buffer.readChar();
			}
			return new ResourceLocation(str);
		}

	};
	
	public static final StreamCodec<PacketBuffer, Vector3d> VEC3 = new StreamCodec<PacketBuffer, Vector3d>() {
        @Override
        public Vector3d decode(PacketBuffer buffer) {
            return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public void encode(PacketBuffer buffer, Vector3d value) {
            buffer.writeDouble(value.x);
            buffer.writeDouble(value.y);
            buffer.writeDouble(value.z);
        }
    };

}
