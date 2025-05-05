package voltaic.api.radiation;

import voltaic.common.tags.VoltaicTags;
import voltaic.prefab.utilities.math.Color;
import voltaic.registers.VoltaicDamageTypes;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EffectRadiation extends Effect {

	public static final Color COLOR = new Color(78, 174, 49, 255);

	public EffectRadiation(EffectType typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}

	public EffectRadiation() {
		this(EffectType.HARMFUL, COLOR.color());
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level.random.nextFloat() < 0.033) {
			entity.hurt(VoltaicDamageTypes.RADIATION, (float) (Math.pow(amplifier, 1.3) + 1));
			if (entity instanceof PlayerEntity) {
				PlayerEntity pl = (PlayerEntity) entity;
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
		return Arrays.asList(ing.getItems());
	}

}
