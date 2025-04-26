package voltaic.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import voltaic.Voltaic;
import voltaic.registers.VoltaicDamageTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class VoltaicDamageTagsProvider extends DamageTypeTagsProvider {

	public VoltaicDamageTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Voltaic.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).add(VoltaicDamageTypes.ELECTRICITY, VoltaicDamageTypes.RADIATION);
		// .isMagic()
		tag(DamageTypeTags.WITCH_RESISTANT_TO).add(VoltaicDamageTypes.ELECTRICITY, VoltaicDamageTypes.RADIATION);
		tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add(VoltaicDamageTypes.ELECTRICITY, VoltaicDamageTypes.RADIATION);
		tag(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH).add(VoltaicDamageTypes.ELECTRICITY, VoltaicDamageTypes.RADIATION);
	}

}
