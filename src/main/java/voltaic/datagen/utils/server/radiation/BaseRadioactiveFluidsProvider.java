package voltaic.datagen.utils.server.radiation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;

public abstract class BaseRadioactiveFluidsProvider implements IDataProvider {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator dataGenerator;
	private final String modID;
	private final String loc;

	public BaseRadioactiveFluidsProvider(DataGenerator dataGenerator, String modID) {
		this.dataGenerator = dataGenerator;
		this.modID = modID;
		loc = "data/" + Voltaic.ID + "/" + RadioactiveFluidRegister.FOLDER + "/" + modID + "_" +RadioactiveFluidRegister.FILE_NAME;
	}

	@Override
	public void run(DirectoryCache cache) {
		JsonObject json = new JsonObject();
		getRadioactiveItems(json);

		Path parent = dataGenerator.getOutputFolder().resolve(loc + ".json");
		try {

			IDataProvider.save(GSON, cache, json, parent);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void getRadioactiveItems(JsonObject json);

	@SuppressWarnings("unused")
	public void addItem(Fluid fluid, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add(ForgeRegistries.FLUIDS.getKey(fluid).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	public void addTag(INamedTag<Fluid> tag, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add("#" + tag.getName().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	@Override
	public String getName() {
		return modID + " Radioactive Items Provider";
	}

}
