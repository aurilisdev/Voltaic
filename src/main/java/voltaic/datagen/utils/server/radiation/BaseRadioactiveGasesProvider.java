package voltaic.datagen.utils.server.radiation;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.gas.Gas;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveGasRegister;
import voltaic.registers.VoltaicRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRadioactiveGasesProvider implements DataProvider {

    public static final String LOC = "data/" + Voltaic.ID + "/" + RadioactiveGasRegister.FOLDER + "/" + RadioactiveGasRegister.FILE_NAME;

    private final PackOutput output;
    private final String modID;

    public BaseRadioactiveGasesProvider(PackOutput output, String modID) {
        this.output = output;
        this.modID = modID;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        getRadioactiveItems(json);

        Path parent = output.getOutputFolder().resolve(LOC + ".json");

        return CompletableFuture.allOf(DataProvider.saveStable(cache, json, parent));
    }

    public abstract void getRadioactiveItems(JsonObject json);

    @SuppressWarnings("unused")
    public void addItem(Gas gas, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add(VoltaicRegistries.gasRegistry().getKey(gas).toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
    }

    public void addTag(TagKey<Gas> tag, double radiationAmount, double radiationStrength, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add("#" + tag.location().toString(), RadioactiveObject.CODEC.encode(new RadioactiveObject(radiationStrength, radiationAmount), JsonOps.INSTANCE, data).result().get());
    }

    @Override
    public String getName() {
        return modID + " Radioactive Gases Provider";
    }

}
