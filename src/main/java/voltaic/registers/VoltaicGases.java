package voltaic.registers;

import java.util.concurrent.ConcurrentHashMap;

import voltaic.Voltaic;
import voltaic.api.gas.Gas;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicGases {

    public static final ConcurrentHashMap<Holder<Fluid>, Gas> MAPPED_GASSES = new ConcurrentHashMap<>();

    public static final ResourceLocation GAS_REGISTRY_LOC = Voltaic.rl("gases");
    public static final ResourceKey<Registry<Gas>> GAS_REGISTRY_KEY = ResourceKey.createRegistryKey(GAS_REGISTRY_LOC);
    public static final DeferredRegister<Gas> GASES = DeferredRegister.create(GAS_REGISTRY_KEY, Voltaic.ID);
    public static final Registry<Gas> GAS_REGISTRY = VoltaicGases.GASES.makeRegistry(builder -> builder.sync(true));

    public static final DeferredHolder<Gas, Gas> EMPTY = GASES.register("empty", () -> new Gas(new Holder.Direct<>(Items.AIR), VoltaicTextUtils.gas("empty")));

}
