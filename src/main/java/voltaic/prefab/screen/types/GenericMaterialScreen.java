package voltaic.prefab.screen.types;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import voltaic.prefab.inventory.container.GenericContainer;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import voltaic.prefab.screen.component.utils.AbstractScreenComponent;

/**
 * 
 * This is a simple addon class that allows for a clean integration for fluid and gas lookups with JEI
 * 
 * Note the tile does not need to be a GenericMaterialTile to use this class
 * 
 * @author skip999
 *
 * @param <T>
 */
public class GenericMaterialScreen<T extends GenericContainer> extends GenericScreen<T> {

	private Set<ScreenComponentFluidGauge> fluidGauges = new HashSet<>();

	public GenericMaterialScreen(T container, PlayerInventory inv, ITextComponent titleIn) {
		super(container, inv, titleIn);
	}

	@Override
	public AbstractScreenComponent addComponent(AbstractScreenComponent component) {
		super.addComponent(component);

		if (component instanceof ScreenComponentFluidGauge) {
			fluidGauges.add((ScreenComponentFluidGauge) component);
		} 
		return component;
	}

	public Set<ScreenComponentFluidGauge> getFluidGauges() {
		return fluidGauges;
	}

}
