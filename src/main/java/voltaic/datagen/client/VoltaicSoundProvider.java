package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.datagen.utils.client.BaseSoundProvider;
import voltaic.registers.VoltaicSounds;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VoltaicSoundProvider extends BaseSoundProvider {
    public VoltaicSoundProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, helper, Voltaic.ID);
    }

    @Override
    public void registerSounds() {
        add(VoltaicSounds.SOUND_BATTERY_SWAP);
        add(VoltaicSounds.SOUND_PRESSURERELEASE);
    }
}
