package voltaic.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent.Key;

public abstract class AbstractKeyPressHandler {

	public abstract void handler(Key event, Minecraft minecraft);

}
