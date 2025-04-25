package voltaic.api.radiation;

import voltaic.Voltaic;
import voltaic.api.radiation.util.IHazmatSuit;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.settings.VoltaicConstants;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicEffects;

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

public class CapabilityRadiationRecipient implements IRadiationRecipient, ICapabilitySerializable<CompoundTag> {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    
    private final LazyOptional<IRadiationRecipient> lazyOptional = LazyOptional.of(() -> this);
    
    private double recieved = 0;
    private double recievedStrength = 0;
    
    private double prevRecieved = 0;
    private double prevRecievedStrength = 0;
    
    public CapabilityRadiationRecipient() {
    	
    }
    
    @Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == null || cap != VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT) {
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
		if(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT == null) {
			return;
		}
		recieved = nbt.getDouble("recieved");
		recievedStrength = nbt.getDouble("recievedstrength");
		prevRecieved = nbt.getDouble("prevrecieved");
		prevRecievedStrength = nbt.getDouble("prevrecievedstrength");
	}

    @Override
    public void recieveRadiation(LivingEntity entity, double rads, double strength) {

        if (rads <= 0) {
            return;
        }

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            recieved += rads;
            recievedStrength += strength;
            return;
        }

        if(entity.hasEffect(VoltaicEffects.RADIATION_RESISTANCE.get())) {
            if(rads <= VoltaicConstants.IODINE_RESISTANCE_THRESHHOLD) {
            	recieved += rads;
                recievedStrength += strength;
                return;
            } else {
                rads = rads * VoltaicConstants.IODINE_RAD_REDUCTION;
            }
        }

        int count = 0;

        for (EquipmentSlot slot : ARMOR_SLOTS) {

            ItemStack stack = entity.getItemBySlot(slot);

            if (stack.getItem() instanceof IHazmatSuit) {

                //TODO implement damage reduction based on radiation amount and strength

                count++;

                float damage = (float) (rads * 2.15f) / 2169.9975f;

                if (Voltaic.RANDOM.nextFloat() >= damage) {
                    continue;
                }

                stack.hurtAndBreak((int) Math.ceil(damage), entity, item -> {});

            }

        }

        // Not Full Set
        if (count < 4) {

            int amplitude = getAmplitudeFromRadiation(rads, strength);
            int time = getDurationFromRadiation(rads);

            if (entity.hasEffect(VoltaicEffects.RADIATION.get())) {

                MobEffectInstance instance = entity.getEffect(VoltaicEffects.RADIATION.get());

                if (instance.getAmplifier() > amplitude) {
                    entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION.get(), time + instance.getDuration(), instance.getAmplifier(), false, true));
                } else {
                    entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION.get(), time + instance.getDuration(), amplitude, false, true));
                }

            } else {
                entity.addEffect(new MobEffectInstance(VoltaicEffects.RADIATION.get(), time, amplitude, false, true));
            }
        }

        recieved += rads;
        recievedStrength += strength;

    }

    @Override
    public RadioactiveObject getRecievedRadiation(LivingEntity entity) {
        return new RadioactiveObject(recieved, recievedStrength);
    }

    @Override
    public void tick(LivingEntity entity) {

        prevRecieved = recieved;
        prevRecievedStrength = recievedStrength;

        recieved = 0;
        recievedStrength = 0;

    }

    public static int getDurationFromRadiation(double radiation) {
        return (int) Math.max(20.0, radiation / 100.0 * 20.0);
    }

    public static int getAmplitudeFromRadiation(double radiation, double strength) {
        return (int) Math.min(40.0, radiation / 100.0 * strength);
    }

}
