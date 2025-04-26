package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.datagen.utils.client.BaseSoundProvider;
import voltaic.registers.VoltaicSounds;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VoltaicSoundProvider extends BaseSoundProvider {
    public VoltaicSoundProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, helper, Voltaic.ID);
    }

    @Override
    public void registerSounds() {
        add(VoltaicSounds.SOUND_BATTERY_SWAP);
        add(VoltaicSounds.SOUND_PRESSURERELEASE);
    }
}
