package voltaic.prefab.tile.components.type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.GasTank;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.common.network.utils.FluidUtilities;
import voltaic.common.network.utils.GasUtilities;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.categories.fluid2fluid.Fluid2FluidRecipe;
import voltaic.common.recipe.categories.fluid2gas.Fluid2GasRecipe;
import voltaic.common.recipe.categories.fluid2item.Fluid2ItemRecipe;
import voltaic.common.recipe.categories.fluiditem2fluid.FluidItem2FluidRecipe;
import voltaic.common.recipe.categories.fluiditem2gas.FluidItem2GasRecipe;
import voltaic.common.recipe.categories.fluiditem2item.FluidItem2ItemRecipe;
import voltaic.common.recipe.categories.item2fluid.Item2FluidRecipe;
import voltaic.common.recipe.categories.item2item.Item2ItemRecipe;
import voltaic.common.recipe.recipeutils.AbstractMaterialRecipe;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.properties.PropertyManager;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ArrayProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.QuadConsumer;
import voltaic.registers.VoltaicDataComponentTypes;

public class ComponentProcessor implements IComponent {

    private final GenericTile holder;

    public final SingleProperty<Double> operatingSpeed;
    public final ArrayProperty<Double> operatingTicks;
    public final ArrayProperty<Double> usage;
    public final ArrayProperty<Double> requiredTicks;
    private TriPredicate<ComponentProcessor, Level, Integer> canProcess = (component, level, index) -> false;
    private TriConsumer<ComponentProcessor, Level, Integer> process = (component, level, index) -> {};
    private QuadConsumer<ComponentProcessor, Level, List<Integer>, Boolean> failed = (component, level,
	    failedProcessors, anySuceeded) -> {};
    private final int numProcessors;

    private List<RecipeHolder<VoltaicRecipe>> cachedRecipes = new ArrayList<>();
    private final VoltaicRecipe[] activeRecipies;
    private double storedXp = 0.0;

    private final ArrayProperty<Boolean> isActive;
    private final ArrayProperty<Boolean> shouldKeepProgress;

    public ComponentProcessor(GenericTile source) {
	this(source, 1);
    }

    public ComponentProcessor(GenericTile source, int totalProcessors) {
	holder = source;
	PropertyManager manager = holder.getPropertyManager();
	numProcessors = totalProcessors;
	operatingSpeed = holder.property(new SingleProperty<>(manager, PropertyTypes.DOUBLE, "operatingSpeed", 1.0));
	operatingTicks = holder.property(new ArrayProperty<>(manager, PropertyTypes.DOUBLE_ARRAY, "operatingTicks",
		MathUtils.fillArr(new Double[totalProcessors], 0.0)));
	usage = holder.property(new ArrayProperty<>(manager, PropertyTypes.DOUBLE_ARRAY, "recipeUsage",
		MathUtils.fillArr(new Double[totalProcessors], 0.0)));
	requiredTicks = holder.property(new ArrayProperty<>(manager, PropertyTypes.DOUBLE_ARRAY, "requiredTicks",
		MathUtils.fillArr(new Double[totalProcessors], 0.0)));
	isActive = holder.property(new ArrayProperty<>(manager, PropertyTypes.BOOLEAN_ARRAY, "isprocactive",
		MathUtils.fillArr(new Boolean[totalProcessors], false)));
	shouldKeepProgress = holder.property(new ArrayProperty<>(manager, PropertyTypes.BOOLEAN_ARRAY,
		"shouldprockeepprogress", MathUtils.fillArr(new Boolean[totalProcessors], false)));
	activeRecipies = new VoltaicRecipe[totalProcessors];

	if (!holder.hasComponent(IComponentType.Inventory))
	    throw new UnsupportedOperationException(
		    "You need to implement an inventory component to use the processor component!");
	if (!holder.hasComponent(IComponentType.Tickable))
	    throw new UnsupportedOperationException(
		    "You need to implement a tickable component to use the processor component!");
	if (!holder.hasComponent(IComponentType.Electrodynamic))
	    throw new UnsupportedOperationException(
		    "You need to implement an electrodynamic component to use the processor component!");

	holder.<ComponentTickable>getComponent(IComponentType.Tickable).get().tickServer(this::tickServer);
    }

    @Override
    public GenericTile getHolder() {
	return holder;
    }

    private void tickServer(Level level, ComponentTickable tickable) {
	ComponentElectrodynamic electro = holder.<ComponentElectrodynamic>getComponent(IComponentType.Electrodynamic)
		.get();
	ComponentInventory inv = holder.<ComponentInventory>getComponent(IComponentType.Inventory).get();

	for (ItemStack stack : inv.getUpgradeContents()) {
	    if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade upgrade && !upgrade.subtype.isEmpty) {
		for (int i = 0; i < stack.getCount(); i++) {
		    for (int j = 0; j < numProcessors; j++) {
			upgrade.subtype.applyUpgrade.accept(getHolder(), stack, j);
		    }
		}
	    }
	}

	boolean suceeded = false;
	List<Integer> failure = new ArrayList<>();

	for (int procNumber = 0; procNumber < numProcessors; procNumber++) {
	    if (canProcess.test(this, level, procNumber)) {
		isActive.setValue(true, procNumber);
		operatingTicks.setValue(operatingTicks.getValue()[procNumber] + operatingSpeed.getValue(), procNumber);
		if (operatingTicks.getValue()[procNumber] >= requiredTicks.getValue()[procNumber]) {
		    process.accept(this, level, procNumber);
		    suceeded = true;
		    operatingTicks.setValue(0.0, procNumber);
		}
		if (holder.hasComponent(IComponentType.Electrodynamic)) {
		    electro.joules(
			    electro.getJoulesStored() - usage.getValue()[procNumber] * operatingSpeed.getValue());
		}
	    } else if (isActive(procNumber)) {
		isActive.setValue(false, procNumber);
		if (!shouldKeepProgress.getValue()[procNumber]) {
		    operatingTicks.setValue(0.0, procNumber);
		}
		failure.add(procNumber);
	    } else {
		operatingTicks.setValue(0.0, procNumber);
	    }
	}

	electro.maxJoules(getTotalUsage() * operatingSpeed.getValue() * 10);

	if (!failure.isEmpty()) {
	    failed.accept(this, level, failure, suceeded);
	}

    }

    public ComponentProcessor process(TriConsumer<ComponentProcessor, Level, Integer> process) {
	this.process = process;
	return this;
    }

    public ComponentProcessor failed(QuadConsumer<ComponentProcessor, Level, List<Integer>, Boolean> failed) {
	this.failed = failed;
	return this;
    }

    public ComponentProcessor canProcess(TriPredicate<ComponentProcessor, Level, Integer> canProcess) {
	this.canProcess = canProcess;
	return this;
    }

    public ComponentProcessor usageForAll(double usage) {
	for (int index = 0; index < numProcessors; index++) {
	    this.usage.setValue(usage, index);
	}
	return this;
    }

    public ComponentProcessor usage(double usage, int index) {
	this.usage.setValue(usage, index);
	return this;
    }

    public double getUsage(int index) {
	return usage.getValue()[index] * operatingSpeed.getValue();
    }

    public double getTotalUsage() {
	double total = 0.0;
	for (double use : usage.getValue()) {
	    total += use;
	}
	return total;
    }

    public ComponentProcessor requiredTicks(long requiredTicks, int index) {
	this.requiredTicks.setValue((double) requiredTicks, index);
	return this;
    }

    public ComponentProcessor requiredTicksForAll(long requiredTicks) {
	for (int index = 0; index < numProcessors; index++) {
	    this.requiredTicks.setValue((double) requiredTicks, index);
	}
	return this;
    }

    public int getProcessorCount() {
	return numProcessors;
    }

    @Override
    public IComponentType getType() {
	return IComponentType.Processor;
    }

    public ComponentProcessor consumeBucket() {
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	FluidUtilities.drainItem(holder, handler.getInputTanks());
	return this;
    }

    public ComponentProcessor dispenseBucket() {
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	FluidUtilities.fillItem(holder, handler.getOutputTanks());
	return this;
    }

    public ComponentProcessor outputToFluidPipe() {
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	FluidUtilities.outputToPipe(holder, handler.getOutputTanks(), handler.outputDirections);
	return this;
    }

    public ComponentProcessor consumeGasCylinder() {
	ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	GasUtilities.drainItem(holder, handler.getInputTanks());
	return this;
    }

    public ComponentProcessor dispenseGasCylinder() {
	ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	GasUtilities.fillItem(holder, handler.getOutputTanks());
	return this;
    }

    public ComponentProcessor outputToGasPipe() {
	ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	GasUtilities.outputToPipe(holder, handler.getOutputTanks(), handler.outputDirections);
	return this;
    }

    public @Nullable VoltaicRecipe getRecipe(int index) {
	return activeRecipies[index];
    }

    public void setRecipe(VoltaicRecipe recipe, int index) {
	activeRecipies[index] = recipe;
    }

    public void setStoredXp(double val) {
	storedXp = val;
    }

    public double getStoredXp() {
	return storedXp;
    }

    public boolean isActive(int index) {
	return isActive.getValue()[index];
    }

    public boolean isAnyActive() {
	for (int i = 0; i < numProcessors; i++) {
	    if (isActive(i))
		return true;
	}
	return false;
    }

    public int getTotalActive() {
	int count = 0;
	for (int i = 0; i < numProcessors; i++) {
	    if (isActive(i)) {
		count++;
	    }
	}
	return count;
    }

    public void setShouldKeepProgress(boolean should, int index) {
	shouldKeepProgress.setValue(should, index);
    }

    private enum MainOutput {
	ITEM,
	FLUID,
	GAS
    }

    public boolean canProcessItem2ItemRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, Item2ItemRecipe.class, MainOutput.ITEM,
		(recipe, processor) -> hasRoomForItemOutput(recipe.getItemRecipeOutput(), processor));
    }

    public boolean canProcessFluid2ItemRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, Fluid2ItemRecipe.class, MainOutput.ITEM,
		(recipe, processor) -> hasRoomForItemOutput(recipe.getItemRecipeOutput(), processor));
    }

    public boolean canProcessFluid2FluidRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, Fluid2FluidRecipe.class, MainOutput.FLUID,
		(recipe, processor) -> hasRoomForFluidOutput(recipe.getFluidRecipeOutput()));
    }

    public boolean canProcessItem2FluidRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, Item2FluidRecipe.class, MainOutput.FLUID,
		(recipe, processor) -> hasRoomForFluidOutput(recipe.getFluidRecipeOutput()));
    }

    public boolean canProcessFluidItem2FluidRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, FluidItem2FluidRecipe.class, MainOutput.FLUID,
		(recipe, processor) -> hasRoomForFluidOutput(recipe.getFluidRecipeOutput()));
    }

    public boolean canProcessFluidItem2ItemRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, FluidItem2ItemRecipe.class, MainOutput.ITEM,
		(recipe, processor) -> hasRoomForItemOutput(recipe.getItemRecipeOutput(), processor));
    }

    public boolean canProcessFluid2GasRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, Fluid2GasRecipe.class, MainOutput.GAS,
		(recipe, processor) -> hasRoomForGasOutput(recipe.getGasRecipeOutput()));
    }

    public boolean canProcessFluidItem2GasRecipe(Level level, int procNumber, RecipeType<?> typeIn) {
	return canProcessRecipe(procNumber, typeIn, FluidItem2GasRecipe.class, MainOutput.GAS,
		(recipe, processor) -> hasRoomForGasOutput(recipe.getGasRecipeOutput()));
    }

    public boolean canProcessMaterialRecipe(AbstractMaterialRecipe recipe, int procNumber, int fluidBiproductOffset,
	    int gasBiproductOffset) {
	ComponentElectrodynamic electro = holder.requireComponent(IComponentType.Electrodynamic);
	if (electro.getJoulesStored() < getUsage(procNumber))
	    return false;

	ItemStack itemOutput = recipe.getItemRecipeOutput();
	if (!itemOutput.isEmpty() && !hasRoomForItemOutput(itemOutput, procNumber))
	    return false;

	FluidStack fluidOutput = recipe.getFluidRecipeOutput();
	if (!fluidOutput.isEmpty() && !hasRoomForFluidOutput(fluidOutput))
	    return false;

	GasStack gasOutput = recipe.getGasRecipeOutput();
	if (!gasOutput.isEmpty() && !hasRoomForGasOutput(gasOutput))
	    return false;

	return hasRoomForBiproducts(recipe, procNumber, fluidBiproductOffset, gasBiproductOffset);

    }

    private <R extends VoltaicRecipe> boolean canProcessRecipe(int procNumber, RecipeType<?> typeIn,
	    Class<R> recipeClass, MainOutput mainOutput, BiPredicate<R, Integer> outputCheck) {
	R recipe = prepareRecipe(procNumber, typeIn, recipeClass);
	if (recipe == null)
	    return false;
	ComponentElectrodynamic electro = holder.requireComponent(IComponentType.Electrodynamic);
	return electro.getJoulesStored() >= getUsage(procNumber) && outputCheck.test(recipe, procNumber)
		&& hasRoomForBiproducts(recipe, procNumber, mainOutput);
    }

    @Nullable
    public <R extends VoltaicRecipe> R prepareRecipe(int procNumber, RecipeType<?> typeIn, Class<R> recipeClass) {
	VoltaicRecipe recipe;
	if (checkExistingRecipe(procNumber)) {
	    setShouldKeepProgress(true, procNumber);
	    recipe = activeRecipies[procNumber];
	} else {
	    setShouldKeepProgress(false, procNumber);
	    operatingTicks.setValue(0.0, procNumber);
	    recipe = getRecipe(typeIn, procNumber);
	}
	if (recipe == null)
	    return null;
	R typedRecipe = requireRecipeType(recipe, recipeClass, procNumber);
	setRecipe(typedRecipe, procNumber);
	requiredTicks.setValue((double) typedRecipe.getTicks(), procNumber);
	usage.setValue(typedRecipe.getUsagePerTick(), procNumber);
	return typedRecipe;
    }

    @Nullable
    private <R extends VoltaicRecipe> R getActiveRecipe(int procNumber, Class<R> recipeClass) {
	VoltaicRecipe recipe = getRecipe(procNumber);
	return recipe == null ? null : requireRecipeType(recipe, recipeClass, procNumber);
    }

    private static <R extends VoltaicRecipe> R requireRecipeType(VoltaicRecipe recipe, Class<R> recipeClass,
	    int procNumber) {
	if (!recipeClass.isInstance(recipe))
	    throw new IllegalStateException("Processor " + procNumber + " has " + recipe.getClass().getSimpleName()
		    + " active, expected " + recipeClass.getSimpleName());
	return recipeClass.cast(recipe);
    }

    private boolean hasRoomForItemOutput(ItemStack result, int procNumber) {
	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ItemStack output = inv.getOutputsForProcessor(procNumber).get(0);
	if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, result))
	    return false;
	int capacity = output.isEmpty() ? result.getMaxStackSize() : output.getMaxStackSize();
	return output.getCount() + result.getCount() <= capacity;
    }

    private boolean hasRoomForFluidOutput(FluidStack result) {
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	FluidTank[] tanks = handler.getOutputTanks();
	return tanks.length > 0 && tanks[0].fill(result, FluidAction.SIMULATE) >= result.getAmount();
    }

    private boolean hasRoomForGasOutput(GasStack result) {
	ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	GasTank[] tanks = handler.getOutputTanks();
	return tanks.length > 0 && tanks[0].fill(result, GasAction.SIMULATE) >= result.getAmount();
    }

    private boolean hasRoomForBiproducts(VoltaicRecipe recipe, int procNumber, MainOutput mainOutput) {
	return hasRoomForBiproducts(recipe, procNumber, mainOutput == MainOutput.FLUID ? 1 : 0,
		mainOutput == MainOutput.GAS ? 1 : 0);
    }

    private boolean hasRoomForBiproducts(VoltaicRecipe recipe, int procNumber, int fluidOffset, int gasOffset) {
	if (recipe.hasItemBiproducts()) {
	    ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	    if (!hasRoomForItemBiproducts(inv.getBiprodsForProcessor(procNumber), recipe.getFullItemBiStacks()))
		return false;
	}

	if (recipe.hasFluidBiproducts()) {
	    ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	    if (!hasRoomForFluidBiproducts(handler.getOutputTanks(), recipe.getFullFluidBiStacks(), fluidOffset))
		return false;
	}

	if (recipe.hasGasBiproducts()) {
	    ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);

	    if (!hasRoomForGasBiproducts(handler.getOutputTanks(), recipe.getFullGasBiStacks(), gasOffset))
		return false;
	}

	return true;
    }

    public static boolean hasRoomForItemBiproducts(List<ItemStack> slots, ItemStack[] biproducts) {
	if (slots.size() < biproducts.length)
	    return false;
	for (int i = 0; i < biproducts.length; i++) {
	    ItemStack slot = slots.get(i);
	    ItemStack biproduct = biproducts[i];
	    if (!slot.isEmpty() && !ItemStack.isSameItemSameComponents(slot, biproduct))
		return false;
	    int capacity = slot.isEmpty() ? biproduct.getMaxStackSize() : slot.getMaxStackSize();
	    if (slot.getCount() + biproduct.getCount() > capacity)
		return false;
	}
	return true;
    }

    public static boolean hasRoomForFluidBiproducts(FluidTank[] tanks, FluidStack[] biproducts, int offset) {
	if (tanks.length < offset + biproducts.length)
	    return false;
	for (int i = 0; i < biproducts.length; i++) {
	    FluidStack biproduct = biproducts[i];
	    if (tanks[i + offset].fill(biproduct, FluidAction.SIMULATE) < biproduct.getAmount())
		return false;
	}
	return true;
    }

    public static boolean hasRoomForGasBiproducts(GasTank[] tanks, GasStack[] biproducts, int offset) {
	if (tanks.length < offset + biproducts.length)
	    return false;
	for (int i = 0; i < biproducts.length; i++) {
	    GasStack biproduct = biproducts[i];
	    if (tanks[i + offset].fill(biproduct, GasAction.SIMULATE) < biproduct.getAmount())
		return false;
	}
	return true;
    }

    public void processItem2ItemRecipe(Level level, int procNumber) {
	Item2ItemRecipe recipe = getActiveRecipe(procNumber, Item2ItemRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	int[] itemAmounts = recipe.getCountedIngredients().stream().mapToInt(CountableIngredient::getStackSize)
		.toArray();
	List<Integer> itemArrangement = requireItemArrangement(recipe, procNumber, itemAmounts.length, inv);
	processRecipe(recipe, procNumber, MainOutput.ITEM,
		() -> outputItem(inv, procNumber, recipe.getItemRecipeOutput()),
		() -> consumeItems(inv, procNumber, itemArrangement, itemAmounts));
    }

    public void processItem2FluidRecipe(Level level, int procNumber) {
	Item2FluidRecipe recipe = getActiveRecipe(procNumber, Item2FluidRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	int[] itemAmounts = recipe.getCountedIngredients().stream().mapToInt(CountableIngredient::getStackSize)
		.toArray();
	List<Integer> itemArrangement = requireItemArrangement(recipe, procNumber, itemAmounts.length, inv);
	processRecipe(recipe, procNumber, MainOutput.FLUID, () -> outputFluid(handler, recipe.getFluidRecipeOutput()),
		() -> consumeItems(inv, procNumber, itemArrangement, itemAmounts));
    }

    public void processFluidItem2FluidRecipe(Level level, int procNumber) {
	FluidItem2FluidRecipe recipe = getActiveRecipe(procNumber, FluidItem2FluidRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	int[] itemAmounts = recipe.getCountedIngredients().stream().mapToInt(CountableIngredient::getStackSize)
		.toArray();
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> itemArrangement = requireItemArrangement(recipe, procNumber, itemAmounts.length, inv);
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(), handler);
	processRecipe(recipe, procNumber, MainOutput.FLUID, () -> outputFluid(handler, recipe.getFluidRecipeOutput()),
		() -> {
		    consumeItems(inv, procNumber, itemArrangement, itemAmounts);
		    consumeFluids(handler, fluidArrangement, fluidIngredients);
		});
    }

    public void processFluidItem2ItemRecipe(Level level, int procNumber) {
	FluidItem2ItemRecipe recipe = getActiveRecipe(procNumber, FluidItem2ItemRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	int[] itemAmounts = recipe.getCountedIngredients().stream().mapToInt(CountableIngredient::getStackSize)
		.toArray();
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> itemArrangement = requireItemArrangement(recipe, procNumber, itemAmounts.length, inv);
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(), handler);
	processRecipe(recipe, procNumber, MainOutput.ITEM,
		() -> outputItem(inv, procNumber, recipe.getItemRecipeOutput()), () -> {
		    consumeItems(inv, procNumber, itemArrangement, itemAmounts);
		    consumeFluids(handler, fluidArrangement, fluidIngredients);
		});
    }

    public void processFluid2ItemRecipe(Level level, int procNumber) {
	Fluid2ItemRecipe recipe = getActiveRecipe(procNumber, Fluid2ItemRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(), handler);
	processRecipe(recipe, procNumber, MainOutput.ITEM,
		() -> outputItem(inv, procNumber, recipe.getItemRecipeOutput()),
		() -> consumeFluids(handler, fluidArrangement, fluidIngredients));
    }

    public void processFluid2FluidRecipe(Level level, int procNumber) {
	Fluid2FluidRecipe recipe = getActiveRecipe(procNumber, Fluid2FluidRecipe.class);
	if (recipe == null)
	    return;
	ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(), handler);
	processRecipe(recipe, procNumber, MainOutput.FLUID, () -> outputFluid(handler, recipe.getFluidRecipeOutput()),
		() -> consumeFluids(handler, fluidArrangement, fluidIngredients));
    }

    public void processFluid2GasRecipe(Level level, int procNumber) {
	Fluid2GasRecipe recipe = getActiveRecipe(procNumber, Fluid2GasRecipe.class);
	if (recipe == null)
	    return;

	ComponentFluidHandlerMulti fluidHandler = holder.requireComponent(IComponentType.FluidHandler);
	ComponentGasHandlerMulti gasHandler = holder.requireComponent(IComponentType.GasHandler);
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(),
		fluidHandler);
	processRecipe(recipe, procNumber, MainOutput.GAS, () -> outputGas(gasHandler, recipe.getGasRecipeOutput()),
		() -> consumeFluids(fluidHandler, fluidArrangement, fluidIngredients));
    }

    public void processFluidItem2GasRecipe(Level level, int procNumber) {
	FluidItem2GasRecipe recipe = getActiveRecipe(procNumber, FluidItem2GasRecipe.class);
	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	ComponentFluidHandlerMulti fluidHandler = holder.requireComponent(IComponentType.FluidHandler);
	ComponentGasHandlerMulti gasHandler = holder.requireComponent(IComponentType.GasHandler);
	int[] itemAmounts = recipe.getCountedIngredients().stream().mapToInt(CountableIngredient::getStackSize)
		.toArray();
	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
	List<Integer> itemArrangement = requireItemArrangement(recipe, procNumber, itemAmounts.length, inv);
	List<Integer> fluidArrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(),
		fluidHandler);
	processRecipe(recipe, procNumber, MainOutput.GAS, () -> outputGas(gasHandler, recipe.getGasRecipeOutput()),
		() -> {
		    consumeItems(inv, procNumber, itemArrangement, itemAmounts);
		    consumeFluids(fluidHandler, fluidArrangement, fluidIngredients);
		});
    }

    public <R extends AbstractMaterialRecipe> void processMaterialRecipe(int procNumber, Class<R> recipeClass,
	    int fluidBiproductOffset, int gasBiproductOffset) {

	R recipe = getActiveRecipe(procNumber, recipeClass);

	if (recipe == null)
	    return;

	ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);

	outputBiproducts(recipe, procNumber, fluidBiproductOffset, gasBiproductOffset);

	ItemStack itemOutput = recipe.getItemRecipeOutput();

	if (!itemOutput.isEmpty())
	    outputItem(inv, procNumber, itemOutput);

	FluidStack fluidOutput = recipe.getFluidRecipeOutput();

	if (!fluidOutput.isEmpty()) {
	    ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	    outputFluid(handler, fluidOutput);
	}

	GasStack gasOutput = recipe.getGasRecipeOutput();

	if (!gasOutput.isEmpty()) {
	    ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	    outputGas(handler, gasOutput);
	}

	List<CountableIngredient> itemIngredients = recipe.getCountedIngredients();

	if (!itemIngredients.isEmpty()) {
	    int[] amounts = itemIngredients.stream().mapToInt(CountableIngredient::getStackSize).toArray();
	    List<Integer> arrangement = requireItemArrangement(recipe, procNumber, amounts.length, inv);

	    consumeItems(inv, procNumber, arrangement, amounts);
	}

	List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();

	if (!fluidIngredients.isEmpty()) {
	    ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	    List<Integer> arrangement = requireFluidArrangement(recipe, procNumber, fluidIngredients.size(), handler);

	    consumeFluids(handler, arrangement, fluidIngredients);
	}

	List<GasIngredient> gasIngredients = recipe.getGasIngredients();

	if (!gasIngredients.isEmpty()) {
	    ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	    List<Integer> arrangement = requireGasArrangement(recipe, procNumber, gasIngredients.size(), handler);

	    consumeGases(handler, arrangement, gasIngredients);
	}

	dispenseExperience(inv, recipe.getXp());
	setChanged();

    }

    private void processRecipe(VoltaicRecipe recipe, int procNumber, MainOutput mainOutput, Runnable output,
	    Runnable consumeInputs) {
	outputBiproducts(recipe, procNumber, mainOutput);
	output.run();
	consumeInputs.run();
	dispenseExperience(holder.requireComponent(IComponentType.Inventory), recipe.getXp());
	setChanged();
    }

    private void outputBiproducts(VoltaicRecipe recipe, int procNumber, MainOutput mainOutput) {
	outputBiproducts(recipe, procNumber, mainOutput == MainOutput.FLUID ? 1 : 0,
		mainOutput == MainOutput.GAS ? 1 : 0);
    }

    private void outputBiproducts(VoltaicRecipe recipe, int procNumber, int fluidOffset, int gasOffset) {
	if (recipe.hasItemBiproducts()) {
	    ComponentInventory inv = holder.requireComponent(IComponentType.Inventory);
	    outputItemBiproducts(inv, procNumber, recipe.getItemBiproducts());
	}

	if (recipe.hasFluidBiproducts()) {
	    ComponentFluidHandlerMulti handler = holder.requireComponent(IComponentType.FluidHandler);
	    outputFluidBiproducts(handler.getOutputTanks(), recipe.getFluidBiproducts(), fluidOffset);
	}

	if (recipe.hasGasBiproducts()) {
	    ComponentGasHandlerMulti handler = holder.requireComponent(IComponentType.GasHandler);
	    outputGasBiproducts(handler.getOutputTanks(), recipe.getGasBiproducts(), gasOffset);
	}
    }

    private static void outputItemBiproducts(ComponentInventory inv, int procNumber, List<ProbableItem> biproducts) {
	List<Integer> slots = inv.getBiprodSlotsForProcessor(procNumber);
	for (int i = 0; i < biproducts.size(); i++) {
	    ItemStack rolled = biproducts.get(i).roll();
	    if (rolled.isEmpty())
		continue;
	    int slot = slots.get(i);
	    ItemStack current = inv.getItem(slot);
	    if (current.isEmpty())
		inv.setItem(slot, rolled.copy());
	    else {
		current.grow(rolled.getCount());
		inv.setItem(slot, current);
	    }
	}
    }

    private static void outputFluidBiproducts(FluidTank[] tanks, List<ProbableFluid> biproducts, int offset) {
	for (int i = 0; i < biproducts.size(); i++)
	    tanks[i + offset].fill(biproducts.get(i).roll(), FluidAction.EXECUTE);
    }

    private static void outputGasBiproducts(GasTank[] tanks, List<ProbableGas> biproducts, int offset) {
	for (int i = 0; i < biproducts.size(); i++)
	    tanks[i + offset].fill(biproducts.get(i).roll(), GasAction.EXECUTE);
    }

    private static void outputItem(ComponentInventory inv, int procNumber, ItemStack result) {
	int outputSlot = inv.getOutputSlotsForProcessor(procNumber).get(0);
	ItemStack output = inv.getItem(outputSlot);
	if (output.isEmpty())
	    inv.setItem(outputSlot, result.copy());
	else {
	    output.grow(result.getCount());
	    inv.setItem(outputSlot, output);
	}
    }

    private static void outputFluid(ComponentFluidHandlerMulti handler, FluidStack result) {
	handler.getOutputTanks()[0].fill(result, FluidAction.EXECUTE);
    }

    private static void outputGas(ComponentGasHandlerMulti handler, GasStack result) {
	handler.getOutputTanks()[0].fill(result, GasAction.EXECUTE);
    }

    private static void consumeItems(ComponentInventory inv, int procNumber, List<Integer> arrangement, int[] amounts) {
	List<Integer> inputSlots = inv.getInputSlotsForProcessor(procNumber);
	for (int i = 0; i < amounts.length; i++) {
	    int slot = inputSlots.get(arrangement.get(i));
	    ItemStack stack = inv.getItem(slot);
	    stack.shrink(amounts[i]);
	    inv.setItem(slot, stack);
	}
    }

    private static void consumeFluids(ComponentFluidHandlerMulti handler, List<Integer> arrangement,
	    List<FluidIngredient> ingredients) {
	FluidTank[] tanks = handler.getInputTanks();
	for (int i = 0; i < ingredients.size(); i++)
	    tanks[arrangement.get(i)].drain(ingredients.get(i).getAmount(), FluidAction.EXECUTE);
    }

    private static void consumeGases(ComponentGasHandlerMulti handler, List<Integer> arrangement,
	    List<GasIngredient> ingredients) {
	GasTank[] tanks = handler.getInputTanks();
	for (int i = 0; i < ingredients.size(); i++)
	    tanks[arrangement.get(i)].drain(ingredients.get(i).getGasStack().getAmount(), GasAction.EXECUTE);

    }

    private static List<Integer> requireItemArrangement(VoltaicRecipe recipe, int procNumber, int ingredientCount,
	    ComponentInventory inv) {
	return requireArrangement(recipe.getItemArrangment(procNumber), ingredientCount,
		inv.getInputSlotsForProcessor(procNumber).size(), "item", procNumber);
    }

    private static List<Integer> requireFluidArrangement(VoltaicRecipe recipe, int procNumber, int ingredientCount,
	    ComponentFluidHandlerMulti handler) {
	return requireArrangement(recipe.getFluidArrangement(), ingredientCount, handler.getInputTanks().length,
		"fluid", procNumber);
    }

    private static List<Integer> requireGasArrangement(VoltaicRecipe recipe, int procNumber, int ingredientCount,
	    ComponentGasHandlerMulti handler) {
	return requireArrangement(recipe.getGasArrangement(), ingredientCount, handler.getInputTanks().length, "gas",
		procNumber);

    }

    private static List<Integer> requireArrangement(@Nullable List<Integer> arrangement, int ingredientCount,
	    int inputCount, String inputType, int procNumber) {
	if (arrangement == null)
	    throw new IllegalStateException("Recipe has no " + inputType + " arrangement for processor " + procNumber);
	if (arrangement.size() != ingredientCount)
	    throw new IllegalStateException("Recipe has " + arrangement.size() + " arranged " + inputType
		    + " inputs but requires " + ingredientCount + " for processor " + procNumber);
	for (int i = 0; i < arrangement.size(); i++) {
	    int index = arrangement.get(i);
	    if (index < 0 || index >= inputCount)
		throw new IllegalStateException("Recipe " + inputType + " arrangement index " + index
			+ " is outside the " + inputCount + " available inputs for processor " + procNumber);
	    if (arrangement.subList(0, i).contains(index))
		throw new IllegalStateException(
			"Recipe reuses " + inputType + " input " + index + " for processor " + procNumber);
	}
	return arrangement;
    }

    public void dispenseExperience(ComponentInventory inv, double experience) {
	storedXp += experience;
	for (ItemStack stack : inv.getUpgradeContents()) {

	    if (!stack.isEmpty()) {
		ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();
		if (upgrade.subtype == SubtypeItemUpgrade.experience) {
		    stack.set(VoltaicDataComponentTypes.XP,
			    stack.getOrDefault(VoltaicDataComponentTypes.XP, 0.0) + getStoredXp());
		    setStoredXp(0);
		    break;
		}
	    }

	}
    }

    public boolean checkExistingRecipe(int index) {
	if (activeRecipies[index] != null)
	    return activeRecipies[index].matchesRecipe(this, index);
	return false;
    }

    @Nullable
    public VoltaicRecipe getRecipe(RecipeType<?> typeIn, int index) {
	if (cachedRecipes.isEmpty()) {
	    cachedRecipes = VoltaicRecipe.findRecipesbyType((RecipeType<VoltaicRecipe>) typeIn, getHolder().getLevel());
	}
	return VoltaicRecipe.getRecipe(this, cachedRecipes, index);
    }

    public void setChanged() {
	// hook method; empty for now
    }

    // now it only calculates it when the upgrades in the inventory change
    public void onInventoryChange(ComponentInventory inv, int slot) {
	if (inv.getUpgradeContents().size() > 0 && (slot >= inv.getUpgradeSlotStartIndex() || slot == -1)) {
	    operatingSpeed.setValue(1.0);
	    for (ItemStack stack : inv.getUpgradeContents()) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade upgrade && upgrade.subtype.isEmpty) {
		    for (int i = 0; i < stack.getCount(); i++) {
			if (upgrade.subtype == SubtypeItemUpgrade.basicspeed) {
			    operatingSpeed.setValue(Math.min(operatingSpeed.getValue() * 1.5, Math.pow(1.5, 3)));
			} else if (upgrade.subtype == SubtypeItemUpgrade.advancedspeed) {
			    operatingSpeed.setValue(Math.min(operatingSpeed.getValue() * 2.25, Math.pow(2.25, 3)));
			}
		    }
		}
	    }

	    if (holder.hasComponent(IComponentType.Electrodynamic)) {
		holder.<ComponentElectrodynamic>getComponent(IComponentType.Electrodynamic)
			.ifPresent(electro -> electro.maxJoules(getTotalUsage() * operatingSpeed.getValue() * 10));
	    }
	}
    }

}
