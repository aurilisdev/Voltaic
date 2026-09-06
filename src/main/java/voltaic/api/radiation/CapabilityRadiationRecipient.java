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

public class CapabilityRadiationRecipient implements IRadiationRecipient {
    private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
	    EquipmentSlot.FEET };

    @Override
    public void recieveRadiation(LivingEntity entity, double rads, double strength) {
	if (rads <= RadiationManager.MIN_APPLIED_RADIATION)
	    return;

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
		stack.hurtAndBreak((int) Math.ceil(damageChance), entity, slot);
	    }
	}

	if (hazmatPieces < 4) {
	    int amplitude = getAmplitudeFromRadiation(rads, strength);
	    int duration = getDurationFromRadiation(rads);

	    MobEffectInstance currentEffect = entity.getEffect(VoltaicEffects.RADIATION);

	    if (currentEffect != null) {
		duration += currentEffect.getDuration();
		amplitude = Math.max(amplitude, currentEffect.getAmplifier());
	    }

	    entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION, duration, amplitude, false, true));
	}

	recordRadiation(entity, rads, strength);
    }

    private static void recordRadiation(LivingEntity entity, double rads, double strength) {
	entity.setData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT,
		entity.getData(VoltaicAttachmentTypes.RECIEVED_RADIATIONAMOUNT) + rads);

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
	return (int) Math.max(1, radiation / 100.0 * 20.0);
    }

    public static int getAmplitudeFromRadiation(double radiation, double strength) {
	return (int) Math.min(40.0, radiation / 100.0 * strength);
    }

}
