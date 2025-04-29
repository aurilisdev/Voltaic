package voltaic.datagen.utils.server.radiation;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

public abstract class BaseRadiationShieldingProvider implements DataProvider {

    public static final String LOC = "data/" + Voltaic.ID + "/" + RadiationShieldingRegister.FOLDER + "/" + RadiationShieldingRegister.FILE_NAME;

    private final DataGenerator dataGenerator;
    private final String modID;

    public BaseRadiationShieldingProvider(DataGenerator dataGenerator, String modID) {
        this.dataGenerator = dataGenerator;
        this.modID = modID;
    }

    @Override
    public void run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        getRadiationShielding(json);

        Path parent = dataGenerator.getOutputFolder().resolve(LOC + ".json");
		try {

			DataProvider.saveStable(cache, json, parent);

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public abstract void getRadiationShielding(JsonObject json);

    public static void addBlock(Block block, double radiationAmount, double radiationLevel, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add(ForgeRegistries.BLOCKS.getKey(block).toString(), RadiationShielding.CODEC.encode(new RadiationShielding(radiationAmount, radiationLevel), JsonOps.INSTANCE, data).result().get());
    }

//    private void addTag(TagKey<Block> tag, double radiationAmount, double radiationLevel, JsonObject json) {
//        JsonObject data = new JsonObject();
//        json.add("#" + tag.location().toString(), RadiationShielding.CODEC.encode(new RadiationShielding(radiationAmount, radiationLevel), JsonOps.INSTANCE, data).getOrThrow());
//    }

    @Override
    public String getName() {
        return modID + " Radiation Shielding Provider";
    }


}
