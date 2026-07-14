package voltaic.datagen.utils.server.radiation;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveBlockRegister;

public abstract class BaseRadioactiveBlocksProvider implements DataProvider {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator dataGenerator;
    private final String modID;
    private final String loc;

    public BaseRadioactiveBlocksProvider(DataGenerator dataGenerator, String modID) {
    	this.dataGenerator = dataGenerator;
        this.modID = modID;
        loc = "data/" + Voltaic.ID + "/" + RadioactiveBlockRegister.FOLDER + "/" + modID + "_" + RadioactiveBlockRegister.FILE_NAME;
    }

    @Override
    public void run(HashCache cache) {
		JsonObject json = new JsonObject();
        getRadioactiveBlocks(json);

        Path parent = dataGenerator.getOutputFolder().resolve(loc + ".json");

        try {

			DataProvider.save(GSON, cache, json, parent);

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public abstract void getRadioactiveBlocks(JsonObject json);

    public void addBlock(Block block, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add(ForgeRegistries.BLOCKS.getKey(block).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
    }

    public void addTag(TagKey<Block> tag, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add("#" + tag.location().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
    }

    @Override
    public String getName() {
        return modID + " Radioactive Blocks Provider";
    }
}
