package voltaic.api.tile;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;

public interface IPlayerStorable {

    void setPlayer(LivingEntity player);

    @Nullable
    UUID getPlayerID();

}
