package voltaic.prefab.tile.components.type;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

public class ComponentName implements IComponent {
	protected boolean translation;
	protected String name = "";

	public ComponentName(String name) {
		this.name = name;
	}

	public ComponentName translation(boolean value) {
		translation = value;
		return this;
	}

	public net.minecraft.network.chat.Component getName() {
		return translation ? new TranslatableComponent(name) : new TextComponent(name);
	}

	@Override
	public IComponentType getType() {
		return IComponentType.Name;
	}
}
