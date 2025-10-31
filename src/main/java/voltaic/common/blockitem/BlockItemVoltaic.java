package voltaic.common.blockitem;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import voltaic.api.creativetab.CreativeTabSupplier;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.api.radiation.util.IRadiationRecipient;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import voltaic.common.settings.VoltaicConfig;
import voltaic.registers.VoltaicCapabilities;

public class BlockItemVoltaic extends BlockItem implements CreativeTabSupplier {

	private final Holder<CreativeModeTab> creativeTab;

	public BlockItemVoltaic(Block block, Properties properties, Holder<CreativeModeTab> creativeTab) {
		super(block, properties);
		this.creativeTab = creativeTab;
	}

	@Override
	public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	@Override
	public boolean isAllowedInCreativeTab(CreativeModeTab tab) {
		return creativeTab.value() == tab;
	}

	@Override
	public boolean hasCreativeTab() {
		return creativeTab != null;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		super.onEntityItemUpdate(stack, entity);

		Level world = entity.level();

		if(world.isClientSide || VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse()) {
			return super.onEntityItemUpdate(stack, entity);
		}

		RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());
		if(rad.amount() <= 0) {
			return false;
		}
		double amount = stack.getCount() * rad.amount();
		int range = (int) (Math.sqrt(amount) / (5 * Math.sqrt(2)) * 1.25);
		RadiationSystem.addRadiationSource(world, new SimpleRadiationSource(amount, rad.strength(), range, true, 0, entity.getOnPos().above(), false, false));
		return super.onEntityItemUpdate(stack, entity);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);

		if(VoltaicConfig.INSTANCE.RADIATION_SYSTEM_ENABLED.isFalse()) {
			return;
		}

		RadioactiveObject rad = RadioactiveItemRegister.getValue(stack.getItem());

		if (rad.amount() > 0 && entity instanceof LivingEntity living) {
			IRadiationRecipient cap = living.getCapability(VoltaicCapabilities.CAPABILITY_RADIATIONRECIPIENT);
			if (cap == null) {
				return;
			}
			cap.recieveRadiation(living, stack.getCount() * rad.amount(), rad.strength());
		}
	}

}
