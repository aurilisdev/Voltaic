package voltaic.registers;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.Voltaic;
import voltaic.common.block.BlockMultiSubnode;

public class VoltaicBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Voltaic.ID);

    public static final RegistryObject<BlockMultiSubnode> BLOCK_MULTISUBNODE = BLOCKS.register("multisubnode", BlockMultiSubnode::new);

}
