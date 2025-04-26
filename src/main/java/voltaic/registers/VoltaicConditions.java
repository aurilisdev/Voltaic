package voltaic.registers;

import com.mojang.serialization.MapCodec;

import voltaic.Voltaic;
import voltaic.common.condition.ConfigCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class VoltaicConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, Voltaic.ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigCondition>> GUIDEBOOK_DISPENSE_CONDITION = CONDITIONS.register("guidebookconfig", () -> ConfigCondition.CODEC);

}