package voltaic.datagen.utils.server.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator;

public abstract class BaseAdvancementProvider implements AdvancementGenerator {

    public final String modID;

    public BaseAdvancementProvider(String modID) {
        this.modID = modID;
    }
    
    public AdvancementBuilder advancement(String name) {
		return AdvancementBuilder.create(new ResourceLocation(modID, name));
	}

}
