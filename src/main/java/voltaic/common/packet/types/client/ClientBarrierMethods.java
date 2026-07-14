package voltaic.common.packet.types.client;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.api.radiation.util.RadiationShielding;
import voltaic.api.radiation.util.RadioactiveObject;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.common.reloadlistener.RadiationShieldingRegister;
import voltaic.common.reloadlistener.RadioactiveBlockRegister;
import voltaic.common.reloadlistener.RadioactiveFluidRegister;
import voltaic.common.reloadlistener.RadioactiveItemRegister;

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
		ClientWorld world = minecraft.level;
		if (world == null || minecraft.player == null) {
			return;
		}
		ScreenGuidebook.setInitNotHappened();
	}

	public static void handlerSpawnSmokeParicle(BlockPos pos) {
		ClientWorld world = Minecraft.getInstance().level;
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

	public static void handleUpdateCarriedItemClient(ItemStack carriedItem, BlockPos tilePos, UUID playerId) {
		PlayerEntity player = Minecraft.getInstance().player;
		World level = Minecraft.getInstance().level;

		if (player == null || !player.getUUID().equals(playerId)) {
			return;
		}

		player.inventory.setCarried(carriedItem);

	}

	public static void handleSetClientRadioactiveBlocks(HashMap<Block, RadioactiveObject> blocks) {
		RadioactiveBlockRegister.INSTANCE.setClientValues(blocks);
	}

}
