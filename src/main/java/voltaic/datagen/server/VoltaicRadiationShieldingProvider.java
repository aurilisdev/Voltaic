package voltaic.datagen.server;

import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;
import voltaic.Voltaic;
import voltaic.datagen.utils.server.radiation.BaseRadiationShieldingProvider;

public class VoltaicRadiationShieldingProvider extends BaseRadiationShieldingProvider {
    public VoltaicRadiationShieldingProvider(DataGenerator generator) {
        super(generator, Voltaic.ID);
    }

    @Override
    public void getRadiationShielding(JsonObject json) {
	addBlock(Blocks.WATER, 0.70, 1, json);
    }
}
