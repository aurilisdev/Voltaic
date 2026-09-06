package voltaic.prefab.screen.component.types.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.button.type.ButtonTankSlider;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import voltaic.prefab.screen.component.utils.AbstractScreenComponent;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.utilities.math.Color;

public class WrapperCyclableFluidGauge {

    private int gauge = 0;
    private final List<AbstractScreenComponent> components = new ArrayList<>();

    public WrapperCyclableFluidGauge(int x, int y, GenericContainerBlockEntity<? extends GenericTile> container,
	    GenericScreen<?> screen, boolean inputTanks) {
	Supplier<Optional<IComponentFluidHandler>> handlerSupplier = () -> container.getSafeHost()
		.flatMap(tile -> tile.<IComponentFluidHandler>getComponent(IComponentType.FluidHandler));
	IntConsumer cycleGauge = change -> handlerSupplier.get().ifPresent(handler -> {
	    int size = inputTanks ? handler.getInputTanks().length : handler.getOutputTanks().length;
	    gauge = size == 0 ? 0 : Math.floorMod(gauge + change, size);
	});
	int yOffset = 0;
	components.add(screen.addComponent(new ScreenComponentSimpleLabel(x + 4, y + yOffset, 7, Color.WHITE,
		() -> Component.literal(Integer.toString(gauge + 1)))));
	yOffset += 8;
	components.add(screen.addComponent(new ScreenComponentFluidGauge(
		() -> handlerSupplier.get()
			.map(handler -> inputTanks ? handler.getInputTanks() : handler.getOutputTanks())
			.filter(tanks -> gauge >= 0 && gauge < tanks.length).map(tanks -> tanks[gauge]).orElse(null),
		x, y + yOffset)));
	yOffset += 50;
	components.add(screen.addComponent(new ButtonTankSlider(ButtonTankSlider.TankSliderPair.LEFT, x, y + yOffset)
		.setOnPress(button -> cycleGauge.accept(-1))));
	components
		.add(screen.addComponent(new ButtonTankSlider(ButtonTankSlider.TankSliderPair.RIGHT, x + 8, y + yOffset)
			.setOnPress(button -> cycleGauge.accept(1))));
    }

    public List<AbstractScreenComponent> getComponents() {
	return components;
    }

}
