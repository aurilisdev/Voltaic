package voltaic.datagen.utils.server.radiation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveBlockRegister;

public abstract class BaseRadioactiveBlocksProvider implements DataProvider {

    private final PackOutput output;
    private final String modID;
    private final String loc;

    public BaseRadioactiveBlocksProvider(PackOutput output, String modID) {
	this.output = output;
	this.modID = modID;
	loc = "data/" + Voltaic.ID + "/" + RadioactiveBlockRegister.FOLDER + "/" + modID + "_"
		+ RadioactiveBlockRegister.FILE_NAME;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
	JsonObject json = new JsonObject();
	getRadioactiveBlocks(json);

	Path parent = output.getOutputFolder().resolve(loc + ".json");

	return CompletableFuture.allOf(DataProvider.saveStable(cache, json, parent));
    }

    public abstract void getRadioactiveBlocks(JsonObject json);

    public void addBlock(Block block, double radiationAmount, double radiationStrength, JsonObject json) {
	JsonObject data = new JsonObject();
	json.add(BuiltInRegistries.BLOCK.getKey(block).toString(),
		RadioactiveObject.CODEC
			.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data)
			.result().get());
    }

    public void addTag(TagKey<Block> tag, double radiationAmount, double radiationStrength, JsonObject json) {
	JsonObject data = new JsonObject();
	json.add("#" + tag.location().toString(),
		RadioactiveObject.CODEC
			.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data)
			.result().get());
    }

    @Override
    public String getName() {
	return modID + " Radioactive Blocks Provider";
    }
}
