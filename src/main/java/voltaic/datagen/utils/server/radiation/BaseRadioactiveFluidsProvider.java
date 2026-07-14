package voltaic.datagen.utils.server.radiation;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;

public abstract class BaseRadioactiveFluidsProvider implements DataProvider {

	private final DataGenerator dataGenerator;
	private final String modID;
	private final String loc;

	public BaseRadioactiveFluidsProvider(DataGenerator dataGenerator, String modID) {
		this.dataGenerator = dataGenerator;
		this.modID = modID;
		loc = "data/" + Voltaic.ID + "/" + RadioactiveFluidRegister.FOLDER + "/" + modID + "_" +RadioactiveFluidRegister.FILE_NAME;
	}

	@Override
	public void run(CachedOutput cache) {
		JsonObject json = new JsonObject();
		getRadioactiveFluids(json);

		Path parent = dataGenerator.getOutputFolder().resolve(loc + ".json");
		try {

			DataProvider.saveStable(cache, json, parent);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void getRadioactiveFluids(JsonObject json);

	public void addFluid(Fluid fluid, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add(ForgeRegistries.FLUIDS.getKey(fluid).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	public void addTag(TagKey<Fluid> tag, double radiationAmount, double radiationStrength, JsonObject json) {
		JsonObject data = new JsonObject();
		json.add("#" + tag.location().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
	}

	@Override
	public String getName() {
		return modID + " Radioactive Fluids Provider";
	}

}
