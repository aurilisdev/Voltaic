package voltaic.datagen.server;

import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import voltaic.datagen.utils.server.advancement.AdvancementBuilder;
import voltaic.datagen.utils.server.advancement.BaseAdvancementProvider;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.command.FunctionObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class VoltaicAdvancementProvider extends BaseAdvancementProvider {

	public VoltaicAdvancementProvider(DataGenerator generator) {
		super(generator, Voltaic.ID);
	}

	public void registerAdvancements(Consumer<AdvancementBuilder> consumer) {

		advancement("dispenseguidebook")
				//
				.addCriterion("SpawnIn", new TickTrigger.Instance(EntityPredicate.AndPredicate.ANY))
				//
				.rewards(new AdvancementRewards(0, new ResourceLocation[] { new ResourceLocation("advancement_reward/electroguidebook") }, new ResourceLocation[0], FunctionObject.CacheableFunction.NONE))
				//
				.condition(new ConfigCondition())
				//
				.save(consumer);

	}
}
