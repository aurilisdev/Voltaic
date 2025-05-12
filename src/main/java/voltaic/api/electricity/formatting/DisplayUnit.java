package voltaic.api.electricity.formatting;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DisplayUnit implements IDisplayUnit {

    private final IFormattableTextComponent symbol;
    private final IFormattableTextComponent name;
    private final IFormattableTextComponent namePlural;
    private final IFormattableTextComponent distanceFromValue;

    public DisplayUnit(IFormattableTextComponent name, IFormattableTextComponent namePlural, IFormattableTextComponent symbol, IFormattableTextComponent distanceFromValue) {
        this.name = name;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.distanceFromValue = distanceFromValue;
    }

    public DisplayUnit(IFormattableTextComponent name, IFormattableTextComponent namePlural, IFormattableTextComponent symbol) {
        this(name, namePlural, symbol, new StringTextComponent(" "));
    }

    @Override
    public IFormattableTextComponent getSymbol() {
        return symbol;
    }

    @Override
    public IFormattableTextComponent getName() {
        return name;
    }

    @Override
    public IFormattableTextComponent getNamePlural() {
        return namePlural;
    }

    @Override
    public IFormattableTextComponent getDistanceFromValue() {
        return distanceFromValue;
    }

}
