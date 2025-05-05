package voltaic.common.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import voltaic.Voltaic;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.packet.types.client.PacketSetClientRadioactiveFluids;
import voltaic.prefab.reloadlistener.AbstractReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class RadioactiveFluidRegister extends AbstractReloadListener<JsonObject> {

    public static RadioactiveFluidRegister INSTANCE = null;

    public static final String FOLDER = "radiation";
    public static final String FILE_NAME = "radioactive_fluids";

    protected static final String JSON_EXTENSION = ".json";
    protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

    private static final Gson GSON = new Gson();

    private final HashMap<INamedTag<Fluid>, RadioactiveObject> tags = new HashMap<>();

    private final HashMap<Fluid, RadioactiveObject> radioactiveFluidMap = new HashMap<>();

    private final Logger logger = Voltaic.LOGGER;

    @Override
    protected JsonObject prepare(IResourceManager manager, IProfiler profiler) {
    	JsonObject combined = new JsonObject();

		List<ResourceLocation> resources = new ArrayList<>(manager.listResources(FOLDER, RadioactiveFluidRegister::isJson));

		Collections.reverse(resources);

		for (ResourceLocation entry : resources) {

			final String namespace = entry.getNamespace();
			final String filePath = entry.getPath();
			final String dataPath = filePath.substring(FOLDER.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);

			final ResourceLocation jsonFile = new ResourceLocation(namespace, dataPath);

			try {

				for (IResource resource : manager.getResources(entry)) {

					try (final InputStream inputStream = resource.getInputStream(); final Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
						final JsonObject json = (JsonObject) JSONUtils.fromJson(GSON, reader, JsonElement.class);

						json.entrySet().forEach(set -> {

							if (combined.has(set.getKey())) {
								combined.remove(set.getKey());
							}

							combined.add(set.getKey(), set.getValue());
						});

					} catch (RuntimeException | IOException exception) {
						logger.error("Data loader for {} could not read data {} from file {} in data pack {}", FOLDER, jsonFile, entry, resource.getSourceName(), exception);
					}

				}

			} catch (IOException exception) {

				this.logger.error("Data loader for {} could not read data {} from file {}", FOLDER, jsonFile, entry, exception);

			}
		}
		return combined;
    }

    @Override
    protected void apply(JsonObject json, IResourceManager manager, IProfiler profiler) {
        tags.clear();

        json.entrySet().forEach(set -> {

            String key = set.getKey();
            RadioactiveObject value = RadioactiveObject.CODEC.decode(JsonOps.INSTANCE, set.getValue()).result().get().getFirst();

            if (key.contains("#")) {

                key = key.substring(1);

                tags.put(FluidTags.createOptional(new ResourceLocation(key)), value);

            } else {

                radioactiveFluidMap.put(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(key)), value);


            }

        });

    }

    public void generateTagValues() {

        tags.forEach((tag, value) -> {
        	FluidTags.getAllTags().getTag(tag.getName()).getValues().forEach(gas -> {

                radioactiveFluidMap.put(gas, value);

            });
        });

        tags.clear();
    }

    public RadioactiveFluidRegister subscribeAsSyncable(final SimpleChannel channel) {
    	MinecraftForge.EVENT_BUS.addListener(getDatapackSyncListener(channel));
        return this;
    }

    private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel) {
        return event -> {
            generateTagValues();
            ServerPlayerEntity player = event.getPlayer();
            PacketSetClientRadioactiveFluids packet = new PacketSetClientRadioactiveFluids(radioactiveFluidMap);
            PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
        };
    }

    public void setClientValues(HashMap<Fluid, RadioactiveObject> mappedValues) {
        this.radioactiveFluidMap.clear();
        this.radioactiveFluidMap.putAll(mappedValues);
    }

    public static HashMap<Fluid, RadioactiveObject> getValues() {
        return INSTANCE.radioactiveFluidMap;
    }

    public static RadioactiveObject getValue(Fluid fluid) {
        return INSTANCE.radioactiveFluidMap.getOrDefault(fluid, RadioactiveObject.ZERO);
    }

    private static boolean isJson(final String filename) {
        return filename.endsWith(FILE_NAME + JSON_EXTENSION);
    }

}
