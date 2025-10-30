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
import net.minecraft.world.level.material.Fluid;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;

public abstract class BaseRadioactiveFluidsProvider implements DataProvider {

    private final PackOutput output;
    private final String modID;
    private final String loc;

    public BaseRadioactiveFluidsProvider(PackOutput output, String modID) {
        this.output = output;
        this.modID = modID;
        loc = "data/" + Voltaic.ID + "/" + RadioactiveFluidRegister.FOLDER + "/" + modID + "_" + RadioactiveFluidRegister.FILE_NAME;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        getRadioactiveFluids(json);

        Path parent = output.getOutputFolder().resolve(loc + ".json");

        return CompletableFuture.allOf(DataProvider.saveStable(cache, json, parent));
    }

    public abstract void getRadioactiveFluids(JsonObject json);

    public void addFluid(Fluid fluid, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add(BuiltInRegistries.FLUID.getKey(fluid).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).getOrThrow());
    }

    public void addTag(TagKey<Fluid> tag, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add("#" + tag.location().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).getOrThrow());
    }

    @Override
    public String getName() {
        return modID + " Radioactive Fluids Provider";
    }
}
