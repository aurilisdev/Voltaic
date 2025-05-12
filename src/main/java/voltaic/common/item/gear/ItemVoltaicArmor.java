package voltaic.common.item.gear;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemVoltaicArmor extends ArmorItem {

	private final Supplier<ItemGroup> creativeTab;

	public ItemVoltaicArmor(IArmorMaterial material, EquipmentSlotType type, Properties properties, Supplier<ItemGroup> creativeTab) {
		super(material, type, properties);
		this.creativeTab = creativeTab;
	}

	@Override
	public void inventoryTick(ItemStack stack, World level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (slotId > 35 && slotId < 40 && entity instanceof PlayerEntity) {
			onWearingTick(stack, level, (PlayerEntity) entity, slotId, isSelected);
		}
	}

	public void onWearingTick(ItemStack stack, World level, PlayerEntity player, int slotId, boolean isSelected) {

	}

	@Override
	protected boolean allowdedIn(ItemGroup category) {
		return creativeTab != null && (category == creativeTab.get() || category == ItemGroup.TAB_SEARCH);
	}

	@Override
	public void fillItemCategory(ItemGroup category, NonNullList<ItemStack> items) {
		if (this.allowdedIn(category)) {
			items.add(new ItemStack(this));
		}
	}

}
