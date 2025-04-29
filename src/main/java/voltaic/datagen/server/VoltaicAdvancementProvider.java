package voltaic.datagen.server;

import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import voltaic.datagen.utils.server.advancement.AdvancementBuilder;
import voltaic.datagen.utils.server.advancement.BaseAdvancementProvider;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class VoltaicAdvancementProvider extends BaseAdvancementProvider {

	public VoltaicAdvancementProvider(DataGenerator generator) {
		super(generator, Voltaic.ID);
	}

	public void registerAdvancements(Consumer<AdvancementBuilder> consumer) {

		advancement("dispenseguidebook")
				//
				.addCriterion("SpawnIn", new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.getId(), EntityPredicate.Composite.ANY))
				//
				.rewards(Builder.loot(new ResourceLocation("advancement_reward/electroguidebook")))
				//
				.condition(new ConfigCondition())
				//
				.save(consumer);

	}
}
