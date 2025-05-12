package voltaic.prefab.tile.components.type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
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

    private GenericTile holder;

    private SingleProperty<Double> powerUsageMultiplier;

    private SingleProperty<Boolean> hasEjectorUpgrade;

    private SingleProperty<Boolean> hasInjectorUpgrade;

    private SingleProperty<Double> powerGenerationMultiplier;

    private SingleProperty<Integer> unbreakingLevel;

    private SingleProperty<Integer> fortuneLevel;

    private SingleProperty<Integer> silkTouchLevel;

    private SingleProperty<Boolean> hasExperienceUpgrade;

    private SingleProperty<Integer> rangeLevel;

    public ComponentUpgradeHandler(GenericTile holder) {
        this.holder = holder;

        powerUsageMultiplier = holder.property(new SingleProperty<>(PropertyTypes.DOUBLE, "powerusageupgradecomponent", 1.0));
        hasEjectorUpgrade = holder.property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasejectorupgradecomponent", false));
        hasInjectorUpgrade = holder.property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasinjectorupgradecomponent", false));
        powerGenerationMultiplier = holder.property(new SingleProperty<>(PropertyTypes.DOUBLE, "powergenupgradecomponent", 1.0));

        unbreakingLevel = holder.property(new SingleProperty<>(PropertyTypes.INTEGER, "unbreakinglevelupgradecomponent", 0));
        silkTouchLevel = holder.property(new SingleProperty<>(PropertyTypes.INTEGER, "silktouchlevelupgradecomponent", 0));
        fortuneLevel = holder.property(new SingleProperty<>(PropertyTypes.INTEGER, "fortunelevelupgradecomponent", 0));

        hasExperienceUpgrade = holder.property(new SingleProperty<>(PropertyTypes.BOOLEAN, "experienceupgradecomponent", false));

        rangeLevel = holder.property(new SingleProperty<>(PropertyTypes.INTEGER, "rangelevelupgradecomponent", 1));

    }

    @Override
    public void holder(GenericTile holder) {
        this.holder = holder;
    }

    @Override
    public IComponentType getType() {
        return IComponentType.UpgradeHandler;
    }

    @Override
    public void loadFromNBT(CompoundNBT nbt) {

    }

    @Override
    public void saveToNBT(CompoundNBT nbt) {

    }

    public void serverTick(ComponentTickable tick) {

        if (!hasEjectorUpgrade.getValue() && !hasInjectorUpgrade.getValue()) {
            return;
        }

        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);

        for (ItemStack stack : inv.getUpgradeContents()) {

            ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();

            if (upgrade.subtype == SubtypeItemUpgrade.itemoutput && hasEjectorUpgrade.getValue()) {

            }

        }

    }

    public void onInventoryChange(int slot, ComponentInventory inv) {

    }

}
