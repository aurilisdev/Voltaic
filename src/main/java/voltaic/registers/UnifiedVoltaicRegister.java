package voltaic.registers;

import net.minecraftforge.eventbus.api.IEventBus;

public class UnifiedVoltaicRegister {

	public static void register(IEventBus bus) {
		VoltaicRegistries.init();
		VoltaicBlocks.BLOCKS.register(bus);
		VoltaicTiles.BLOCK_ENTITY_TYPES.register(bus);
		VoltaicItems.ITEMS.register(bus);
		VoltaicMenuTypes.MENU_TYPES.register(bus);
		VoltaicGases.GASES.register(bus);
		VoltaicParticles.PARTICLES.register(bus);
		VoltaicCreativeTabs.CREATIVE_TABS.register(bus);
		VoltaicSounds.SOUNDS.register(bus);
		VoltaicEffects.EFFECTS.register(bus);
	}

}
