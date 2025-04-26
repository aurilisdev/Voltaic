package voltaic.common.event.type;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public abstract class AbstractPlayerStartTrackingHandler {

	public abstract void handle(PlayerEvent.StartTracking event);

}
