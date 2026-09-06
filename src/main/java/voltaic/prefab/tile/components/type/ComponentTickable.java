package voltaic.prefab.tile.components.type;

import java.util.function.BiConsumer;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

public class ComponentTickable implements IComponent {

    private final GenericTile holder;

    protected BiConsumer<Level, ComponentTickable> tickClient = (level, tickable) -> {};
    protected BiConsumer<Level, ComponentTickable> tickCommon = (level, tickable) -> {};
    protected BiConsumer<Level, ComponentTickable> tickServer = (level, tickable) -> {};

    private long ticks = 0;

    public ComponentTickable(GenericTile holder) {
	this.holder = holder;
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    public ComponentTickable tickCommon(BiConsumer<Level, ComponentTickable> consumer) {
	tickCommon = consumer.andThen(tickCommon);
	return this;
    }

    public ComponentTickable tickClient(BiConsumer<Level, ComponentTickable> consumer) {
	tickClient = consumer.andThen(tickClient);
	return this;
    }

    public ComponentTickable tickServer(BiConsumer<Level, ComponentTickable> consumer) {
	tickServer = consumer.andThen(tickServer);
	return this;
    }

    public void tickCommon(Level level) {
	ticks++;
	tickCommon.accept(level, this);
    }

    public void tickServer(Level level) {
	tickServer.accept(level, this);
    }

    public void tickClient(Level level) {
	tickClient.accept(level, this);
    }

    public long getTicks() {
	return ticks;
    }

    public void performTick(Level level) {
	tickCommon(level);
	if (level.isClientSide) {
	    tickClient(level);
	} else {
	    tickServer(level);
	    if (holder.getPropertyManager().isDirty() || holder.isChanged) {
		holder.setChanged();
		level.sendBlockUpdated(holder.getBlockPos(), holder.getBlockState(), holder.getBlockState(),
			Block.UPDATE_CLIENTS);
		holder.isChanged = false;
	    }
	}
    }

    @Override
    public IComponentType getType() {
	return IComponentType.Tickable;
    }
}
