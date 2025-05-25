package voltaic.registers;

import voltaic.Voltaic;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.gear.ItemGuidebook;
import voltaic.common.item.gear.ItemWrench;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.ModList;

public class VoltaicItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Voltaic.ID);

	public static final RegistryObject<ItemWrench> ITEM_WRENCH = ITEMS.register("wrench", () -> new ItemWrench(new Item.Properties().stacksTo(1), () -> VoltaicCreativeTabs.MAIN));
	public static final RegistryObject<ItemGuidebook> GUIDEBOOK = ITEMS.register("guidebook", () -> new ItemGuidebook(new Item.Properties(), () -> VoltaicCreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_ANTIDOTE = ITEMS.register("antidote", () -> new ItemAntidote(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_IODINETABLET = ITEMS.register("iodinetablet", () -> new ItemIodineTablet(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));

	public static final BulkRegistryObject<ItemUpgrade, SubtypeItemUpgrade> ITEMS_UPGRADE = new BulkRegistryObject<>(SubtypeItemUpgrade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemUpgrade(new Item.Properties(), subtype, () -> VoltaicCreativeTabs.MAIN) {
		@Override
		public boolean allowdedIn(ItemGroup tab) {
			if(super.allowdedIn(tab)) {
				for(String modId : subtype.modIds) {
					if(ModList.get().isLoaded(modId)) {
						return true;
					}
				}
			}
			return false;
		}
	}));

}
