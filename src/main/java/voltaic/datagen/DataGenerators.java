package voltaic.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import voltaic.Voltaic;
import voltaic.datagen.client.VoltaicBlockStateProvider;
import voltaic.datagen.client.VoltaicItemModelsProvider;
import voltaic.datagen.client.VoltaicLangKeyProvider;
import voltaic.datagen.client.VoltaicSoundProvider;
import voltaic.datagen.server.VoltaicAdvancementProvider;
import voltaic.datagen.server.VoltaicRadiationShieldingProvider;
import voltaic.datagen.server.recipe.VoltaicRecipeProvider;
import voltaic.datagen.utils.client.BaseLangKeyProvider.Locale;
import voltaic.datagen.server.tags.VoltaicTagsProvider;
import voltaic.registers.VoltaicDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();

        PackOutput output = generator.getPackOutput();

        ExistingFileHelper helper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        if (event.includeServer()) {

            DatapackBuiltinEntriesProvider datapacks = new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                    //
                    .add(Registries.DAMAGE_TYPE, VoltaicDamageTypes::registerTypes)
                    //
                    , Set.of(Voltaic.ID));

            generator.addProvider(true, datapacks);
            VoltaicTagsProvider.addTagProviders(generator, output, datapacks.getRegistryProvider(), helper);
            generator.addProvider(true, new VoltaicRecipeProvider(output, lookupProvider));
            generator.addProvider(true, new VoltaicAdvancementProvider(output, datapacks.getRegistryProvider()));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new VoltaicBlockStateProvider(output, helper));
            generator.addProvider(true, new VoltaicItemModelsProvider(output, helper));
            generator.addProvider(true, new VoltaicLangKeyProvider(output, Locale.EN_US));
            generator.addProvider(true, new VoltaicSoundProvider(output, helper));
            generator.addProvider(true, new VoltaicRadiationShieldingProvider(output));
        }
    }

}
