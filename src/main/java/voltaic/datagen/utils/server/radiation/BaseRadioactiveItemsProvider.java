package voltaic.datagen.utils.server.radiation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

public abstract class BaseRadioactiveItemsProvider implements IDataProvider {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public static final String LOC = "data/" + Voltaic.ID + "/" + RadioactiveItemRegister.FOLDER + "/" + RadioactiveItemRegister.FILE_NAME;

	private final DataGenerator dataGenerator;
	private final String modID;

	public BaseRadioactiveItemsProvider(DataGenerator dataGenerator, String modID) {
		this.dataGenerator = dataGenerator;
		this.modID = modID;
	}

	@Override
	public void run(DirectoryCache cache) {
		JsonObject json = new JsonObject();
		getRadioactiveItems(json);

		Path parent = dataGenerator.getOutputFolder().resolve(LOC + ".json");
		try {

			IDataProvider.save(GSON, cache, json, parent);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void getRadioactiveItems(JsonObject json);

	@SuppressWarnings("unused")
	public void addItem(Item item, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add(ForgeRegistries.ITEMS.getKey(item).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	public void addTag(INamedTag<Item> tag, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add("#" + tag.getName().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	@Override
	public String getName() {
		return modID + " Radioactive Items Provider";
	}

}
