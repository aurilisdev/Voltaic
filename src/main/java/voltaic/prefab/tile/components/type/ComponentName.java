package voltaic.prefab.tile.components.type;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

	public IFormattableTextComponent getName() {
		return translation ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	@Override
	public IComponentType getType() {
		return IComponentType.Name;
	}
}
