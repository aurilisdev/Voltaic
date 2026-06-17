package voltaic.datagen.server.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import voltaic.datagen.server.tags.types.VoltaicDamageTagsProvider;
import voltaic.datagen.server.tags.types.VoltaicEnchantmentTagsProvider;

public class VoltaicTagsProvider {

    public static void addTagProviders(DataGenerator generator, PackOutput output,
	    CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
	generator.addProvider(true, new VoltaicDamageTagsProvider(output, lookupProvider, helper));
	generator.addProvider(true, new VoltaicEnchantmentTagsProvider(output, lookupProvider, helper));
    }

}
