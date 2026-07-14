package voltaic.datagen.server;

import java.util.function.Consumer;

import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import voltaic.datagen.utils.server.advancement.AdvancementBuilder;
import voltaic.datagen.utils.server.advancement.BaseAdvancementProvider;

public class VoltaicAdvancementProvider extends BaseAdvancementProvider {

	public VoltaicAdvancementProvider(DataGenerator generator) {
		super(generator, Voltaic.ID);
	}

	@Override
	public void registerAdvancements(Consumer<AdvancementBuilder> consumer) {

		advancement("dispenseguidebook")
				//
				.addCriterion("SpawnIn", new TickTrigger.TriggerInstance(EntityPredicate.Composite.ANY))
				//
				.rewards(Builder.loot(new ResourceLocation("advancement_reward/electroguidebook")))
				//
				.condition(new ConfigCondition())
				//
				.save(consumer);

	}
}
