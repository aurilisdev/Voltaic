package voltaic.common.item.gear;

import java.util.List;

import voltaic.api.creativetab.CreativeTabSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public class ItemVoltaicArmor extends ArmorItem implements CreativeTabSupplier {

	private final RegistryObject<CreativeModeTab> creativeTab;

	public ItemVoltaicArmor(ArmorMaterial material, Type type, Properties properties, RegistryObject<CreativeModeTab> creativeTab) {
		super(material, type, properties);
		this.creativeTab = creativeTab;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if(slotId > 35 && slotId < 40 && entity instanceof Player player){
			onWearingTick(stack, level, player, slotId, isSelected);
		}
	}

	public void onWearingTick(ItemStack stack, Level level, Player player, int slotId, boolean isSelected) {

	}

	@Override
	public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	@Override
	public boolean isAllowedInCreativeTab(CreativeModeTab tab) {
		return creativeTab.get() == tab;
	}

	@Override
	public boolean hasCreativeTab() {
		return creativeTab != null;
	}

}
