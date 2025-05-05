package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.inventory.container.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VoltaicMenuTypes {
	public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Voltaic.ID);

	public static final RegistryObject<ContainerType<ContainerGuidebook>> CONTAINER_GUIDEBOOK = register("guidebook", ContainerGuidebook::new);
	public static final RegistryObject<ContainerType<ContainerO2OProcessor>> CONTAINER_O2OPROCESSOR = register("o2oprocessor", ContainerO2OProcessor::new);
	public static final RegistryObject<ContainerType<ContainerO2OProcessorDouble>> CONTAINER_O2OPROCESSORDOUBLE = register("o2oprocessordouble", ContainerO2OProcessorDouble::new);
	public static final RegistryObject<ContainerType<ContainerO2OProcessorTriple>> CONTAINER_O2OPROCESSORTRIPLE = register("o2oprocessortriple", ContainerO2OProcessorTriple::new);
	public static final RegistryObject<ContainerType<ContainerDO2OProcessor>> CONTAINER_DO2OPROCESSOR = register("do2oprocessor", ContainerDO2OProcessor::new);

	private static <T extends Container> RegistryObject<ContainerType<T>> register(String id, ContainerType.IFactory<T> supplier) {
		return MENU_TYPES.register(id, () -> new ContainerType<>(supplier));
	}
}
