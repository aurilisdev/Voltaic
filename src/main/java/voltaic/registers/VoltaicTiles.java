package voltaic.registers;

import com.google.common.collect.Sets;

import voltaic.Voltaic;
import voltaic.api.multiblock.assemblybased.TileMultiblockSlave;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicTiles {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Voltaic.ID);

	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileMultiSubnode>> TILE_MULTI = BLOCK_ENTITY_TYPES.register("multisubnode", () -> new BlockEntityType<>(TileMultiSubnode::new, Sets.newHashSet(VoltaicBlocks.BLOCK_MULTISUBNODE.get()), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMultiblockSlave>> TILE_MULTIBLOCK_SLAVE = BLOCK_ENTITY_TYPES.register("multiblockslave", () -> new BlockEntityType<>(TileMultiblockSlave::new, Sets.newHashSet(VoltaicBlocks.BLOCK_MULTIBLOCK_SLAVE.get()), null));

}
