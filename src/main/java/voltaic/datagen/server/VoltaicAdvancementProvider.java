package voltaic.datagen.server;

import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import voltaic.datagen.utils.server.advancement.BaseAdvancementProvider;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public class VoltaicAdvancementProvider extends BaseAdvancementProvider {

    public VoltaicAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Voltaic.ID);
    }

    public void generate(HolderLookup.Provider registries) {

        advancement("dispenseguidebook")
                //
                .addCriterion("SpawnIn", PlayerTrigger.TriggerInstance.tick())
                //
                .rewards(AdvancementRewards.Builder.loot(ResourceKey.create(Registries.LOOT_TABLE, Voltaic.vanillarl("advancement_reward/electroguidebook"))))
                //
                .condition(new ConfigCondition());


    }
}
