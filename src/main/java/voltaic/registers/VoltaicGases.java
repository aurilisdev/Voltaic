package voltaic.registers;

import java.util.concurrent.ConcurrentHashMap;

import voltaic.Voltaic;
import voltaic.api.gas.Gas;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicGases {

    public static final ConcurrentHashMap<Fluid, Gas> MAPPED_GASSES = new ConcurrentHashMap<>();

    public static final DeferredRegister<Gas> GASES = DeferredRegister.create(VoltaicRegistries.GAS_REGISTRY_KEY, Voltaic.ID);

    public static final RegistryObject<Gas> EMPTY = GASES.register("empty", () -> new Gas(() -> Items.AIR, VoltaicTextUtils.gas("empty")));

}
