package voltaic.prefab.tile.components.type;

import java.util.function.BiFunction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

public class ComponentContainerProvider implements IComponent, INamedContainerProvider {

	protected GenericTile holder = null;
	protected BiFunction<Integer, PlayerInventory, Container> createMenuFunction;
	protected String name = "";

	public ComponentContainerProvider(String name, GenericTile holder) {
		this.name = "container." + name;
		this.holder = holder;
	}

	@Override
	public void holder(GenericTile holder) {
		this.holder = holder;
	}

	@Override
	public GenericTile getHolder() {
		return holder;
	}

	public ComponentContainerProvider createMenu(BiFunction<Integer, PlayerInventory, Container> createMenuFunction) {
		this.createMenuFunction = createMenuFunction;
		return this;
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
		if (createMenuFunction != null) {
			if (holder.hasComponent(IComponentType.Inventory)) {
				ComponentInventory componentinv = holder.getComponent(IComponentType.Inventory);
				if (!componentinv.stillValid(pl)) {
					return null;
				}
				componentinv.startOpen(pl);
			}
			return createMenuFunction.apply(id, inv);
		}
		return null;
	}

	@Override
	public IFormattableTextComponent getDisplayName() {
		return new TranslationTextComponent(name);
	}

	@Override
	public IComponentType getType() {
		return IComponentType.ContainerProvider;
	}
}
