package voltaic.datagen;

import java.util.List;
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
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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
            generator.addProvider(true, new VoltaicRecipeProvider(output));
            generator.addProvider(true, new ForgeAdvancementProvider(output, event.getLookupProvider(), helper, List.of(new VoltaicAdvancementProvider())));
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
