package voltaic.registers;

import voltaic.Voltaic;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Voltaic.ID);

	public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder().title(VoltaicTextUtils.creativeTab("main")).icon(() -> new ItemStack(VoltaicItems.ITEM_WRENCH.get())).build());

}
