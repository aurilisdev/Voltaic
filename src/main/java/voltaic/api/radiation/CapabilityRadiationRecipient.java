package voltaic.api.radiation;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import voltaic.Voltaic;
import voltaic.api.radiation.util.IHazmatSuit;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.settings.VoltaicConfig;
import voltaic.registers.VoltaicAttachmentTypes;
import voltaic.registers.VoltaicEffects;
import voltaic.prefab.utilities.RadiationUtils;

public class CapabilityRadiationRecipient implements IRadiationRecipient {
    private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
	    EquipmentSlot.FEET };

    @Override
    public void recieveRadiation(LivingEntity entity, double rads, double strength) {
	if (!(rads > RadiationManager.MIN_APPLIED_RADIATION))
	    return;

        rads = Math.min(Double.MAX_VALUE, rads);
        strength = Double.isNaN(strength) ? 0 : Math.max(0, Math.min(Double.MAX_VALUE, strength));

	if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
	    recordRadiation(entity, rads, strength);
	    return;
	}

	if (entity.getEffect(VoltaicEffects.RADIATION_RESISTANCE) != null) {
	    if (rads <= VoltaicConfig.INSTANCE.IODINE_RESISTANCE_THRESHOLD.get()) {
		recordRadiation(entity, rads, strength);
		return;
	    }

	    rads *= VoltaicConfig.INSTANCE.IODINE_RAD_REDUCTION.get();
	}

	int hazmatPieces = 0;

	for (EquipmentSlot slot : ARMOR_SLOTS) {
	    ItemStack stack = entity.getItemBySlot(slot);

	    if (!(stack.getItem() instanceof IHazmatSuit)) {
		continue;
	    }

	    hazmatPieces++;

	    float damageChance = (float) (rads * 2.15 / 2169.9975);

	    if (Voltaic.RANDOM.nextFloat() < damageChance) {
                // Bound durability and enchantment work even for enormous storage doses.
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
		stack.hurtAndBreak((int) Math.min(remaining, Math.ceil(damageChance)), entity, slot);
	    }
	}

	if (hazmatPieces < 4) {
	    int amplitude = getAmplitudeFromRadiation(rads, strength);
	    int duration = getDurationFromRadiation(rads);

	    MobEffectInstance currentEffect = entity.getEffect(VoltaicEffects.RADIATION);

	    if (currentEffect != null) {
		duration = (int) Math.min(Integer.MAX_VALUE, (long) duration + currentEffect.getDuration());
		amplitude = Math.max(amplitude, currentEffect.getAmplifier());
	    }

	    entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION, duration, amplitude, false, true));
	}

	recordRadiation(entity, rads, strength);
    }

    private static void recordRadiation(LivingEntity entity, double rads, double strength) {
	entity.setData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT,
		RadiationUtils.saturatedAdd(entity.getData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT), rads));

	entity.setData(VoltaicAttachmentTypes.RECIEVED_RADIATIONSTRENGTH, strength);
    }

    @Override
    public RadioactiveObject getRecievedRadiation(LivingEntity entity) {
	return new RadioactiveObject(entity.getData(VoltaicAttachmentTypes.OLD_RECIEVED_RADIATIONSTRENGTH),
		entity.getData(VoltaicAttachmentTypes.OLD_RECIEVED_RADIATIONAMOUNT));
    }

    @Override
    public void tick(LivingEntity entity) {

	entity.setData(VoltaicAttachmentTypes.OLD_RECIEVED_RADIATIONAMOUNT,
		entity.getData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT));
	entity.setData(VoltaicAttachmentTypes.OLD_RECIEVED_RADIATIONSTRENGTH,
		entity.getData(VoltaicAttachmentTypes.RECIEVED_RADIATIONSTRENGTH));

	entity.setData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT, 0.0);
	entity.setData(VoltaicAttachmentTypes.RECIEVED_RADIATIONSTRENGTH, 0.0);

    }

    public static int getDurationFromRadiation(double radiation) {
	double ticks = radiation / 100.0 * 20.0;
	if (Double.isNaN(ticks)) {
	    return 1;
	}
	return (int) Math.min(Integer.MAX_VALUE, Math.max(1, ticks));
    }

    public static int getAmplitudeFromRadiation(double radiation, double strength) {
	double amplitude = radiation / 100.0 * strength;
	if (Double.isNaN(amplitude)) {
	    return 0;
	}
	return (int) Math.min(40.0, Math.max(0.0, amplitude));
    }

}
