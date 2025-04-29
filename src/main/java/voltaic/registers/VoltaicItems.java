package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.item.gear.ItemGuidebook;
import voltaic.common.item.gear.ItemWrench;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Voltaic.ID);

	public static final RegistryObject<ItemWrench> ITEM_WRENCH = ITEMS.register("wrench", () -> new ItemWrench(new Item.Properties().stacksTo(1), () -> VoltaicCreativeTabs.MAIN));
	public static final RegistryObject<ItemGuidebook> GUIDEBOOK = ITEMS.register("guidebook", () -> new ItemGuidebook(new Item.Properties(), () -> VoltaicCreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_ANTIDOTE = ITEMS.register("antidote", () -> new ItemAntidote(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));
	//public static final RegistryObject<Item> ITEM_IODINETABLET = ITEMS.register("iodinetablet", () -> new ItemIodineTablet(new Item.Properties(), VoltaicAPICreativeTabs.MAIN));

	//public static final BulkRegistryObject<ItemUpgrade, SubtypeItemUpgrade> ITEMS_UPGRADE = new BulkDeferredHolder<>(SubtypeItemUpgrade.values(), subtype -> ITEMS.register(subtype.tag(), () -> new ItemUpgrade(new Item.Properties(), subtype)));

}
