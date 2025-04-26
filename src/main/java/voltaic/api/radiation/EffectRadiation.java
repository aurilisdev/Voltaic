package voltaic.api.radiation;

import voltaic.common.tags.VoltaicTags;
import voltaic.prefab.utilities.math.Color;
import voltaic.registers.VoltaicDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.stream.Stream;

public class EffectRadiation extends MobEffect {

	public static final Color COLOR = new Color(78, 174, 49, 255);

	public EffectRadiation(MobEffectCategory typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}

	public EffectRadiation() {
		this(MobEffectCategory.HARMFUL, COLOR.color());
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level().random.nextFloat() < 0.033) {
			entity.hurt(entity.damageSources().source(VoltaicDamageTypes.RADIATION, entity), (float) (Math.pow(amplifier, 1.3) + 1));
			if (entity instanceof Player pl) {
				pl.causeFoodExhaustion(0.05F * (amplifier + 1));
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
		return true;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		Ingredient ing = Ingredient.of(VoltaicTags.Items.CURES_RADIATION);
		return Stream.of(ing.getItems()).toList();
	}

}
