package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;

public class ItemRadioactive extends ItemVoltaic {

    public ItemRadioactive(Item.Properties properties, Supplier<ItemGroup> creativeTab) {
        super(properties, creativeTab);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        World world = entity.level;
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());
        double amount = stack.getCount() * rad.amount();
        int range = (int) (Math.sqrt(amount) / (5 * Math.sqrt(2)) * 1.25);
        RadiationSystem.addRadiationSource(world, new SimpleRadiationSource(amount, rad.strength(), range, true, 0, entity.blockPosition(), false));
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());

        if (entity instanceof LivingEntity) {
        	LivingEntity living = (LivingEntity) entity;
            IRadiationRecipient cap = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
            if (cap == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                return;
            }
            cap.recieveRadiation(living, stack.getCount() * rad.amount(), rad.strength());
        }
    }

}
