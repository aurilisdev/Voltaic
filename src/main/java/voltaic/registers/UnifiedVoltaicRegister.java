package voltaic.registers;

import net.minecraftforge.eventbus.api.IEventBus;

public class UnifiedVoltaicRegister {

	public static void register(IEventBus bus) {
		VoltaicBlocks.BLOCKS.register(bus);
		VoltaicTiles.BLOCK_ENTITY_TYPES.register(bus);
		VoltaicItems.ITEMS.register(bus);
		VoltaicMenuTypes.MENU_TYPES.register(bus);
		VoltaicParticles.PARTICLES.register(bus);
		VoltaicSounds.SOUNDS.register(bus);
	}

}
