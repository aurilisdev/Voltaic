package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.inventory.container.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Voltaic.ID);

	public static final DeferredHolder<MenuType<?>,MenuType<ContainerGuidebook>> CONTAINER_GUIDEBOOK = register("guidebook", ContainerGuidebook::new);
	public static final DeferredHolder<MenuType<?>,MenuType<ContainerO2OProcessor>> CONTAINER_O2OPROCESSOR = register("o2oprocessor", ContainerO2OProcessor::new);
	public static final DeferredHolder<MenuType<?>,MenuType<ContainerO2OProcessorDouble>> CONTAINER_O2OPROCESSORDOUBLE = register("o2oprocessordouble", ContainerO2OProcessorDouble::new);
	public static final DeferredHolder<MenuType<?>,MenuType<ContainerO2OProcessorTriple>> CONTAINER_O2OPROCESSORTRIPLE = register("o2oprocessortriple", ContainerO2OProcessorTriple::new);
	public static final DeferredHolder<MenuType<?>,MenuType<ContainerDO2OProcessor>> CONTAINER_DO2OPROCESSOR = register("do2oprocessor", ContainerDO2OProcessor::new);

	private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>,MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS));
	}
}
