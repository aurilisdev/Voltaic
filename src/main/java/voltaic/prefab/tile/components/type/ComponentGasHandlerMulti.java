package voltaic.prefab.tile.components.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.api.gas.IGasHandler;
import voltaic.api.gas.PropertyGasTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentGasHandler;
import voltaic.prefab.tile.components.utils.RegistryValidatorUtils;
import voltaic.prefab.tile.components.utils.SidedCapabilityHandler;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.registers.VoltaicGases;

public class ComponentGasHandlerMulti implements IComponentGasHandler {

    private final GenericTile holder;

    public Direction[] inputDirections = {};
    public Direction[] outputDirections = {};
    private PropertyGasTank[] inputTanks = {};
    private PropertyGasTank[] outputTanks = {};
    private TagKey<Gas>[] validInputGasTags = (TagKey<Gas>[]) new TagKey<?>[0];
    private TagKey<Gas>[] validOutputGasTags = (TagKey<Gas>[]) new TagKey<?>[0];
    private Gas[] validInputGases = {};
    private Gas[] validOutputGases = {};
    private final HashSet<Gas> inputValidatorGases = new HashSet<>();
    private final HashSet<Gas> outputValidatorGases = new HashSet<>();

    private final SidedCapabilityHandler<IGasHandler> sidedHandler = new SidedCapabilityHandler<>(IGasHandler[]::new);

    private @Nullable RecipeType<? extends AbstractMaterialRecipe> recipeType;

    public ComponentGasHandlerMulti(GenericTile holder) {
	this.holder = holder;

	if (!holder.getBlockState().hasProperty(VoltaicBlockStates.FACING))
	    throw new UnsupportedOperationException("The tile " + holder + " must have the FACING direction property!");
    }

    public ComponentGasHandlerMulti setInputTanks(int count, int[] capacity, int[] maxTemperature, int[] maxPressure) {
	inputTanks = new PropertyGasTank[count];
	if (capacity.length < count)
	    throw new UnsupportedOperationException(
		    "The number of capacities does not match the number of input tanks");
	if (maxPressure.length < count)
	    throw new UnsupportedOperationException(
		    "The number of max temperatures does not match the number of input tanks");
	if (maxTemperature.length < count)
	    throw new UnsupportedOperationException(
		    "The number of max pressures does not match the number of input tanks");
	for (int i = 0; i < count; i++) {
	    inputTanks[i] = new PropertyGasTank(holder, "input" + i, capacity[i], maxTemperature[i], maxPressure[i]);
	}
	return this;
    }

    public ComponentGasHandlerMulti setOutputTanks(int count, int[] capacity, int[] maxTemperature, int[] maxPressure) {
	outputTanks = new PropertyGasTank[count];
	if (capacity.length < count)
	    throw new UnsupportedOperationException(
		    "The number of capacities does not match the number of output tanks");
	if (maxPressure.length < count)
	    throw new UnsupportedOperationException(
		    "The number of max temperatures does not match the number of output tanks");
	if (maxTemperature.length < count)
	    throw new UnsupportedOperationException(
		    "The number of max pressures does not match the number of output tanks");
	for (int i = 0; i < count; i++) {
	    outputTanks[i] = new PropertyGasTank(holder, "output" + i, capacity[i], maxTemperature[i], maxPressure[i]);
	}
	return this;
    }

    public ComponentGasHandlerMulti setTanks(int inputCount, int[] inputCapacity, int[] inputMaxTemperature,
	    int[] inputMaxPressure, int outputCount, int[] outputCapacity, int[] outputMaxTemperature,
	    int[] outputMaxPressure) {
	return setInputTanks(inputCount, inputCapacity, inputMaxTemperature, inputMaxPressure)
		.setOutputTanks(outputCount, outputCapacity, outputMaxTemperature, outputMaxPressure);
    }

    public ComponentGasHandlerMulti setInputGases(Gas... gases) {
	validInputGases = gases;
	return this;
    }

    public ComponentGasHandlerMulti setInputGasTags(TagKey<Gas>... gases) {
	validInputGasTags = gases;
	return this;
    }

    public ComponentGasHandlerMulti setOutputGases(Gas... gases) {
	validOutputGases = gases;
	return this;
    }

    public ComponentGasHandlerMulti setOutputGasTags(TagKey<Gas>... gases) {
	validOutputGasTags = gases;
	return this;
    }

    public ComponentGasHandlerMulti setInputDirections(BlockEntityUtils.MachineDirection... directions) {
	sidedHandler.enableSidedAccess();
	inputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	return this;
    }

    public ComponentGasHandlerMulti setOutputDirections(BlockEntityUtils.MachineDirection... directions) {
	sidedHandler.enableSidedAccess();
	outputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	return this;
    }

    public ComponentGasHandlerMulti setRecipeType(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	this.recipeType = recipeType;
	return this;
    }

    // It is assumed you have defined tanks when calling this method
    public ComponentGasHandlerMulti setCondensedHandler(BiConsumer<GasTank, GenericTile> consumer) {
	for (PropertyGasTank tank : inputTanks) {
	    tank.setOnGasCondensed(consumer);
	}
	for (PropertyGasTank tank : outputTanks) {
	    tank.setOnGasCondensed(consumer);
	}
	return this;
    }

    public int tankCount(boolean input) {
	return tanks(input).length;
    }

    public GasStack getGasInTank(int tank, boolean input) {
	return tanks(input)[tank].getGas();
    }

    @Nullable
    public PropertyGasTank getTankFromGas(Gas gas, boolean isInput) {
	if (isInput) {
	    for (PropertyGasTank tank : inputTanks) {
		if (tank.getGas().getGas().equals(gas))
		    return tank;
	    }
	    for (PropertyGasTank tank : inputTanks) {
		if (tank.isEmpty())
		    return tank;
	    }
	}
	for (PropertyGasTank tank : outputTanks) {
	    if (tank.getGas().getGas().equals(gas))
		return tank;
	}
	for (PropertyGasTank tank : outputTanks) {
	    if (tank.isEmpty())
		return tank;
	}

	return null;
    }

    public int getTankCapacity(int tank, boolean input) {
	return tanks(input)[tank].getCapacity();
    }

    public boolean isGasValid(int tank, GasStack stack, boolean input) {
	return tanks(input)[tank].isGasValid(stack);
    }

    public int fill(int tank, GasStack resource, GasAction action, boolean input) {
	return tanks(input)[tank].fill(resource, action);
    }

    public GasStack drain(int tank, GasStack resource, GasAction action, boolean input) {
	return tanks(input)[tank].drain(resource, action);
    }

    public GasStack drain(int tank, int maxDrain, GasAction action, boolean input) {
	return tanks(input)[tank].drain(maxDrain, action);
    }

    private PropertyGasTank[] tanks(boolean input) {
	return input ? inputTanks : outputTanks;
    }

    @Override
    @Nullable
    public IGasHandler getCapability(@Nullable Direction direction, CapabilityInputType mode) {
	return sidedHandler.getSided(direction);
    }

    @Override
    public void refreshIfUpdate(Level level, BlockState oldState, BlockState newState) {
	if (sidedHandler.requiresRefresh(oldState, newState)) {
	    defineOptionals(level, newState.getValue(VoltaicBlockStates.FACING));
	}
    }

    @Override
    public void refresh(Level level) {
	defineOptionals(level, holder.getFacing());
    }

    private void defineOptionals(Level level, Direction facing) {
	level.invalidateCapabilities(holder.getBlockPos());
	sidedHandler.refresh(facing, inputDirections, () -> new InputTankDispatcher(inputTanks), outputDirections,
		() -> new OutputTankDispatcher(outputTanks));
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    @Override
    public void onLoad(Level level) {
	IComponentGasHandler.super.onLoad(level);

	RecipeType<? extends AbstractMaterialRecipe> recipeType = this.recipeType;
	if (recipeType != null) {
	    configureTanksFromRecipes(recipeType);
	} else {
	    RegistryValidatorUtils.populate(inputValidatorGases, validInputGases, validInputGasTags,
		    VoltaicGases.GAS_REGISTRY);
	    RegistryValidatorUtils.populate(outputValidatorGases, validOutputGases, validOutputGasTags,
		    VoltaicGases.GAS_REGISTRY);
	}
	applyValidators();
    }

    private void configureTanksFromRecipes(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	GasRecipeRequirements requirements = collectRecipeRequirements(recipeType);
	inputValidatorGases.addAll(requirements.inputGases);
	outputValidatorGases.addAll(requirements.outputGases);

	configureTanks(inputTanks, 0, requirements.input);
	int outputOffset = configurePrimaryOutputTank(requirements.output);
	configureTanks(outputTanks, outputOffset, requirements.biproduct);
    }

    private GasRecipeRequirements collectRecipeRequirements(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	GasRecipeRequirements requirements = new GasRecipeRequirements();
	for (RecipeHolder<VoltaicRecipe> recipeHolder : VoltaicRecipe.findRecipesbyType(recipeType,
		holder.getLevel())) {
	    AbstractMaterialRecipe recipe = (AbstractMaterialRecipe) recipeHolder.value();
	    for (GasIngredient ingredient : recipe.getGasIngredients()) {
		ingredient.getMatchingGases().forEach(gas -> requirements.inputGases.add(gas.getGas()));
		requirements.input.include(ingredient.getGasStack());
	    }

	    GasStack output = recipe.getGasRecipeOutput();
	    requirements.outputGases.add(output.getGas());
	    requirements.output.include(output);

	    if (recipe.hasGasBiproducts()) {
		for (GasStack biproduct : recipe.getFullGasBiStacks()) {
		    requirements.outputGases.add(biproduct.getGas());
		    requirements.biproduct.include(biproduct);
		}
	    }
	}
	return requirements;
    }

    private static void configureTanks(PropertyGasTank[] tanks, int startIndex, GasTankRequirement requirement) {
	if (requirement.amount <= 0)
	    return;

	for (int i = startIndex; i < tanks.length; i++) {
	    configureTank(tanks[i], requirement);
	}
    }

    private int configurePrimaryOutputTank(GasTankRequirement requirement) {
	if (requirement.amount <= 0)
	    return 0;

	configureTank(outputTanks[0], requirement);
	return 1;
    }

    private static void configureTank(PropertyGasTank tank, GasTankRequirement requirement) {
	double capacity = roundUpTankCapacity(requirement.amount);
	int pressure = roundUpPressure(requirement.pressure);
	if (tank.getCapacity() < capacity) {
	    tank.setCapacity((int) capacity);
	}
	if (tank.getMaxTemperature() < requirement.temperature) {
	    tank.setMaxTemperature((int) (requirement.temperature + 10.0));
	}
	if (tank.getMaxPressure() < pressure) {
	    tank.setMaxPressure(pressure);
	}
    }

    private static double roundUpTankCapacity(double amount) {
	return amount / TANK_MULTIPLIER * TANK_MULTIPLIER + TANK_MULTIPLIER;
    }

    private static int roundUpPressure(int pressure) {
	return (int) Math.pow(2, MathUtils.logBase2(pressure) + 1);
    }

    private void applyValidators() {
	if (!inputValidatorGases.isEmpty()) {
	    for (PropertyGasTank tank : inputTanks) {
		tank.setValidator(gasStack -> inputValidatorGases.contains(gasStack.getGas()));
	    }
	}
	if (!outputValidatorGases.isEmpty()) {
	    for (PropertyGasTank tank : outputTanks) {
		tank.setValidator(gasStack -> outputValidatorGases.contains(gasStack.getGas()));
	    }
	}
    }

    private static final class GasRecipeRequirements {

	private final List<Gas> inputGases = new ArrayList<>();
	private final List<Gas> outputGases = new ArrayList<>();
	private final GasTankRequirement input = new GasTankRequirement();
	private final GasTankRequirement output = new GasTankRequirement();
	private final GasTankRequirement biproduct = new GasTankRequirement();
    }

    private static final class GasTankRequirement {

	private double amount;
	private double temperature;
	private int pressure;

	private void include(GasStack stack) {
	    amount = Math.max(amount, stack.getAmount());
	    temperature = Math.max(temperature, stack.getTemperature());
	    pressure = Math.max(pressure, stack.getPressure());
	}
    }

    @Override
    public IComponentType getType() {
	return IComponentType.GasHandler;
    }

    @Override
    public PropertyGasTank[] getInputTanks() {
	return inputTanks;
    }

    @Override
    public PropertyGasTank[] getOutputTanks() {
	return outputTanks;
    }

    private abstract static class AbstractGasTankDispatcher implements IGasHandler {

	protected final PropertyGasTank[] tanks;

	protected AbstractGasTankDispatcher(PropertyGasTank[] tanks) {
	    this.tanks = tanks;
	}

	@Override
	public int getTanks() {
	    return tanks.length;
	}

	@Override
	public GasStack getGasInTank(int tank) {
	    return tanks[tank].getGas();
	}

	@Override
	public int getTankCapacity(int tank) {
	    return tanks[tank].getCapacity();
	}

	@Override
	public int getTankMaxTemperature(int tank) {
	    return tanks[tank].getMaxTemperature();
	}

	@Override
	public int getTankMaxPressure(int tank) {
	    return tanks[tank].getMaxPressure();
	}

	@Override
	public int heat(int tank, int deltaTemperature, GasAction action) {
	    return tanks[tank].heat(tank, deltaTemperature, action);
	}

	@Override
	public int bringPressureTo(int tank, int atm, GasAction action) {
	    return tanks[tank].bringPressureTo(tank, atm, action);
	}

    }

    private static final class InputTankDispatcher extends AbstractGasTankDispatcher {

	private InputTankDispatcher(PropertyGasTank[] tanks) {
	    super(tanks);
	}

	@Override
	public boolean isGasValid(int tank, GasStack gas) {
	    return tanks[tank].isGasValid(gas);
	}

	@Override
	public int fill(GasStack gas, GasAction action) {
	    for (PropertyGasTank tank : tanks) {
		if (tank.getGas().is(gas.getGas()))
		    return tank.fill(gas, action);
	    }
	    for (PropertyGasTank tank : tanks) {
		if (tank.isEmpty())
		    return tank.fill(gas, action);
	    }
	    return 0;
	}

	@Override
	public GasStack drain(GasStack gas, GasAction action) {
	    return GasStack.EMPTY;
	}

	@Override
	public GasStack drain(int maxFill, GasAction action) {
	    return GasStack.EMPTY;
	}

    }

    private static final class OutputTankDispatcher extends AbstractGasTankDispatcher {

	private OutputTankDispatcher(PropertyGasTank[] tanks) {
	    super(tanks);
	}

	@Override
	public boolean isGasValid(int tank, GasStack gas) {
	    return false;
	}

	@Override
	public int fill(GasStack gas, GasAction action) {
	    return 0;
	}

	@Override
	public GasStack drain(GasStack gas, GasAction action) {
	    for (PropertyGasTank tank : tanks) {
		if (tank.getGas().is(gas.getGas()))
		    return tank.drain(gas, action);
	    }
	    return GasStack.EMPTY;
	}

	@Override
	public GasStack drain(int maxFill, GasAction action) {
	    return GasStack.EMPTY;
	}

    }

}
