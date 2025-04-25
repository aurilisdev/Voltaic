package voltaic.datagen.server;

import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import voltaic.datagen.utils.server.advancement.BaseAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class VoltaicAdvancementProvider extends BaseAdvancementProvider {

    public VoltaicAdvancementProvider() {
        super(Voltaic.ID);
    }

    public void generate(Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {

        advancement("dispenseguidebook")
                //
                .addCriterion("SpawnIn", PlayerTrigger.TriggerInstance.tick())
                //
                .rewards(Builder.loot(new ResourceLocation("advancement_reward/electroguidebook")))
                //
                .condition(new ConfigCondition())
				//
				.save(saver);


    }
}
