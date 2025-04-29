package voltaic.datagen.utils.server.advancement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Sets;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataGenerator.PathProvider;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;

public abstract class BaseAdvancementProvider implements DataProvider {

	public final String modID;
	private final PathProvider pathProvider;

	public BaseAdvancementProvider(DataGenerator generatorIn, String modID) {
		this.pathProvider = generatorIn.createPathProvider(DataGenerator.Target.DATA_PACK, "advancements");
		this.modID = modID;
	}

	@Override
	public void run(CachedOutput pOutput) throws IOException {
		Set<ResourceLocation> registeredAdvancements = Sets.newHashSet();
		Consumer<AdvancementBuilder> consumer = advancementBuilder -> {
			if (!registeredAdvancements.add(advancementBuilder.id)) {
				throw new IllegalStateException("Duplicate advancement " + advancementBuilder.id);
			}
			Path path = this.pathProvider.json(advancementBuilder.id);

			try {
				DataProvider.saveStable(pOutput, advancementBuilder.serializeToJson(), path);
			} catch (IOException ioexception) {
				Voltaic.LOGGER.error("Couldn't save advancement {}", path, ioexception);
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
