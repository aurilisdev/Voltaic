package voltaic.prefab.screen.component.types.wrapper;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.network.chat.TextComponent;

public class WrapperCyclableFluidGauge {

    private int gauge = 0;
    private List<AbstractScreenComponent> components = new ArrayList<>();

    public WrapperCyclableFluidGauge(int x, int y, GenericContainerBlockEntity<? extends GenericTile> container, GenericScreen<?> screen, boolean inputTanks) {

        int yOffset = 0;

        components.add(screen.addComponent(new ScreenComponentSimpleLabel(x + 4, y + yOffset, 7, Color.WHITE, () -> new TextComponent("" + (gauge + 1)))));

        yOffset += 8;

        components.add(screen.addComponent(new ScreenComponentFluidGauge(() -> {
            GenericTile tile = container.getSafeHost();
            if (tile == null) {
                return null;
            }
            IComponentFluidHandler handler = tile.getComponent(IComponentType.FluidHandler);
            if (handler == null) {
                return null;
            }
            return inputTanks ? handler.getInputTanks()[gauge] : handler.getOutputTanks()[gauge];
        }, x, y + yOffset)));

        yOffset += 50;

        components.add(screen.addComponent(new ButtonTankSlider(ButtonTankSlider.TankSliderPair.LEFT, x, y + yOffset).setOnPress(button -> {
            GenericTile tile = container.getSafeHost();
            if (tile == null) {
                return;
            }
            IComponentFluidHandler handler = tile.getComponent(IComponentType.FluidHandler);
            if (handler == null) {
                return;
            }
            int size = inputTanks ? handler.getInputTanks().length : handler.getOutputTanks().length;

            gauge--;

            if (gauge < 0) {
                gauge = size - 1;
            }
        })));

        components.add(screen.addComponent(new ButtonTankSlider(ButtonTankSlider.TankSliderPair.RIGHT, x + 8, y + yOffset).setOnPress(button -> {
            GenericTile tile = container.getSafeHost();
            if (tile == null) {
                return;
            }
            IComponentFluidHandler handler = tile.getComponent(IComponentType.FluidHandler);
            if (handler == null) {
                return;
            }
            int size = inputTanks ? handler.getInputTanks().length : handler.getOutputTanks().length;

            gauge++;

            if (size <= gauge) {
                gauge = 0;
            }
        })));

    }

    public List<AbstractScreenComponent> getComponents() {
        return components;
    }


}
