package voltaic.api.electricity.formatting;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class DisplayUnit implements IDisplayUnit {

    private final Component symbol;
    private final Component name;
    private final Component namePlural;
    private final Component distanceFromValue;

    public DisplayUnit(Component name, Component namePlural, Component symbol, Component distanceFromValue) {
        this.name = name;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.distanceFromValue = distanceFromValue;
    }

    public DisplayUnit(Component name, Component namePlural, Component symbol) {
        this(name, namePlural, symbol, new TextComponent(" "));
    }

    @Override
    public Component getSymbol() {
        return symbol;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getNamePlural() {
        return namePlural;
    }

    @Override
    public Component getDistanceFromValue() {
        return distanceFromValue;
    }

}
