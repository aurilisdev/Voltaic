package voltaic.common.event.type;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public abstract class AbstractIncomingDamageHandler {

    public abstract void handle(LivingIncomingDamageEvent event);
}
