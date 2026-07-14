package voltaic.common.reloadlistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveBlocks;

public class RadioactiveBlockRegister extends SimplePreparableReloadListener<JsonObject> {

    public static RadioactiveBlockRegister INSTANCE = null;

    public static final String FOLDER = "radiation";
    public static final String FILE_NAME = "radioactive_blocks";

    protected static final String JSON_EXTENSION = ".json";
    protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

    private static final Gson GSON = new Gson();

    private final HashMap<TagKey<Block>, RadioactiveObject> tags = new HashMap<>();

    private final HashMap<Block, RadioactiveObject> radioactiveBlockMap = new HashMap<>();

    private final Logger logger = Voltaic.LOGGER;

    @Override
    protected JsonObject prepare(ResourceManager manager, ProfilerFiller profiler) {
        JsonObject combined = new JsonObject();

        List<Map.Entry<ResourceLocation, Resource>> resources = new ArrayList<>(manager.listResources(FOLDER, RadioactiveBlockRegister::isJson).entrySet());
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

                tags.put(BlockTags.create(new ResourceLocation(key)), value);

            } else {

                radioactiveBlockMap.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key)), value);


            }

        });

    }

    public void generateTagValues() {

        tags.forEach((tag, value) -> {
            ForgeRegistries.BLOCKS.tags().getTag(tag).forEach(gas -> {

                radioactiveBlockMap.put(gas, value);

            });
        });

        tags.clear();
    }

    public RadioactiveBlockRegister subscribeAsSyncable(final SimpleChannel channel) {
    	MinecraftForge.EVENT_BUS.addListener(getDatapackSyncListener(channel));
        return this;
    }

    private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel) {
        return event -> {
            generateTagValues();
            ServerPlayer player = event.getPlayer();
            PacketSetClientRadioactiveBlocks packet = new PacketSetClientRadioactiveBlocks(radioactiveBlockMap);
            PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
        };
    }

    public void setClientValues(HashMap<Block, RadioactiveObject> mappedValues) {
        this.radioactiveBlockMap.clear();
        this.radioactiveBlockMap.putAll(mappedValues);
    }

    public static HashMap<Block, RadioactiveObject> getValues() {
        return INSTANCE.radioactiveBlockMap;
    }

    public static RadioactiveObject getValue(Block block) {
        return INSTANCE.radioactiveBlockMap.getOrDefault(block, RadioactiveObject.ZERO);
    }

    private static boolean isJson(final ResourceLocation filename) {
        return filename.getPath().contains(FILE_NAME + JSON_EXTENSION);
    }
}
