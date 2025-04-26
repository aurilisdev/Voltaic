package voltaic.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import voltaic.Voltaic;
import org.jetbrains.annotations.Nullable;

import voltaic.common.tags.VoltaicTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class VoltaicEnchantmentTagsProvider extends EnchantmentTagsProvider {

    public VoltaicEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, Voltaic.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(VoltaicTags.Enchantments.EFFICIENCY).add(Enchantments.EFFICIENCY);
        tag(VoltaicTags.Enchantments.UNBREAKING).add(Enchantments.UNBREAKING);
        tag(VoltaicTags.Enchantments.SILK_TOUCH).add(Enchantments.SILK_TOUCH);
    }
}
