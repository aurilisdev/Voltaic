package voltaic.api.radiation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import voltaic.Voltaic;
import voltaic.api.radiation.util.IHazmatSuit;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.settings.VoltaicConstants;
import voltaic.compatibility.mekanism.MekanismRadiationCompat;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicEffects;

public class CapabilityRadiationRecipient implements IRadiationRecipient, ICapabilitySerializable<CompoundTag> {

    private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
	    EquipmentSlot.FEET };

    private final LazyOptional<IRadiationRecipient> lazyOptional = LazyOptional.of(() -> this);

    private double recieved = 0;
    private double recievedStrength = 0;

    private double prevRecieved = 0;
    private double prevRecievedStrength = 0;

    public CapabilityRadiationRecipient() {

    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
	if (cap == null || cap != VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT) {
	    return LazyOptional.empty();
	}
	return lazyOptional.cast();
    }

    @Override
    public CompoundTag serializeNBT() {
	CompoundTag tag = new CompoundTag();
	tag.putDouble("recieved", recieved);
	tag.putDouble("recievedstrength", recievedStrength);
	tag.putDouble("prevrecieved", prevRecieved);
	tag.putDouble("prevrecievedstrength", prevRecievedStrength);
	return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
	if (VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT == null) {
	    return;
	}
	recieved = nbt.getDouble("recieved");
	recievedStrength = nbt.getDouble("recievedstrength");
	prevRecieved = nbt.getDouble("prevrecieved");
	prevRecievedStrength = nbt.getDouble("prevrecievedstrength");
    }

    @Override
    public void recieveRadiation(LivingEntity entity, double rads, double strength) {

	if (rads <= RadiationManager.MIN_APPLIED_RADIATION) {
	    return;
	}

	if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
	    recieved += rads;
	    recievedStrength = strength;
	    return;
	}

	if (entity.hasEffect(VoltaicEffects.RADIATION_RESISTANCE.get())) {
	    if (rads <= VoltaicConstants.IODINE_RESISTANCE_THRESHHOLD) {
		recieved += rads;
		recievedStrength = strength;
		return;
	    }
	    rads = rads * VoltaicConstants.IODINE_RAD_REDUCTION;
	}

	double shielding = 0.0;
	float damageChance = (float) (rads * 2.15 / 2169.9975);

	for (EquipmentSlot slot : ARMOR_SLOTS) {
	    ItemStack stack = entity.getItemBySlot(slot);
	    double armorShielding = getArmorShielding(slot);

	    if (stack.getItem() instanceof IHazmatSuit) {
		shielding += armorShielding;
		if (Voltaic.RANDOM.nextFloat() < damageChance) {
		    stack.hurtAndBreak((int) Math.ceil(damageChance), entity,
			    wearer -> wearer.broadcastBreakEvent(slot));
		}
	    } else {
		shielding += MekanismRadiationCompat.applyRadiationShielding(stack, damageChance, armorShielding);
	    }
	}

	double unshieldedRads = shielding >= 1.0 - 1.0E-12 ? 0.0 : rads * Math.max(0.0, 1.0 - shielding);
	if (unshieldedRads > 0.0) {
	    int amplitude = getAmplitudeFromRadiation(unshieldedRads, strength);
	    int duration = getDurationFromRadiation(unshieldedRads);
	    MobEffectInstance currentEffect = entity.getEffect(VoltaicEffects.RADIATION.get());

	    if (currentEffect != null) {
		duration += currentEffect.getDuration();
		amplitude = Math.max(amplitude, currentEffect.getAmplifier());
	    }

	    entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION.get(), duration, amplitude, false, true));
	}

	recieved += rads;
	recievedStrength = strength;

    }

    @Override
    public RadioactiveObject getRecievedRadiation(LivingEntity entity) {
	return new RadioactiveObject(prevRecievedStrength, prevRecieved);
    }

    @Override
    public void tick(LivingEntity entity) {

	prevRecieved = recieved;
	prevRecievedStrength = recievedStrength;

	recieved = 0;
	recievedStrength = 0;

    }

    public static int getDurationFromRadiation(double radiation) {
	return (int) Math.max(1, radiation / 100.0 * 20.0);
    }

    public static int getAmplitudeFromRadiation(double radiation, double strength) {
	return (int) Math.min(40.0, radiation / 100.0 * strength);
    }

    private static double getArmorShielding(EquipmentSlot slot) {
	return switch (slot) {
	case HEAD -> 5.0 / 24.0;
	case CHEST -> 8.0 / 24.0;
	case LEGS -> 7.0 / 24.0;
	case FEET -> 4.0 / 24.0;
	default -> 0.0;
	};
    }

}
