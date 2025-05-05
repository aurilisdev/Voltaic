package voltaic.prefab.tile;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import voltaic.Voltaic;

import voltaic.api.IWrenchItem;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.item.ItemUpgrade;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.variant.AbstractProperty;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.ItemUtils;
import voltaic.registers.VoltaicCapabilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class GenericTile extends TileEntity implements INameable, IPropertyHolderTile, ITickableTileEntity {

	private final IComponent[] components = new IComponent[IComponentType.values().length];
	private final PropertyManager propertyManager = new PropertyManager(this);

	// use this for manually setting the change flag
	public boolean isChanged = false;

	public GenericTile(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	@Override
	public void tick() {
		if (hasComponent(IComponentType.Tickable)) {
			ComponentTickable tickable = getComponent(IComponentType.Tickable);
			tickable.performTick(level);
		}
	}

	public <T extends AbstractProperty> T property(T prop) {
		for (AbstractProperty existing : propertyManager.getProperties()) {
			if (existing.getName().equals(prop.getName())) {
				throw new RuntimeException(prop.getName() + " is already being used by another property!");
			}
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

	public <T extends IComponent> T getComponent(IComponentType type) {
		return !hasComponent(type) ? null : (T) components[type.ordinal()];
	}

	public GenericTile addComponent(IComponent component) {
		component.holder(this);
		if (hasComponent(component.getType())) {
			throw new ExceptionInInitializerError("Component of type: " + component.getType().name() + " already registered!");
		}
		components[component.getType().ordinal()] = component;
		return this;
	}

	//Try not using this method
	@Deprecated
	public GenericTile forceComponent(IComponent component) {
		component.holder(this);
		components[component.getType().ordinal()] = component;
		return this;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		if (propertyManager != null && compound.contains(PropertyManager.NBT_KEY)) {
			CompoundNBT propertyData = compound.getCompound(PropertyManager.NBT_KEY);
			propertyManager.loadFromTag(propertyData);
			compound.remove(PropertyManager.NBT_KEY);
		}
		for (IComponent component : components) {
			if (component != null) {
				component.holder(this);
				component.loadFromNBT(compound);
			}
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (propertyManager != null) {
			CompoundNBT propertyData = new CompoundNBT();
			propertyManager.saveToTag(propertyData);
			compound.put(PropertyManager.NBT_KEY, propertyData);
		}
		for (IComponent component : components) {
			if (component != null) {
				component.holder(this);
				component.saveToNBT(compound);
			}
		}
		return super.save(compound);
	}
	
	// called either from initial client sync
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		if (propertyManager != null) {
			CompoundNBT propertyData = new CompoundNBT();
			propertyManager.saveAllPropsForClientSync(propertyData);
			tag.put(PropertyManager.NBT_KEY, propertyData);
			propertyManager.clean();
		}

		return tag;
	}

	// Called when Level#sendBlockUpdated is called
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		CompoundNBT data = new CompoundNBT();
		propertyManager.saveDirtyPropsToTag(data);
		tag.put(PropertyManager.NBT_KEY, data);
		return new SUpdateTileEntityPacket(getBlockPos(), 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT compoundtag = pkt.getTag();
        if (compoundtag != null) {
            load(getBlockState(), compoundtag);
        }
	}

	// Only fires on server side
	@Override
	public void onLoad() {
		super.onLoad();

		for (IComponent component : components) {
			if (component != null) {
				component.holder(this);
				component.onLoad();
			}
		}

		if (propertyManager != null) {
			propertyManager.onTileLoaded();
		}
	}

	@Override
	public ITextComponent getName() {
		return hasComponent(IComponentType.Name) ? this.<ComponentName>getComponent(IComponentType.Name).getName() : new TranslationTextComponent(Voltaic.ID + ".default.tile.name");
	}

	/* Since you have to register it anyway, might as well make it somewhat faster */

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK && components[IComponentType.Electrodynamic.ordinal()] != null) {
			return components[IComponentType.Electrodynamic.ordinal()].getCapability(cap, side, null);
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && components[IComponentType.FluidHandler.ordinal()] != null) {
			return components[IComponentType.FluidHandler.ordinal()].getCapability(cap, side, null);
		}
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && components[IComponentType.Inventory.ordinal()] != null) {
			return components[IComponentType.Inventory.ordinal()].getCapability(cap, side, null);
		}
		if (cap == CapabilityEnergy.ENERGY && components[IComponentType.ForgeEnergy.ordinal()] != null) {
			return components[IComponentType.ForgeEnergy.ordinal()].getCapability(cap, side, null);
		}
		return LazyOptional.empty();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (IComponent component : components) {
			if (component != null) {
				component.holder(this);
				component.remove();
			}
		}
	}

	public IIntArray getCoordsArray() {
		IntArray array = new IntArray(3);
		array.set(0, worldPosition.getX());
		array.set(1, worldPosition.getY());
		array.set(2, worldPosition.getZ());
		return array;
	}

	public boolean isPoweredByRedstone() {
		return level.getDirectSignalTo(worldPosition) > 0;
	}

	/**
	 * NORTH is defined as the default direction
	 *
	 * @return
	 */
	public Direction getFacing() {
		return getBlockState().hasProperty(VoltaicBlockStates.FACING) ? getBlockState().getValue(VoltaicBlockStates.FACING) : Direction.NORTH;
	}

	public void onEnergyChange(ComponentElectrodynamic cap) {
		// hook method for now
	}

	// no more polling for upgrade effects :D
	public void onInventoryChange(ComponentInventory inv, int slot) {
		// this can be moved to a seperate tile class in the future
		if (hasComponent(IComponentType.Processor)) {
			this.<ComponentProcessor>getComponent(IComponentType.Processor).onInventoryChange(inv, slot);
		}
	}

	public void onFluidTankChange(FluidTank tank) {
		// hook method for now
	}

	// This is ceded to the tile to allow for greater control with the use function
	public ActionResultType use(PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		ItemStack stack = player.getItemInHand(handIn);
		if (stack.getItem() instanceof ItemUpgrade && hasComponent(IComponentType.Inventory)) {
			ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();

			ComponentInventory inv = getComponent(IComponentType.Inventory);
			// null check for safety
			if (inv != null && inv.upgrades() > 0) {
				int upgradeIndex = inv.getUpgradeSlotStartIndex();
				for (int i = 0; i < inv.upgrades(); i++) {
					if (inv.canPlaceItem(upgradeIndex + i, stack)) {
						ItemStack upgradeStack = inv.getItem(upgradeIndex + i);
						if (upgradeStack.isEmpty()) {
							if (!level.isClientSide()) {
								inv.setItem(upgradeIndex + i, stack.copy());
								stack.shrink(stack.getCount());
							}
							return ActionResultType.CONSUME;
						}
						if (ItemUtils.testItems(upgrade, upgradeStack.getItem())) {
							int room = upgradeStack.getMaxStackSize() - upgradeStack.getCount();
							if (room > 0) {
								if (!level.isClientSide()) {
									int accepted = room > stack.getCount() ? stack.getCount() : room;
									upgradeStack.grow(accepted);
									stack.shrink(accepted);
								}
								return ActionResultType.CONSUME;
							}
						}
					}
				}
			}

		} else if (!(stack.getItem() instanceof IWrenchItem)) {
			if (hasComponent(IComponentType.ContainerProvider)) {

				if (!level.isClientSide) {

					player.openMenu(getComponent(IComponentType.ContainerProvider));

					player.awardStat(Stats.INTERACT_WITH_FURNACE);

				}

				return ActionResultType.CONSUME;

			}
		}
		return ActionResultType.PASS;
	}

	public void onBlockDestroyed() {

	}

	public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {

	}

	public void onPlace(BlockState oldState, boolean isMoving) {

		for (IComponent component : components) {
			if (component != null) {
				component.holder(this);
				component.onLoad();
			}
		}

	}

	public int getComparatorSignal() {
		return 0;
	}

	public int getDirectSignal(Direction dir) {
		return 0;
	}

	public int getSignal(Direction dir) {
		return 0;
	}

	public void onEntityInside(BlockState state, World level, BlockPos pos, Entity entity) {

	}

	protected static TriPredicate<Integer, ItemStack, ComponentInventory> machineValidator() {
		return (x, y, i) ->
		//
		x < i.getOutputStartIndex() ||
		//
				x >= i.getInputBucketStartIndex() && x < i.getUpgradeSlotStartIndex() && y.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) != null ||
				//
				x >= i.getUpgradeSlotStartIndex() && y.getItem() instanceof ItemUpgrade && i.isUpgradeValid(((ItemUpgrade) y.getItem()).subtype);
		//
	}

	public static final int[] arr(int... values) {
		return values;
	}

	/**
	 * This method will never have air as the newState unless something has gone horribly horribly wrong!
	 *
	 * @param oldState
	 * @param newState
	 */
	public void onBlockStateUpdate(BlockState oldState, BlockState newState) {
		for (IComponent component : components) {
			if (component != null) {
				component.refreshIfUpdate(oldState, newState);
			}
		}
	}

	public void setPlacedBy(LivingEntity player, ItemStack stack) {

	}

}
