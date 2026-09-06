package voltaic.prefab.tile.components.type;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;

@SuppressWarnings("unused")
public class ComponentUpgradeHandler implements IComponent {

    public static final double BASIC_SPEED_BOOST = 1.5;
    public static final double BASIC_SPEED_POWER = 1.5;

    public static final double ADVANCED_SPEED_BOOST = 2.25;
    public static final double ADVANCED_SPEED_POWER = 2.25;

    public static final double SOLAR_CELL_MULT = 2.25;

    public static final double STATOR_MULT = 2.25;

    private final GenericTile holder;

    private final SingleProperty<Double> powerUsageMultiplier;

    private final SingleProperty<Boolean> hasEjectorUpgrade;

    private final SingleProperty<Boolean> hasInjectorUpgrade;

    private final SingleProperty<Double> powerGenerationMultiplier;

    private final SingleProperty<Integer> unbreakingLevel;

    private final SingleProperty<Integer> fortuneLevel;

    private final SingleProperty<Integer> silkTouchLevel;

    private final SingleProperty<Boolean> hasExperienceUpgrade;

    private final SingleProperty<Integer> rangeLevel;

    public ComponentUpgradeHandler(PropertyManager manager, GenericTile holder) {
	this.holder = holder;

	powerUsageMultiplier = holder
		.property(new SingleProperty<>(manager, PropertyTypes.DOUBLE, "powerusageupgradecomponent", 1.0));
	hasEjectorUpgrade = holder
		.property(new SingleProperty<>(manager, PropertyTypes.BOOLEAN, "hasejectorupgradecomponent", false));
	hasInjectorUpgrade = holder
		.property(new SingleProperty<>(manager, PropertyTypes.BOOLEAN, "hasinjectorupgradecomponent", false));
	powerGenerationMultiplier = holder
		.property(new SingleProperty<>(manager, PropertyTypes.DOUBLE, "powergenupgradecomponent", 1.0));

	unbreakingLevel = holder
		.property(new SingleProperty<>(manager, PropertyTypes.INTEGER, "unbreakinglevelupgradecomponent", 0));
	silkTouchLevel = holder
		.property(new SingleProperty<>(manager, PropertyTypes.INTEGER, "silktouchlevelupgradecomponent", 0));
	fortuneLevel = holder
		.property(new SingleProperty<>(manager, PropertyTypes.INTEGER, "fortunelevelupgradecomponent", 0));

	hasExperienceUpgrade = holder
		.property(new SingleProperty<>(manager, PropertyTypes.BOOLEAN, "experienceupgradecomponent", false));

	rangeLevel = holder
		.property(new SingleProperty<>(manager, PropertyTypes.INTEGER, "rangelevelupgradecomponent", 1));

    }

    @Override
    public IComponentType getType() {
	return IComponentType.UpgradeHandler;
    }

    @Override
    public void loadFromNBT(CompoundTag nbt) {

    }

    @Override
    public void saveToNBT(CompoundTag nbt) {

    }

    public void serverTick(ComponentTickable tick) {

	if (!hasEjectorUpgrade.getValue() && !hasInjectorUpgrade.getValue())
	    return;

	Optional<ComponentInventory> oInv = holder.getComponent(IComponentType.Inventory);
	if (oInv.isEmpty())
	    return;

	ComponentInventory inv = oInv.get();

	for (ItemStack stack : inv.getUpgradeContents()) {

	    ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();

	    if (upgrade.subtype == SubtypeItemUpgrade.itemoutput && hasEjectorUpgrade.getValue()) {

	    }

	}

    }

    public void onInventoryChange(int slot, ComponentInventory inv) {

    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

}
