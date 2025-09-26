package voltaic.common.blockitem;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.common.settings.VoltaicConstants;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;

public class BlockItemVoltaic extends BlockItem {

	private final Supplier<ItemGroup> creativeTab;

	public BlockItemVoltaic(Block block, Properties properties, Supplier<ItemGroup> creativeTab) {
		super(block, properties);
		this.creativeTab = creativeTab;
	}
	
	@Override
	protected boolean allowdedIn(ItemGroup category) {
		return creativeTab != null && (category == creativeTab.get() || category == ItemGroup.TAB_SEARCH);
	}
	
	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if(allowdedIn(group)) {
			this.getBlock().fillItemCategory(group, items);
		}
	}
	
	@Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		
		super.onEntityItemUpdate(stack, entity);
		
        World world = entity.level;
        
        if(world.isClientSide || !VoltaicConstants.RADIATION_SYSTEM_ENABLED) {
        	return super.onEntityItemUpdate(stack, entity);
        }
        
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());
        double amount = stack.getCount() * rad.amount();
        int range = (int) (Math.sqrt(amount) / (5 * Math.sqrt(2)) * 1.25);
        RadiationSystem.addRadiationSource(world, new SimpleRadiationSource(amount, rad.strength(), range, true, 0, entity.blockPosition().above(), false, false));
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        
        if(world.isClientSide || !VoltaicConstants.RADIATION_SYSTEM_ENABLED) {
        	return;
        }
        
        RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());

        if (entity instanceof LivingEntity && !world.isClientSide) {
            IRadiationRecipient cap = entity.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT).orElse(CapabilityUtils.EMPTY_RADIATION_REPIPIENT);
            if (cap == CapabilityUtils.EMPTY_RADIATION_REPIPIENT) {
                return;
            }
            cap.recieveRadiation((LivingEntity) entity, stack.getCount() * rad.amount(), rad.strength());
        }
    }

}
