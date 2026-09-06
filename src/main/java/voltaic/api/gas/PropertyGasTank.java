package voltaic.api.gas;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;

/**
 * An extension of the GasTank class incorporating the Electrodynamics property
 * system
 * 
 * @author skip999
 *
 */
public class PropertyGasTank extends GasTank {

    protected GenericTile holder;

    protected SingleProperty<GasStack> gasProperty;
    protected SingleProperty<Integer> capacityProperty;
    protected SingleProperty<Integer> maxTemperatureProperty;
    protected SingleProperty<Integer> maxPressureProperty;

    protected BiConsumer<GasTank, GenericTile> onGasCondensed = (gas, tile) -> {};

    public PropertyGasTank(GenericTile holder, String key, int capacity, int maxTemperature, int maxPressure) {
	super(capacity, maxTemperature, maxPressure);

	this.holder = holder;

	gasProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.GAS_STACK,
		"propertygastankstack" + key, GasStack.EMPTY));
	capacityProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.INTEGER,
		"propertygastankcapacity" + key, capacity));
	maxTemperatureProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(),
		PropertyTypes.INTEGER, "propertygastankmaxtemperature" + key, maxTemperature));
	maxPressureProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.INTEGER,
		"propertygastankmaxpressure" + key, maxPressure));
    }

    public PropertyGasTank(GenericTile holder, String key, int capacity, int maxTemperature, int maxPressure,
	    Predicate<GasStack> isGasValid) {
	super(capacity, maxTemperature, maxPressure, isGasValid);

	this.holder = holder;

	gasProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.GAS_STACK,
		"propertygastankstack" + key, GasStack.EMPTY));
	capacityProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.INTEGER,
		"propertygastankcapacity" + key, capacity));
	maxTemperatureProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(),
		PropertyTypes.INTEGER, "propertygastankmaxtemperature" + key, maxTemperature));
	maxPressureProperty = holder.property(new SingleProperty<>(holder.getPropertyManager(), PropertyTypes.INTEGER,
		"propertygastankmaxpressure" + key, maxPressure));

    }

    protected PropertyGasTank(PropertyGasTank other) {
	super(other.getCapacity(), other.getMaxTemperature(), other.getMaxPressure(), other.isGasValid);

	holder = other.holder;

	gasProperty = other.gasProperty;
	capacityProperty = other.capacityProperty;
	maxTemperatureProperty = other.maxTemperatureProperty;
	maxPressureProperty = other.maxPressureProperty;

    }

    @Override
    public PropertyGasTank setValidator(Predicate<GasStack> predicate) {
	return (PropertyGasTank) super.setValidator(predicate);
    }

    public PropertyGasTank setOnGasCondensed(BiConsumer<GasTank, GenericTile> onGasCondensed) {
	this.onGasCondensed = onGasCondensed;
	return this;
    }

    @Override
    public void setGas(GasStack gas) {
	gasProperty.setValue(gas);
    }

    @Override
    public void setCapacity(int capacity) {
	capacityProperty.setValue(capacity);
    }

    @Override
    public void setMaxTemperature(int temperature) {
	maxTemperatureProperty.setValue(temperature);
    }

    @Override
    public void setMaxPressure(int atm) {
	maxPressureProperty.setValue(atm);
    }

    @Override
    public GasStack getGas() {
	return gasProperty.getValue();
    }

    @Override
    public int getGasAmount() {
	return getGas().getAmount();
    }

    @Override
    public int getCapacity() {
	return capacityProperty.getValue();
    }

    @Override
    public int getMaxTemperature() {
	return maxTemperatureProperty.getValue();
    }

    @Override
    public int getMaxPressure() {
	return maxPressureProperty.getValue();
    }

    @Override
    public void onChange() {
	gasProperty.forceDirtyForManager();
	holder.onGasTankChange(this);
    }

    @Override
    public void onOverheat() {
	Level level = holder.getLevel();
	if (level == null)
	    return;

	BlockPos pos = holder.getBlockPos();
	level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
    }

    @Override
    public void onOverpressure() {
	Level level = holder.getLevel();
	if (level == null)
	    return;

	BlockPos pos = holder.getBlockPos();
	level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, ExplosionInteraction.BLOCK);
    }

    @Override
    public void onGasCondensed() {
	onGasCondensed.accept(this, holder);
    }

    public PropertyGasTank[] asArray() {
	return new PropertyGasTank[] { this };
    }

}
