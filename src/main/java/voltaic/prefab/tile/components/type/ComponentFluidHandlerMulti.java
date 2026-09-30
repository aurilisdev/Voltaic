package voltaic.prefab.tile.components.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import voltaic.api.fluid.PropertyFluidTank;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.utils.IComponentFluidHandler;
import voltaic.prefab.tile.components.utils.RegistryValidatorUtils;
import voltaic.prefab.tile.components.utils.SidedCapabilityHandler;
import voltaic.prefab.utilities.BlockEntityUtils;

/**
 * This class is separate from ComponentFluidHandlerSimple as it has segregated
 * input and output tanks. These tanks are then dispatched when the Capability
 * is requested. The only way to fill an output tank or drain an input tank is
 * through internal tile logic.
 * 
 * This class also allows for RecipeTypes to be used as filters, as Recipes
 * inherently have segregated inputs and outputs.
 * 
 * @author skip999
 *
 */
public class ComponentFluidHandlerMulti implements IComponentFluidHandler {

    private final GenericTile holder;

    private PropertyFluidTank[] inputTanks = {};
    private PropertyFluidTank[] outputTanks = {};

    public Direction[] inputDirections = {};
    public Direction[] outputDirections = {};

    private TagKey<Fluid>[] validInputFluidTags = (TagKey<Fluid>[]) new TagKey<?>[0];
    private TagKey<Fluid>[] validOutputFluidTags = (TagKey<Fluid>[]) new TagKey<?>[0];

    private Fluid[] validInputFluids = {};
    private Fluid[] validOutputFluids = {};
    private final SidedCapabilityHandler<IFluidHandler> sidedHandler = new SidedCapabilityHandler<>(
	    IFluidHandler[]::new);

    private final HashSet<Fluid> inputValidatorFluids = new HashSet<>();
    private final HashSet<Fluid> outputValidatorFluids = new HashSet<>();

    private @Nullable RecipeType<? extends AbstractMaterialRecipe> recipeType;

    public ComponentFluidHandlerMulti(GenericTile holder) {
	this.holder = holder;

	if (!holder.getBlockState().hasProperty(VoltaicBlockStates.FACING))
	    throw new UnsupportedOperationException("The tile " + holder + " must have the FACING direction property!");

    }

    public ComponentFluidHandlerMulti setInputTanks(int count, int... capacity) {
	inputTanks = new PropertyFluidTank[count];
	if (capacity.length < count)
	    throw new UnsupportedOperationException(
		    "The number of capacities does not match the number of input tanks");
	for (int i = 0; i < count; i++) {
	    inputTanks[i] = new PropertyFluidTank(capacity[i], holder, "input" + i);
	}
	return this;
    }

    public ComponentFluidHandlerMulti setOutputTanks(int count, int... capacity) {
	outputTanks = new PropertyFluidTank[count];
	if (capacity.length < count)
	    throw new UnsupportedOperationException(
		    "The number of capacities does not match the number of output tanks");
	for (int i = 0; i < count; i++) {
	    outputTanks[i] = new PropertyFluidTank(capacity[i], holder, "output" + i);
	}
	return this;
    }

    public ComponentFluidHandlerMulti setTanks(int inputCount, int outputCount, int[] inputCapacity,
	    int[] outputCapacity) {
	return setInputTanks(inputCount, inputCapacity).setOutputTanks(outputCount, outputCapacity);
    }

    public ComponentFluidHandlerMulti setInputFluids(Fluid... fluids) {
	validInputFluids = fluids;
	return this;
    }

    public ComponentFluidHandlerMulti setInputFluidTags(TagKey<Fluid>... fluids) {
	validInputFluidTags = fluids;
	return this;
    }

    public ComponentFluidHandlerMulti setOutputFluids(Fluid... fluids) {
	validOutputFluids = fluids;
	return this;
    }

    public ComponentFluidHandlerMulti setOutputFluidTags(TagKey<Fluid>... fluids) {
	validOutputFluidTags = fluids;
	return this;
    }

    public ComponentFluidHandlerMulti setInputDirections(BlockEntityUtils.MachineDirection... directions) {
	inputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	sidedHandler.enableSidedAccess();
	return this;
    }

    public ComponentFluidHandlerMulti setOutputDirections(BlockEntityUtils.MachineDirection... directions) {
	outputDirections = BlockEntityUtils.MachineDirection.toDirectionArray(directions);
	sidedHandler.enableSidedAccess();
	return this;
    }

    public ComponentFluidHandlerMulti setRecipeType(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	this.recipeType = recipeType;
	return this;
    }

    public int tankCount(boolean input) {
	return tanks(input).length;
    }

    public FluidStack getFluidInTank(int tank, boolean input) {
	return tanks(input)[tank].getFluid();
    }

    @Nullable
    public PropertyFluidTank getTankFromFluid(Fluid fluid, boolean isInput) {
	if (isInput) {
	    for (PropertyFluidTank tank : inputTanks) {
		if (tank.getFluid().getFluid().isSame(fluid))
		    return tank;
	    }
	    for (PropertyFluidTank tank : inputTanks) {
		if (tank.isEmpty())
		    return tank;
	    }
	}
	for (PropertyFluidTank tank : outputTanks) {
	    if (tank.getFluid().getFluid().isSame(fluid))
		return tank;
	}
	for (PropertyFluidTank tank : outputTanks) {
	    if (tank.isEmpty())
		return tank;
	}

	return null;
    }

    public int getTankCapacity(int tank, boolean input) {
	return tanks(input)[tank].getCapacity();
    }

    public boolean isFluidValid(int tank, FluidStack stack, boolean input) {
	return tanks(input)[tank].isFluidValid(stack);
    }

    public int fill(int tank, FluidStack resource, FluidAction action, boolean input) {
	return tanks(input)[tank].fill(resource, action);
    }

    public FluidStack drain(int tank, FluidStack resource, FluidAction action, boolean input) {
	return tanks(input)[tank].drain(resource, action);
    }

    public FluidStack drain(int tank, int maxDrain, FluidAction action, boolean input) {
	return tanks(input)[tank].drain(maxDrain, action);
    }

    private PropertyFluidTank[] tanks(boolean input) {
	return input ? inputTanks : outputTanks;
    }

    @Override
    public IComponentType getType() {
	return IComponentType.FluidHandler;
    }

    @Override
    @Nullable
    public IFluidHandler getCapability(@Nullable Direction side, CapabilityInputType inputType) {
	return sidedHandler.getSided(side);
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
	IComponentFluidHandler.super.onLoad(level);

	RecipeType<? extends AbstractMaterialRecipe> recipeType = this.recipeType;
	if (recipeType != null) {
	    configureTanksFromRecipes(recipeType);
	} else {
	    RegistryValidatorUtils.populate(inputValidatorFluids, validInputFluids, validInputFluidTags,
		    BuiltInRegistries.FLUID);
	    RegistryValidatorUtils.populate(outputValidatorFluids, validOutputFluids, validOutputFluidTags,
		    BuiltInRegistries.FLUID);
	}
	applyValidators();
    }

    private void configureTanksFromRecipes(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	FluidRecipeRequirements requirements = collectRecipeRequirements(recipeType);
	inputValidatorFluids.addAll(requirements.inputFluids);
	outputValidatorFluids.addAll(requirements.outputFluids);

	setMinimumCapacity(inputTanks, requirements.maxInputAmount);
	int outputOffset = setPrimaryOutputCapacity(requirements.maxOutputAmount);
	setMinimumCapacity(outputTanks, outputOffset, requirements.maxBiproductAmount);
    }

    private FluidRecipeRequirements collectRecipeRequirements(RecipeType<? extends AbstractMaterialRecipe> recipeType) {
	FluidRecipeRequirements requirements = new FluidRecipeRequirements();
	for (RecipeHolder<VoltaicRecipe> recipeHolder : VoltaicRecipe.findRecipesbyType(recipeType,
		holder.getLevel())) {
	    AbstractMaterialRecipe recipe = (AbstractMaterialRecipe) recipeHolder.value();
	    for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
		ingredient.getMatchingFluids().forEach(fluid -> requirements.inputFluids.add(fluid.getFluid()));
		requirements.maxInputAmount = Math.max(requirements.maxInputAmount, ingredient.getAmount());
	    }

	    FluidStack output = recipe.getFluidRecipeOutput();
	    requirements.outputFluids.add(output.getFluid());
	    requirements.maxOutputAmount = Math.max(requirements.maxOutputAmount, output.getAmount());

	    if (recipe.hasFluidBiproducts()) {
		for (FluidStack biproduct : recipe.getFullFluidBiStacks()) {
		    requirements.outputFluids.add(biproduct.getFluid());
		    requirements.maxBiproductAmount = Math.max(requirements.maxBiproductAmount, biproduct.getAmount());
		}
	    }
	}
	return requirements;
    }

    private static void setMinimumCapacity(PropertyFluidTank[] tanks, int requiredAmount) {
	setMinimumCapacity(tanks, 0, requiredAmount);
    }

    private static void setMinimumCapacity(PropertyFluidTank[] tanks, int startIndex, int requiredAmount) {
	if (requiredAmount <= 0)
	    return;

	int capacity = roundUpTankCapacity(requiredAmount);
	for (int i = startIndex; i < tanks.length; i++) {
	    if (tanks[i].getCapacity() < capacity) {
		tanks[i].setCapacity(capacity);
	    }
	}
    }

    private int setPrimaryOutputCapacity(int requiredAmount) {
	if (requiredAmount <= 0)
	    return 0;

	int capacity = roundUpTankCapacity(requiredAmount);
	if (outputTanks[0].getCapacity() < capacity) {
	    outputTanks[0].setCapacity(capacity);
	}
	return 1;
    }

    private static int roundUpTankCapacity(int amount) {
	return amount / TANK_MULTIPLER * TANK_MULTIPLER + TANK_MULTIPLER;
    }

    private void applyValidators() {
	if (!inputValidatorFluids.isEmpty()) {
	    for (PropertyFluidTank tank : inputTanks) {
		tank.setValidator(fluidStack -> inputValidatorFluids.contains(fluidStack.getFluid()));
	    }
	}
	if (!outputValidatorFluids.isEmpty()) {
	    for (PropertyFluidTank tank : outputTanks) {
		tank.setValidator(fluidStack -> outputValidatorFluids.contains(fluidStack.getFluid()));
	    }
	}
    }

    private static final class FluidRecipeRequirements {

	private final List<Fluid> inputFluids = new ArrayList<>();
	private final List<Fluid> outputFluids = new ArrayList<>();
	private int maxInputAmount;
	private int maxOutputAmount;
	private int maxBiproductAmount;
    }

    @Override
    public PropertyFluidTank[] getInputTanks() {
	return inputTanks;
    }

    @Override
    public PropertyFluidTank[] getOutputTanks() {
	return outputTanks;
    }

    private abstract static class AbstractFluidTankDispatcher implements IFluidHandler {

	protected final PropertyFluidTank[] tanks;

	protected AbstractFluidTankDispatcher(PropertyFluidTank[] tanks) {
	    this.tanks = tanks;
	}

	@Override
	public int getTanks() {
	    return tanks.length;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
	    if (tank >= getTanks())
		return FluidStack.EMPTY;
	    return tanks[tank].getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
	    if (tank >= getTanks())
		return 0;
	    return tanks[tank].getCapacity();
	}

    }

    private static final class InputTankDispatcher extends AbstractFluidTankDispatcher {

	private InputTankDispatcher(PropertyFluidTank[] tanks) {
	    super(tanks);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
	    if (tank >= getTanks())
		return false;
	    return tanks[tank].isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
	    for (PropertyFluidTank tank : tanks) {
		if (tank.getFluid().is(resource.getFluid()))
		    return tank.fill(resource, action);
	    }
	    for (PropertyFluidTank tank : tanks) {
		if (tank.isEmpty())
		    return tank.fill(resource, action);
	    }
	    return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
	    return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
	    return FluidStack.EMPTY;
	}

    }

    private static final class OutputTankDispatcher extends AbstractFluidTankDispatcher {

	private OutputTankDispatcher(PropertyFluidTank[] tanks) {
	    super(tanks);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
	    return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
	    return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
	    for (PropertyFluidTank tank : tanks) {
		if (tank.getFluid().is(resource.getFluid()))
		    return tank.drain(resource, action);
	    }
	    return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
	    return FluidStack.EMPTY;
	}

    }

    /**
     * A modified variant of the ComponentFluidHandlerMulti that allows for inputs
     * and outputs to share the same side
     * 
     * Note, the calling tile is responsible for providing the non-null
     * CapabilityInputType
     * 
     * @author skip999
     *
     */
    public static class ComponentFluidHandlerMultiBiDirec extends ComponentFluidHandlerMulti {

	private IFluidHandler[] inputSidedOptionals = new IFluidHandler[6];
	private IFluidHandler[] outputSidedOptionals = new IFluidHandler[6];

	public ComponentFluidHandlerMultiBiDirec(GenericTile holder) {
	    super(holder);
	}

	@Override
	@Nullable
	public IFluidHandler getCapability(@Nullable Direction side, CapabilityInputType inputType) {
	    if (side == null)
		return null;

	    if (inputType == CapabilityInputType.INPUT)
		return inputSidedOptionals[side.ordinal()];
	    return outputSidedOptionals[side.ordinal()];

	}

	@Override
	public void refresh(Level level) {
	    refreshSidedOptionals(level, super.holder.getFacing());
	}

	@Override
	public void refreshIfUpdate(Level level, BlockState oldState, BlockState newState) {
	    if (oldState.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING)) {
		refreshSidedOptionals(level, newState.getValue(VoltaicBlockStates.FACING));
	    }
	}

	private void refreshSidedOptionals(Level level, Direction facing) {
	    level.invalidateCapabilities(super.holder.getBlockPos());

	    inputSidedOptionals = new IFluidHandler[6];
	    outputSidedOptionals = new IFluidHandler[6];

	    IFluidHandler inputOptional = new InputTankDispatcher(super.inputTanks);

	    for (Direction dir : inputDirections) {
		inputSidedOptionals[BlockEntityUtils.getRelativeSide(facing, dir).ordinal()] = inputOptional;
	    }
	    IFluidHandler outputOptional = new OutputTankDispatcher(super.outputTanks);

	    for (Direction dir : outputDirections) {
		outputSidedOptionals[BlockEntityUtils.getRelativeSide(facing, dir).ordinal()] = outputOptional;
	    }
	}

    }

}
