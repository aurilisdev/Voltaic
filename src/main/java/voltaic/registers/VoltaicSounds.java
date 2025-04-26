package voltaic.registers;

import voltaic.Voltaic;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VoltaicSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Voltaic.ID);

    public static final RegistryObject<SoundEvent> SOUND_BATTERY_SWAP = sound("batteryswap");
    public static final RegistryObject<SoundEvent> SOUND_PRESSURERELEASE = sound("pressurerelease");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(Voltaic.rl(name), 16.0F));
    }

}
