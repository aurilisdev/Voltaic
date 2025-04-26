package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.datagen.utils.client.BaseBlockstateProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import voltaic.registers.VoltaicBlocks;

public class VoltaicBlockStateProvider extends BaseBlockstateProvider {

    public VoltaicBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper, Voltaic.ID);
    }

    @Override
    protected void registerStatesAndModels() {
        airBlock(VoltaicBlocks.BLOCK_MULTISUBNODE, "block/multisubnode", false);
        slaveNode(VoltaicBlocks.BLOCK_MULTIBLOCK_SLAVE.get(), "block/multisubnode");
    }
}
