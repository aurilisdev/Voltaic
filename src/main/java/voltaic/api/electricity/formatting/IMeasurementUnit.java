package voltaic.api.electricity.formatting;

import net.minecraft.util.text.IFormattableTextComponent;

public interface IMeasurementUnit {

	default double process(double val) {
		return val / getValue();
	}

	default IFormattableTextComponent getName(boolean isSymbol) {
		if (isSymbol) {
			return getSymbol();
		}

		return getName();
	}

	public double getValue();

	public IFormattableTextComponent getSymbol();

	public IFormattableTextComponent getName();

}
