package voltaic.registers;

import java.util.ArrayList;
import java.util.List;

import voltaic.Voltaic;
import voltaic.api.creativetab.CreativeTabSupplier;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.gear.ItemGuidebook;
import voltaic.common.item.gear.ItemWrench;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.ModList;

public class VoltaicItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Voltaic.ID);

	public static final RegistryObject<ItemWrench> ITEM_WRENCH = ITEMS.register("wrench", () -> new ItemWrench(new Item.Properties().stacksTo(1), VoltaicCreativeTabs.MAIN));
	public static final RegistryObject<ItemGuidebook> GUIDEBOOK = ITEMS.register("guidebook", () -> new ItemGuidebook(new Item.Properties(), VoltaicCreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_ANTIDOTE = ITEMS.register("antidote", () -> new ItemAntidote(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_IODINETABLET = ITEMS.register("iodinetablet", () -> new ItemIodineTablet(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));

	public static final BulkRegistryObject<ItemUpgrade, SubtypeItemUpgrade> ITEMS_UPGRADE = new BulkRegistryObject<>(SubtypeItemUpgrade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemUpgrade(new Item.Properties(), subtype, VoltaicCreativeTabs.MAIN) {
		@Override
		public boolean hasCreativeTab() {
			if(super.hasCreativeTab()) {
				for(String modId : subtype.modIds) {
					if(ModList.get().isLoaded(modId)) {
						return true;
					}
				}
			}
			return false;
		}
	}));

	@EventBusSubscriber(value = Dist.CLIENT, modid = Voltaic.ID, bus = EventBusSubscriber.Bus.MOD)
	public static class VoltaicCreativeRegistry {

		@SubscribeEvent
		public static void registerItems(BuildCreativeModeTabContentsEvent event) {

			ITEMS.getEntries().forEach(reg -> {

				CreativeTabSupplier supplier = (CreativeTabSupplier) reg.get();

				if (supplier.hasCreativeTab() && supplier.isAllowedInCreativeTab(event.getTab())) {
					List<ItemStack> toAdd = new ArrayList<>();
					supplier.addCreativeModeItems(event.getTab(), toAdd);
					event.acceptAll(toAdd);
				}

			});

		}

	}

}
