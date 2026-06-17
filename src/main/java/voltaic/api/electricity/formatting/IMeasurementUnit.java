package voltaic.api.electricity.formatting;

import net.minecraft.network.chat.Component;

public interface IMeasurementUnit {

    default double process(double val) {
	return val / getValue();
    }

    default Component getName(boolean isSymbol) {
	if (isSymbol) {
	    return getSymbol();
	}

	return getName();
    }

    public double getValue();

    public Component getSymbol();

    public Component getName();

}
