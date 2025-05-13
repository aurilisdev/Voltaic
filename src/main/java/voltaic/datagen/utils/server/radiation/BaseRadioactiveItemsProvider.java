package voltaic.datagen.utils.server.radiation;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRadioactiveItemsProvider implements DataProvider {

	private final PackOutput output;
	private final String modID;
	private final String loc;

	public BaseRadioactiveItemsProvider(PackOutput output, String modID) {
		this.output = output;
		this.modID = modID;
		loc = "data/" + Voltaic.ID + "/" + RadioactiveItemRegister.FOLDER + "/" + modID + "_" + RadioactiveItemRegister.FILE_NAME;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		JsonObject json = new JsonObject();
		getRadioactiveItems(json);

		Path parent = output.getOutputFolder().resolve(loc + ".json");

		return CompletableFuture.allOf(DataProvider.saveStable(cache, json, parent));
	}

	public abstract void getRadioactiveItems(JsonObject json);

	public void addItem(Item item, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add(BuiltInRegistries.ITEM.getKey(item).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	public void addTag(TagKey<Item> tag, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add("#" + tag.location().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	@Override
	public String getName() {
		return modID + " Radioactive Items Provider";
	}

}
