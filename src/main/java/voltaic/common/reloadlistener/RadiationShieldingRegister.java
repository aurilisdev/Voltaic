package voltaic.common.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import voltaic.Voltaic;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.common.packet.types.client.PacketSetClientRadiationShielding;
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

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class RadiationShieldingRegister extends SimplePreparableReloadListener<JsonObject> {

	public static RadiationShieldingRegister INSTANCE = null;

	public static final String FOLDER = "radiation";
	public static final String FILE_NAME = "radiation_shielding";

	protected static final String JSON_EXTENSION = ".json";
	protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

	private static final Gson GSON = new Gson();

	private final HashMap<TagKey<Block>, RadiationShielding> tags = new HashMap<>();

	private final HashMap<Block, RadiationShielding> radiationShieldingMap = new HashMap<>();

	private final Logger logger = Voltaic.LOGGER;

	@Override
	protected JsonObject prepare(ResourceManager manager, ProfilerFiller profiler) {
		JsonObject combined = new JsonObject();

		List<ResourceLocation> resources = new ArrayList<>(manager.listResources(FOLDER, RadiationShieldingRegister::isJson));

		Collections.reverse(resources);

		for (ResourceLocation entry : resources) {

			final String namespace = entry.getNamespace();
			final String filePath = entry.getPath();
			final String dataPath = filePath.substring(FOLDER.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);

			final ResourceLocation jsonFile = new ResourceLocation(namespace, dataPath);

			try {

				for (Resource resource : manager.getResources(entry)) {

					try (final InputStream inputStream = resource.getInputStream(); final Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
						final JsonObject json = (JsonObject) GsonHelper.fromJson(GSON, reader, JsonElement.class);

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
	protected void apply(JsonObject json, ResourceManager manager, ProfilerFiller profiler) {
		tags.clear();

		json.entrySet().forEach(set -> {

			String key = set.getKey();
			RadiationShielding value = RadiationShielding.CODEC.decode(JsonOps.INSTANCE, set.getValue()).result().get().getFirst();

			if (key.contains("#")) {

				key = key.substring(1);

				tags.put(BlockTags.create(new ResourceLocation(key)), value);

			} else {

				radiationShieldingMap.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key)), value);

			}

		});

	}

	public void generateTagValues() {

		tags.forEach((tag, value) -> {
			ForgeRegistries.BLOCKS.tags().getTag(tag).forEach(block -> {

				radiationShieldingMap.put(block, value);

			});
		});

		tags.clear();
	}

	public RadiationShieldingRegister subscribeAsSyncable(final SimpleChannel channel) {
		MinecraftForge.EVENT_BUS.addListener(getDatapackSyncListener(channel));
		return this;
	}

	private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(final SimpleChannel channel) {
		return event -> {
			generateTagValues();
			ServerPlayer player = event.getPlayer();
			PacketSetClientRadiationShielding packet = new PacketSetClientRadiationShielding(radiationShieldingMap);
			PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
			channel.send(target, packet);
		};
	}

	public void setClientValues(HashMap<Block, RadiationShielding> mappedValues) {
		this.radiationShieldingMap.clear();
		this.radiationShieldingMap.putAll(mappedValues);
	}

	public static HashMap<Block, RadiationShielding> getValues() {
		return INSTANCE.radiationShieldingMap;
	}

	public static RadiationShielding getValue(Block block) {
		return INSTANCE.radiationShieldingMap.getOrDefault(block, RadiationShielding.ZERO);
	}

	private static boolean isJson(final String filename) {
		return filename.endsWith(FILE_NAME + JSON_EXTENSION);
	}

}
