package voltaic.compatibility.jei.screenhandlers;

import javax.annotation.Nullable;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import voltaic.prefab.screen.component.types.gauges.ScreenComponentFluidGauge;
import voltaic.prefab.screen.types.GenericMaterialScreen;

public class ScreenHandlerMaterialScreen implements IGuiContainerHandler<GenericMaterialScreen<?>> {

	@Override
	public @Nullable Object getIngredientUnderMouse(GenericMaterialScreen<?> screen, double mouseX, double mouseY) {

		double xAxis = mouseX - screen.getGuiWidth();
		double yAxis = mouseY - screen.getGuiHeight();

		for (ScreenComponentFluidGauge gauge : screen.getFluidGauges()) {

			if (gauge.isMouseOver(xAxis, yAxis)) {
				IFluidTank tank = gauge.fluidInfoHandler.getTank();
				if (tank == null) {
					continue;
				}
				FluidStack stack = tank.getFluid();

				if (stack.isEmpty()) {
					continue;
				}
				return stack;
			}
		}

		return null;
	}

}
