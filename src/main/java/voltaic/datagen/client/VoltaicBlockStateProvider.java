package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.datagen.utils.client.BaseBlockstateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.registers.VoltaicBlocks;

public class VoltaicBlockStateProvider extends BaseBlockstateProvider {

    public VoltaicBlockStateProvider(DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, exFileHelper, Voltaic.ID);
    }

    @Override
    protected void registerStatesAndModels() {
        airBlock(VoltaicBlocks.BLOCK_MULTISUBNODE, "block/multisubnode", false);
    }
}
