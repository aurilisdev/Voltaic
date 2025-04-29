package voltaic.common.packet.types.client;

import voltaic.api.radiation.util.RadiationShielding;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

/**
 * Apparently with packets, certain class calls cannot be called within the packet itself because Java
 * 
 * SoundInstance for example is an exclusively client class only
 * 
 * Place methods that need to use those here
 */
public class ClientBarrierMethods {

    public static void handlerSetGuidebookInitFlag() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;
        if (world == null || minecraft.player == null) {
            return;
        }
        ScreenGuidebook.setInitNotHappened();
    }

    public static void handlerSpawnSmokeParicle(BlockPos pos) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
    }

    public static void handleSetClientRadioactiveItems(HashMap<Item, RadioactiveObject> items) {
        RadioactiveItemRegister.INSTANCE.setClientValues(items);
    }

    public static void handleSetClientRadioactiveFluids(HashMap<Fluid, RadioactiveObject> fluids) {
        RadioactiveFluidRegister.INSTANCE.setClientValues(fluids);
    }

    public static void handleSetClientRadiationShielding(HashMap<Block, RadiationShielding> shielding) {
        RadiationShieldingRegister.INSTANCE.setClientValues(shielding);
    }

}
