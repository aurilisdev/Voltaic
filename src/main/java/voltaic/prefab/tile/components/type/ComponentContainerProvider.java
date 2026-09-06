package voltaic.prefab.tile.components.type;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

public class ComponentContainerProvider implements IComponent, MenuProvider {

    protected String name = "";
    protected GenericTile holder;

    @Nullable
    protected BiFunction<Integer, Inventory, AbstractContainerMenu> createMenuFunction;

    public ComponentContainerProvider(String name, GenericTile holder) {
	this.name = "container." + name;
	this.holder = holder;
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    public ComponentContainerProvider createMenu(
	    BiFunction<Integer, Inventory, AbstractContainerMenu> createMenuFunction) {
	this.createMenuFunction = createMenuFunction;
	return this;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player pl) {
	BiFunction<Integer, Inventory, AbstractContainerMenu> pCreateMenuFunction = createMenuFunction;
	if (pCreateMenuFunction != null) {
	    if (holder.hasComponent(IComponentType.Inventory)) {
		Optional<ComponentInventory> oCompInv = holder.getComponent(IComponentType.Inventory);
		if (oCompInv.isEmpty())
		    return null;

		ComponentInventory compInv = oCompInv.get();
		if (!compInv.stillValid(pl))
		    return null;

		compInv.startOpen(pl);
	    }
	    return pCreateMenuFunction.apply(id, inv);
	}
	return null;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
	return net.minecraft.network.chat.Component.translatable(name);
    }

    @Override
    public IComponentType getType() {
	return IComponentType.ContainerProvider;
    }
}
