package voltaic.registers;

import com.google.common.collect.Sets;

import voltaic.Voltaic;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VoltaicTiles {
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Voltaic.ID);

	public static final RegistryObject<TileEntityType<TileMultiSubnode>> TILE_MULTI = BLOCK_ENTITY_TYPES.register("multisubnode", () -> new TileEntityType<>(TileMultiSubnode::new, Sets.newHashSet(VoltaicBlocks.BLOCK_MULTISUBNODE.get()), null));

}
