package voltaic.registers;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.fml.ModList;
import voltaic.Voltaic;
import voltaic.api.creativetab.CreativeTabSupplier;
import voltaic.api.registration.BulkDeferredHolder;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.gear.ItemGuidebook;
import voltaic.common.item.gear.ItemWrench;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voltaic.common.item.subtype.SubtypeItemUpgrade;

public class VoltaicItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Voltaic.ID);

	public static final DeferredHolder<Item, ItemWrench> ITEM_WRENCH = ITEMS.register("wrench", () -> new ItemWrench(new Item.Properties().stacksTo(1), VoltaicCreativeTabs.MAIN));
	public static final DeferredHolder<Item, ItemGuidebook> GUIDEBOOK = ITEMS.register("guidebook", () -> new ItemGuidebook(new Item.Properties(), VoltaicCreativeTabs.MAIN));
	//public static final DeferredHolder<Item, Item> ITEM_ANTIDOTE = ITEMS.register("antidote", () -> new ItemAntidote(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));
	//public static final DeferredHolder<Item, Item> ITEM_IODINETABLET = ITEMS.register("iodinetablet", () -> new ItemIodineTablet(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));

	public static final BulkDeferredHolder<Item, ItemUpgrade, SubtypeItemUpgrade> ITEMS_UPGRADE = new BulkDeferredHolder<>(SubtypeItemUpgrade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemUpgrade(new Item.Properties(), subtype, VoltaicCreativeTabs.MAIN) {

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
	private static class VoltaicCreativeRegistry {

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
