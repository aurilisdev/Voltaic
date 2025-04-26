package voltaic.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import voltaic.common.inventory.container.ContainerO2OProcessor;
import voltaic.common.inventory.container.ContainerO2OProcessorTriple;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentProgress;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentProcessor;

@OnlyIn(Dist.CLIENT)
public class ScreenO2OProcessorTriple extends GenericScreen<ContainerO2OProcessorTriple> {
	public ScreenO2OProcessorTriple(ContainerO2OProcessorTriple container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentProgress(ScreenComponentProgress.ProgressBars.PROGRESS_ARROW_RIGHT, () -> {
			GenericTile furnace = container.getSafeHost();
			if (furnace != null) {
				ComponentProcessor processor = furnace.getComponent(IComponentType.Processor);
				if (processor.isActive(0)) {
					return processor.operatingTicks.getValue()[0] / processor.requiredTicks.getValue()[0];
				}
			}
			return 0;
		}, 84 - ContainerO2OProcessor.startXOffset, 24));
		addComponent(new ScreenComponentProgress(ScreenComponentProgress.ProgressBars.PROGRESS_ARROW_RIGHT, () -> {
			GenericTile furnace = container.getSafeHost();
			if (furnace != null) {
				ComponentProcessor processor = furnace.getComponent(IComponentType.Processor);
				if (processor.isActive(1)) {
					return processor.operatingTicks.getValue()[1] / processor.requiredTicks.getValue()[1];
				}
			}
			return 0;
		}, 84 - ContainerO2OProcessor.startXOffset, 44));
		addComponent(new ScreenComponentProgress(ScreenComponentProgress.ProgressBars.PROGRESS_ARROW_RIGHT, () -> {
			GenericTile furnace = container.getSafeHost();
			if (furnace != null) {
				ComponentProcessor processor = furnace.getComponent(IComponentType.Processor);
				if (processor.isActive(2)) {
					return processor.operatingTicks.getValue()[2] / processor.requiredTicks.getValue()[2];
				}
			}
			return 0;
		}, 84 - ContainerO2OProcessor.startXOffset, 66));
		imageHeight += 20;
		inventoryLabelY += 20;
		addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2));

		new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2, 75, 82 + 20, 8, 72 + 20);
	}
}