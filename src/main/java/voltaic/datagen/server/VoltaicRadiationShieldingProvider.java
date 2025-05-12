package voltaic.datagen.server;

import com.google.gson.JsonObject;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import voltaic.Voltaic;
import voltaic.datagen.utils.server.radiation.BaseRadiationShieldingProvider;

public class VoltaicRadiationShieldingProvider extends BaseRadiationShieldingProvider {
    public VoltaicRadiationShieldingProvider(DataGenerator generator) {
        super(generator, Voltaic.ID);
    }

    @Override
    public void getRadiationShielding(JsonObject json) {
        addBlock(Blocks.WATER, 5000, 1, json);
    }
}
