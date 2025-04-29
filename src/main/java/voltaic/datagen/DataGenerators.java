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
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();

        ExistingFileHelper helper = event.getExistingFileHelper();


        if (event.includeServer()) {
        	
        	generator.addProvider(true, new VoltaicRecipeProvider(generator));
        	generator.addProvider(true, new VoltaicAdvancementProvider(generator));
        	generator.addProvider(true, new VoltaicRadiationShieldingProvider(generator));

        }
        if (event.includeClient()) {
            generator.addProvider(true, new VoltaicBlockStateProvider(generator, helper));
            generator.addProvider(true, new VoltaicItemModelsProvider(generator, helper));
            generator.addProvider(true, new VoltaicLangKeyProvider(generator, Locale.EN_US));
            generator.addProvider(true, new VoltaicSoundProvider(generator, helper));
        }
    }

}
