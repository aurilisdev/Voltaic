package voltaic.api.electricity.formatting;

import net.minecraft.util.text.IFormattableTextComponent;

public class MeasurementUnit implements IMeasurementUnit {

    private final double value;
    private final IFormattableTextComponent symbol;
    private final IFormattableTextComponent name;

    public MeasurementUnit(IFormattableTextComponent name, IFormattableTextComponent symbol, double value) {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public IFormattableTextComponent getSymbol() {
        return symbol;
    }

    @Override
    public IFormattableTextComponent getName() {
        return name;
    }
}
