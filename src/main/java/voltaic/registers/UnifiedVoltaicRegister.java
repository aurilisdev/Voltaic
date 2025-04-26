package voltaic.registers;

import net.neoforged.bus.api.IEventBus;

public class UnifiedVoltaicRegister {

	public static void register(IEventBus bus) {
		VoltaicBlocks.BLOCKS.register(bus);
		VoltaicTiles.BLOCK_ENTITY_TYPES.register(bus);
		VoltaicItems.ITEMS.register(bus);
		VoltaicMenuTypes.MENU_TYPES.register(bus);
		VoltaicGases.GASES.register(bus);
		VoltaicParticles.PARTICLES.register(bus);
		VoltaicCreativeTabs.CREATIVE_TABS.register(bus);
		VoltaicIngredients.INGREDIENT_TYPES.register(bus);
		VoltaicSounds.SOUNDS.register(bus);
		VoltaicEffects.EFFECTS.register(bus);
		VoltaicDataComponentTypes.DATA_COMPONENT_TYPES.register(bus);
		VoltaicAttachmentTypes.ATTACHMENT_TYPES.register(bus);
		VoltaicConditions.CONDITIONS.register(bus);
	}

}
