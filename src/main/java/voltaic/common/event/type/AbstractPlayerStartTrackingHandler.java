package voltaic.common.event.type;

import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class AbstractPlayerStartTrackingHandler {

	public abstract void handle(PlayerEvent.StartTracking event);

}
