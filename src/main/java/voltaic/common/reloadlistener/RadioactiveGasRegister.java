package voltaic.common.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.gas.Gas;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveGases;
import voltaic.common.tags.VoltaicTags;
import voltaic.registers.VoltaicRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class RadioactiveGasRegister extends SimplePreparableReloadListener<JsonObject> {

    public static RadioactiveGasRegister INSTANCE = null;

    public static final String FOLDER = "radiation";
    public static final String FILE_NAME = "radioactive_gases";

    protected static final String JSON_EXTENSION = ".json";
    protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

    private static final Gson GSON = new Gson();

    private final HashMap<TagKey<Gas>, RadioactiveObject> tags = new HashMap<>();

    private final HashMap<Gas, RadioactiveObject> radioactiveGasMap = new HashMap<>();

    private final Logger logger = Voltaic.LOGGER;

    @Override
    protected JsonObject prepare(ResourceManager manager, ProfilerFiller profiler) {
        JsonObject combined = new JsonObject();

        List<Map.Entry<ResourceLocation, Resource>> resources = new ArrayList<>(manager.listResources(FOLDER, RadioactiveGasRegister::isJson).entrySet());
        Collections.reverse(resources);

        for (Map.Entry<ResourceLocation, Resource> entry : resources) {
            ResourceLocation loc = entry.getKey();
            final String namespace = loc.getNamespace();
            final String filePath = loc.getPath();
            final String dataPath = filePath.substring(FOLDER.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);

            final ResourceLocation jsonFile = new ResourceLocation(namespace, dataPath);

            Resource resource = entry.getValue();
            try (final InputStream inputStream = resource.open(); final Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
                final JsonObject json = (JsonObject) GsonHelper.fromJson(GSON, reader, JsonElement.class);

                json.entrySet().forEach(set -> {

                    if (combined.has(set.getKey())) {
                        combined.remove(set.getKey());
                    }

                    combined.add(set.getKey(), set.getValue());
                });

            } catch (RuntimeException | IOException exception) {
                logger.error("Data loader for {} could not read data {} from file {} in data pack {}", FOLDER, jsonFile, loc, resource.sourcePackId(), exception);
            }

        }
        return combined;
    }

    @Override
    protected void apply(JsonObject json, ResourceManager manager, ProfilerFiller profiler) {
        tags.clear();

        json.entrySet().forEach(set -> {

            String key = set.getKey();
            RadioactiveObject value = RadioactiveObject.CODEC.decode(JsonOps.INSTANCE, set.getValue()).result().get().getFirst();

            if (key.contains("#")) {

                key = key.substring(1);

                tags.put(VoltaicTags.Gases.create(new ResourceLocation(key)), value);

            } else {

                radioactiveGasMap.put(VoltaicRegistries.gasRegistry().getValue(new ResourceLocation(key)), value);


            }

        });

    }

    public void generateTagValues() {

        tags.forEach((tag, value) -> {
            VoltaicRegistries.gasRegistry().tags().getTag(tag).forEach(fluid -> {

                radioactiveGasMap.put(fluid, value);

            });
        });

        tags.clear();
    }

    public RadioactiveGasRegister subscribeAsSyncable(final SimpleChannel channel) {
    	MinecraftForge.EVENT_BUS.addListener(getDatapackSyncListener(channel));
        return this;
    }

    private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel) {
        return event -> {
            generateTagValues();
            ServerPlayer player = event.getPlayer();
            PacketSetClientRadioactiveGases packet = new PacketSetClientRadioactiveGases(radioactiveGasMap);
            PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
        };
    }

    public void setClientValues(HashMap<Gas, RadioactiveObject> mappedValues) {
        this.radioactiveGasMap.clear();
        this.radioactiveGasMap.putAll(mappedValues);
    }

    public static HashMap<Gas, RadioactiveObject> getValues() {
        return INSTANCE.radioactiveGasMap;
    }

    public static RadioactiveObject getValue(Gas gas) {
        return INSTANCE.radioactiveGasMap.getOrDefault(gas, RadioactiveObject.ZERO);
    }

    private static boolean isJson(final ResourceLocation filename) {
        return filename.getPath().contains(FILE_NAME + JSON_EXTENSION);
    }

}

