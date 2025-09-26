package voltaic.common.item;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.common.settings.VoltaicConstants;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;

public class ItemVoltaic extends Item {

	private final Supplier<CreativeModeTab> creativeTab;

	public ItemVoltaic(Properties properties, Supplier<CreativeModeTab> creativeTab) {
		super(properties);
		this.creativeTab = creativeTab;
	}

	@Override
	protected boolean allowdedIn(CreativeModeTab category) {
		return creativeTab != null && (category == creativeTab.get() || category == CreativeModeTab.TAB_SEARCH);
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (this.allowdedIn(category)) {
			items.add(new ItemStack(this));
		}
	}
	
	@Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		
		super.onEntityItemUpdate(stack, entity);
		
        Level world = entity.level;
        
        if(world.isClientSide || !VoltaicConstants.RADIATION_SYSTEM_ENABLED) {
        	return super.onEntityItemUpdate(stack, entity);
        }
        
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());
        double amount = stack.getCount() * rad.amount();
        int range = (int) (Math.sqrt(amount) / (5 * Math.sqrt(2)) * 1.25);
        RadiationSystem.addRadiationSource(world, new SimpleRadiationSource(amount, rad.strength(), range, true, 0, entity.getOnPos().above(), false, false));
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        
        if(world.isClientSide || !VoltaicConstants.RADIATION_SYSTEM_ENABLED) {
        	return;
        }
        
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());

        if (entity instanceof LivingEntity living && !world.isClientSide) {
            IRadiationRecipient cap = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
            if (cap == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                return;
            }
            cap.recieveRadiation(living, stack.getCount() * rad.amount(), rad.strength());
        }
    }

}
