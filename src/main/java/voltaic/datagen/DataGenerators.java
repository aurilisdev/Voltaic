package voltaic.datagen;

import voltaic.Voltaic;
import voltaic.datagen.client.VoltaicBlockStateProvider;
import voltaic.datagen.client.VoltaicItemModelsProvider;
import voltaic.datagen.client.VoltaicLangKeyProvider;
import voltaic.datagen.client.VoltaicSoundProvider;
import voltaic.datagen.server.VoltaicAdvancementProvider;
import voltaic.datagen.server.VoltaicRadiationShieldingProvider;
import voltaic.datagen.server.recipe.VoltaicRecipeProvider;
import voltaic.datagen.utils.client.BaseLangKeyProvider.Locale;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();

        ExistingFileHelper helper = event.getExistingFileHelper();


        if (event.includeServer()) {
        	
        	generator.addProvider(new VoltaicRecipeProvider(generator));
        	generator.addProvider(new VoltaicAdvancementProvider(generator));
        	generator.addProvider(new VoltaicRadiationShieldingProvider(generator));

        }
        if (event.includeClient()) {
            generator.addProvider(new VoltaicBlockStateProvider(generator, helper));
            generator.addProvider(new VoltaicItemModelsProvider(generator, helper));
            generator.addProvider(new VoltaicLangKeyProvider(generator, Locale.EN_US));
            generator.addProvider(new VoltaicSoundProvider(generator, helper));
        }
    }

}
