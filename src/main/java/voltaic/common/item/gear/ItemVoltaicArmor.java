package voltaic.common.item.gear;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemVoltaicArmor extends ArmorItem {

	private final Supplier<CreativeModeTab> creativeTab;

	public ItemVoltaicArmor(ArmorMaterial material, EquipmentSlot type, Properties properties, Supplier<CreativeModeTab> creativeTab) {
		super(material, type, properties);
		this.creativeTab = creativeTab;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (slotId > 35 && slotId < 40 && entity instanceof Player player) {
			onWearingTick(stack, level, player, slotId, isSelected);
		}
	}

	public void onWearingTick(ItemStack stack, Level level, Player player, int slotId, boolean isSelected) {

	}

	@Override
	protected boolean allowedIn(CreativeModeTab category) {
		return creativeTab != null && (category == creativeTab.get() || category == CreativeModeTab.TAB_SEARCH);
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (this.allowedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
