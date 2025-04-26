package voltaic.registers;

import voltaic.Voltaic;
import voltaic.api.gas.Gas;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

//@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class VoltaicRegistries {

	/* GAS */
	public static final ResourceLocation GAS_REGISTRY_LOC = new ResourceLocation(Voltaic.ID, "gases");
	public static final ResourceKey<Registry<Gas>> GAS_REGISTRY_KEY = ResourceKey.createRegistryKey(GAS_REGISTRY_LOC);
	private static Supplier<IForgeRegistry<Gas>> GAS_REGISTRY_SUPPLIER;

	public static void init() {
		GAS_REGISTRY_SUPPLIER = VoltaicGases.GASES.makeRegistry(() -> new RegistryBuilder<Gas>().setName(GAS_REGISTRY_LOC).hasTags());
	}

	public static IForgeRegistry<Gas> gasRegistry() {
		return GAS_REGISTRY_SUPPLIER.get();
	}

}
