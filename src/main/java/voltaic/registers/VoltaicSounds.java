package voltaic.registers;

import voltaic.Voltaic;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VoltaicSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Voltaic.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_BATTERY_SWAP = sound("batteryswap");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRESSURERELEASE = sound("pressurerelease");

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(Voltaic.rl(name), 16.0F));
    }

}
