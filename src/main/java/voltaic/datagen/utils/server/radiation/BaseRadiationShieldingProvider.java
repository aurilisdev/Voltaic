package voltaic.datagen.utils.server.radiation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.reloadlistener.RadiationShieldingRegister;

public abstract class BaseRadiationShieldingProvider implements DataProvider {

    private final PackOutput output;
    private final String modID;
    private final String loc;

    public BaseRadiationShieldingProvider(PackOutput output, String modID) {
        this.output = output;
        this.modID = modID;
        loc = "data/" + Voltaic.ID + "/" + RadiationShieldingRegister.FOLDER + "/" + modID + "_" + RadiationShieldingRegister.FILE_NAME;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        getRadiationShielding(json);

        Path parent = output.getOutputFolder().resolve(loc + ".json");

        return CompletableFuture.allOf(DataProvider.saveStable(cache, json, parent));
    }

    public abstract void getRadiationShielding(JsonObject json);

    public static void addBlock(Block block, double radiationAmount, double radiationLevel, JsonObject json) {
        JsonObject data = new JsonObject();
        json.add(BuiltInRegistries.BLOCK.getKey(block).toString(), RadiationShielding.CODEC.encode(new RadiationShielding(radiationAmount, radiationLevel), JsonOps.INSTANCE, data).getOrThrow());
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
