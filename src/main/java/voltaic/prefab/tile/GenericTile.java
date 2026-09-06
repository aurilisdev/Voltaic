package voltaic.prefab.tile;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.Voltaic;
import voltaic.api.IWrenchItem;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandler;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.packet.types.client.PacketUpdateCariedItemClient;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.variant.AbstractProperty;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentName;
import voltaic.prefab.tile.components.type.ComponentProcessor;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.utilities.ItemUtils;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicDataComponentTypes;

public abstract class GenericTile extends BlockEntity implements Nameable, IPropertyHolderTile {

    private final IComponent[] components = new IComponent[IComponentType.values().length];
    private final PropertyManager propertyManager = new PropertyManager(this);

    // use this for manually setting the change flag
    public boolean isChanged = false;

    public GenericTile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState) {
	super(tileEntityTypeIn, worldPos, blockState);
    }

    public <T extends AbstractProperty> T property(T prop) {
	for (AbstractProperty existing : propertyManager.getProperties()) {
	    if (existing.getName().equals(prop.getName()))
		throw new RuntimeException(prop.getName() + " is already being used by another property!");
	}

	return propertyManager.addProperty(prop);
    }

    @Override
    public PropertyManager getPropertyManager() {
	return propertyManager;
    }

    public boolean hasComponent(IComponentType type) {
	return components[type.ordinal()] != null;
    }

    public <T extends IComponent> Optional<T> getComponent(IComponentType type) {
	return Optional.ofNullable((T) components[type.ordinal()]);
    }

    /**
     * Returns the requested component or throws if it is unavailable. Only use this
     * when you are completely certain that the tile has the component, or when the
     * component is required for a feature to function.
     *
     * @deprecated Prefer {@link #getComponent(IComponentType)} unless the component
     *             is guaranteed to exist.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    public <C extends IComponent> C requireComponent(IComponentType type) {
	return this.<C>getComponent(type).orElseThrow(
		() -> new IllegalStateException("Tile " + this + " is missing required component " + type));
    }

    public GenericTile addComponent(IComponent component) {
	if (hasComponent(component.getType()))
	    throw new ExceptionInInitializerError(
		    "Component of type: " + component.getType().name() + " already registered!");
	components[component.getType().ordinal()] = component;
	return this;
    }

    @Deprecated(since = "Try not using this method.")
    public GenericTile forceComponent(IComponent component) {
	components[component.getType().ordinal()] = component;
	return this;
    }

    // called when tile is created/loaded from memory
    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	super.loadAdditional(compound, registries);
	if (compound.contains(PropertyManager.NBT_KEY)) {
	    CompoundTag propertyData = compound.getCompound(PropertyManager.NBT_KEY);
	    propertyManager.loadFromTag(propertyData, registries);
	    compound.remove(PropertyManager.NBT_KEY);
	}
	for (IComponent component : components) {
	    if (component != null) {
		component.loadFromNBT(compound);
	    }
	}
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	CompoundTag propertyData = new CompoundTag();
	propertyManager.saveToTag(propertyData, registries);
	compound.put(PropertyManager.NBT_KEY, propertyData);
	for (IComponent component : components) {
	    if (component != null) {
		component.saveToNBT(compound);
	    }
	}
	super.saveAdditional(compound, registries);
    }

    // called either from initial client sync
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
	CompoundTag tag = super.getUpdateTag(registries);
	CompoundTag propertyData = new CompoundTag();
	propertyManager.saveAllPropsForClientSync(propertyData, registries);
	tag.put(PropertyManager.NBT_KEY, propertyData);
	propertyManager.clean();

	return tag;
    }

    // Called when Level#sendBlockUpdated is called
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
	return ClientboundBlockEntityDataPacket.create(this, (tile, registries) -> {
	    CompoundTag tag = new CompoundTag();
	    CompoundTag data = new CompoundTag();
	    propertyManager.flushDirtyPropsToTag(data, registries);
	    tag.put(PropertyManager.NBT_KEY, data);
	    return tag;
	});
    }

    // Only fires on server side
    @Override
    public void onLoad() {
	super.onLoad();

	Level level = this.level;
	if (level == null)
	    throw new IllegalStateException("Block entity " + getType() + " has no level after onLoad at "
		    + worldPosition + ". This should not happen in vanilla Minecraft code.");

	for (IComponent component : components) {
	    if (component != null) {
		component.onLoad(level);
	    }
	}

	propertyManager.onTileLoaded();
    }

    @Override
    public Component getName() {
	return this.<ComponentName>getComponent(IComponentType.Name).map(ComponentName::getName)
		.orElse(Component.literal(Voltaic.ID + ".default.tile.name"));
    }

    /*
     * Since you have to register it anyway, might as well make it somewhat faster
     */
    @Nullable
    public ICapabilityElectrodynamic getElectrodynamicCapability(@Nullable Direction side) {
	return this.<ComponentElectrodynamic>getComponent(IComponentType.Electrodynamic)
		.map(electro -> electro.getCapability(side, CapabilityInputType.NONE)).orElse(null);
    }

    @Nullable
    public IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
	return this.<IComponentFluidHandler>getComponent(IComponentType.FluidHandler)
		.map(fluid -> fluid.getCapability(side, CapabilityInputType.NONE)).orElse(null);
    }

    @Nullable
    public IGasHandler getGasHandlerCapability(@Nullable Direction side) {
	return this.<IComponentGasHandler>getComponent(IComponentType.GasHandler)
		.map(gas -> gas.getCapability(side, CapabilityInputType.NONE)).orElse(null);
    }

    @Nullable
    public IItemHandler getItemHandlerCapability(@Nullable Direction side) {
	return this.<ComponentInventory>getComponent(IComponentType.Inventory)
		.map(inv -> inv.getCapability(side, CapabilityInputType.NONE)).orElse(null);
    }

    @Nullable
    public IEnergyStorage getForgeEnergyCapability(@Nullable Direction side) {
	return this.<ComponentForgeEnergy>getComponent(IComponentType.ForgeEnergy)
		.map(energy -> energy.getCap(side, CapabilityInputType.NONE)).orElse(null);
    }

    @Override
    public void setRemoved() {
	super.setRemoved();
	for (IComponent component : components) {
	    if (component != null) {
		component.remove();
	    }
	}
    }

    public SimpleContainerData getCoordsArray() {
	SimpleContainerData array = new SimpleContainerData(3);
	array.set(0, worldPosition.getX());
	array.set(1, worldPosition.getY());
	array.set(2, worldPosition.getZ());
	return array;
    }

    public boolean isPoweredByRedstone() {
	Level level = this.level;
	if (level == null)
	    return false;

	return level.getDirectSignalTo(worldPosition) > 0;
    }

    /**
     * NORTH is defined as the default direction
     *
     * @return the facing direction.
     */
    public Direction getFacing() {
	return getBlockState().hasProperty(VoltaicBlockStates.FACING)
		? getBlockState().getValue(VoltaicBlockStates.FACING)
		: Direction.NORTH;
    }

    public void onEnergyChange(ComponentElectrodynamic cap) {
    }

    public void onInventoryChange(ComponentInventory inv, int slot) {
	this.<ComponentProcessor>getComponent(IComponentType.Processor)
		.ifPresent(processor -> processor.onInventoryChange(inv, slot));
    }

    public void onFluidTankChange(FluidTank tank) {
    }

    public void onGasTankChange(GasTank tank) {
    }

    public InteractionResult useWithoutItem(Level level, Player player, BlockHitResult hit) {
	if (hasComponent(IComponentType.ContainerProvider)) {
	    if (!level.isClientSide) {
		player.openMenu(this.<ComponentContainerProvider>getComponent(IComponentType.ContainerProvider).get());
		player.awardStat(Stats.INTERACT_WITH_FURNACE);
	    }
	    return InteractionResult.CONSUME;
	}
	return InteractionResult.PASS;
    }

    public ItemInteractionResult useWithItem(Level level, ItemStack used, Player player, InteractionHand hand,
	    BlockHitResult hit) {
	if (used.getItem() instanceof ItemUpgrade upgrade && hasComponent(IComponentType.Inventory)) {
	    ComponentInventory inv = this.<ComponentInventory>getComponent(IComponentType.Inventory).get();
	    // null check for safety
	    if (inv != null && inv.upgrades() > 0) {
		int upgradeIndex = inv.getUpgradeSlotStartIndex();
		for (int i = 0; i < inv.upgrades(); i++) {
		    if (inv.canPlaceItem(upgradeIndex + i, used)) {
			ItemStack upgradeStack = inv.getItem(upgradeIndex + i);
			if (upgradeStack.isEmpty()) {
			    if (!level.isClientSide()) {
				inv.setItem(upgradeIndex + i, used.copy());
				used.shrink(used.getCount());
			    }
			    return ItemInteractionResult.CONSUME;
			}
			if (ItemUtils.testItems(upgrade, upgradeStack.getItem())) {
			    int room = upgradeStack.getMaxStackSize() - upgradeStack.getCount();
			    if (room > 0) {
				if (!level.isClientSide()) {
				    int accepted = room > used.getCount() ? used.getCount() : room;
				    upgradeStack.grow(accepted);
				    used.shrink(accepted);
				}
				return ItemInteractionResult.CONSUME;
			    }
			}
		    }
		}
	    }

	} else if (!(used.getItem() instanceof IWrenchItem)) {
	}
	return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void onBlockDestroyed(Level level) {
    }

    public void onNeighbourChanged(LevelReader reader, BlockPos neighbor, boolean blockStateTrigger) {
    }

    public void onPlace(Level level, BlockState oldState, boolean isMoving) {
    }

    public int getComparatorSignal(Level level) {
	return 0;
    }

    public int getDirectSignal(Direction dir) {
	return 0;
    }

    public int getSignal(Direction dir) {
	return 0;
    }

    public void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    }

    public void updateCarriedItemInContainer(ItemStack stack, UUID playerId) {
	Level level = this.level;
	if (level == null)
	    return;

	ServerPlayer serverPlayer = (ServerPlayer) level.getPlayerByUUID(playerId);
	if (serverPlayer == null)
	    return;

	if (serverPlayer.hasContainerOpen()) {
	    stack.set(VoltaicDataComponentTypes.HASCLICKEDONFLUIDGAUGE, false);
	    serverPlayer.containerMenu.setCarried(stack);
	    PacketDistributor.sendToPlayer(serverPlayer,
		    new PacketUpdateCariedItemClient(stack, worldPosition, playerId));
	}
    }

    protected static TriPredicate<Integer, ItemStack, ComponentInventory> machineValidator() {
	return (x, y, i) ->
	//
	x < i.getOutputStartIndex() ||
	//
		x >= i.getInputBucketStartIndex() && x < i.getInputGasStartIndex()
			&& y.getCapability(Capabilities.FluidHandler.ITEM) != null
		||
		//
		x >= i.getInputGasStartIndex() && x < i.getUpgradeSlotStartIndex()
			&& y.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM) != null
		||
		//
		x >= i.getUpgradeSlotStartIndex() && y.getItem() instanceof ItemUpgrade upgrade
			&& i.isUpgradeValid(upgrade.subtype);
	//
    }

    public static final int[] arr(int... values) {
	return values;
    }

    /**
     * This method will never have air as the newState unless something has gone
     * horribly horribly wrong!
     * 
     * @param level
     *
     * @param oldState
     * @param newState
     */
    public void onBlockStateUpdate(Level level, BlockState oldState, BlockState newState) {
	for (IComponent component : components) {
	    if (component != null) {
		component.refreshIfUpdate(level, oldState, newState);
	    }
	}
    }

    public void setPlacedBy(LivingEntity player, ItemStack stack) {
    }
}
