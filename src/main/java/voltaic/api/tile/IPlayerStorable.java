package voltaic.api.tile;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;

public interface IPlayerStorable {

	void setPlayer(LivingEntity player);

	UUID getPlayerID();

}
