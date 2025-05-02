package voltaic.datagen.utils.server.advancement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;

public abstract class BaseAdvancementProvider implements DataProvider {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public final String modID;
	public final DataGenerator generator;

	public BaseAdvancementProvider(DataGenerator generator, String modID) {
		this.generator = generator;
		this.modID = modID;
	}

	@Override
	public void run(HashCache cache) throws IOException {
		Set<ResourceLocation> registeredAdvancements = Sets.newHashSet();
		Path path = generator.getOutputFolder();
		Consumer<AdvancementBuilder> consumer = advancementBuilder -> {
			if (!registeredAdvancements.add(advancementBuilder.id)) {
				throw new IllegalStateException("Duplicate advancement " + advancementBuilder.id);
			}
			Path filePath = path.resolve("data/" + advancementBuilder.id.getNamespace() + "/advancements/" + advancementBuilder.id.getPath() + ".json");

			try {
				DataProvider.save(GSON, cache, advancementBuilder.serializeToJson(), filePath);
			} catch (IOException ioexception) {
				Voltaic.LOGGER.error("Couldn't save advancement {}", filePath, ioexception);
			}
		};

		registerAdvancements(consumer);

	}
	
	public abstract void registerAdvancements(Consumer<AdvancementBuilder> consumer);

	@Override
	public String getName() {
		return modID + " Advancement Provider";
	}
	
	public AdvancementBuilder advancement(String name) {
		return AdvancementBuilder.create(new ResourceLocation(modID, name));
	}	

}
