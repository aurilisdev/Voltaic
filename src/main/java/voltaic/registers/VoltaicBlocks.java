package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.block.BlockMultiSubnode;
import voltaic.common.block.BlockMultiblockSlave;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Voltaic.ID);

    public static final DeferredHolder<Block, BlockMultiSubnode> BLOCK_MULTISUBNODE = BLOCKS.register("multisubnode", BlockMultiSubnode::new);
    public static final DeferredHolder<Block, BlockMultiblockSlave> BLOCK_MULTIBLOCK_SLAVE = BLOCKS.register("multiblockslave", BlockMultiblockSlave::new);

}
