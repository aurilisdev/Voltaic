package voltaic.registers;

import com.google.common.collect.Sets;

import voltaic.Voltaic;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicTiles {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Voltaic.ID);

	public static final RegistryObject<BlockEntityType<TileMultiSubnode>> TILE_MULTI = BLOCK_ENTITY_TYPES.register("multisubnode", () -> new BlockEntityType<>(TileMultiSubnode::new, Sets.newHashSet(VoltaicBlocks.BLOCK_MULTISUBNODE.get()), null));

}
