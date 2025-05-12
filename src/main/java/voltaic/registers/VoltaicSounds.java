package voltaic.registers;

import voltaic.Voltaic;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VoltaicSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Voltaic.ID);

    public static final RegistryObject<SoundEvent> SOUND_BATTERY_SWAP = sound("batteryswap");
    public static final RegistryObject<SoundEvent> SOUND_PRESSURERELEASE = sound("pressurerelease");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(Voltaic.rl(name)));
    }

}
