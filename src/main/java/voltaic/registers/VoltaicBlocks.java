package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.block.BlockMultiSubnode;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Voltaic.ID);

    public static final RegistryObject<BlockMultiSubnode> BLOCK_MULTISUBNODE = BLOCKS.register("multisubnode", BlockMultiSubnode::new);

}
