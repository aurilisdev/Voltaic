package voltaic.common.event.type;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public abstract class AbstractLivingDamageHandler {

	public abstract void handle(LivingDamageEvent.Pre event);

}
