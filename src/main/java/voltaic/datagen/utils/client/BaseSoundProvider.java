package voltaic.datagen.utils.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinition.Sound;
import net.minecraftforge.common.data.SoundDefinition.SoundType;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.fml.RegistryObject;

public abstract class BaseSoundProvider extends SoundDefinitionsProvider {

	private final String modID;

	public BaseSoundProvider(DataGenerator gen, ExistingFileHelper exFileHelper, String modID) {
		super(gen, modID, exFileHelper);
		this.modID = modID;
	}

	public void add(RegistryObject<SoundEvent> sound) {
		add(sound.get(), SoundDefinition.definition().subtitle("subtitles." + modID + "." + sound.getId().getPath()).with(Sound.sound(sound.getId(), SoundType.SOUND)));
	}

}
