package voltaic.prefab.tile.components.type;

import net.minecraft.network.chat.Component;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

public class ComponentName implements IComponent {
    protected boolean translation;
    protected String name = "";
    private final GenericTile holder;

    public ComponentName(GenericTile holder, String name) {
	this.holder = holder;
	this.name = name;
    }

    public ComponentName translation(boolean value) {
	translation = value;
	return this;
    }

    public Component getName() {
	return translation ? Component.translatable(name) : Component.literal(name);
    }

    @Override
    public IComponentType getType() {
	return IComponentType.Name;
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }
}
