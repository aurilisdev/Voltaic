package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.inventory.container.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Voltaic.ID);

	public static final RegistryObject<MenuType<ContainerGuidebook>> CONTAINER_GUIDEBOOK = register("guidebook", ContainerGuidebook::new);
	public static final RegistryObject<MenuType<ContainerO2OProcessor>> CONTAINER_O2OPROCESSOR = register("o2oprocessor", ContainerO2OProcessor::new);
	public static final RegistryObject<MenuType<ContainerO2OProcessorDouble>> CONTAINER_O2OPROCESSORDOUBLE = register("o2oprocessordouble", ContainerO2OProcessorDouble::new);
	public static final RegistryObject<MenuType<ContainerO2OProcessorTriple>> CONTAINER_O2OPROCESSORTRIPLE = register("o2oprocessortriple", ContainerO2OProcessorTriple::new);
	public static final RegistryObject<MenuType<ContainerDO2OProcessor>> CONTAINER_DO2OPROCESSOR = register("do2oprocessor", ContainerDO2OProcessor::new);

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS));
	}
}
