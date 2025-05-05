package voltaic.prefab.tile.components.type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.TriConsumer;

import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.common.network.utils.FluidUtilities;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.categories.fluid2fluid.Fluid2FluidRecipe;
import voltaic.common.recipe.categories.fluid2item.Fluid2ItemRecipe;
import voltaic.common.recipe.categories.fluiditem2fluid.FluidItem2FluidRecipe;
import voltaic.common.recipe.categories.fluiditem2item.FluidItem2ItemRecipe;
import voltaic.common.recipe.categories.item2fluid.Item2FluidRecipe;
import voltaic.common.recipe.categories.item2item.Item2ItemRecipe;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.prefab.properties.variant.ArrayProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.utilities.ItemUtils;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.math.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ComponentProcessor implements IComponent {

    private GenericTile holder;

    public final SingleProperty<Double> operatingSpeed;
    public final ArrayProperty<Double> operatingTicks;
    public final ArrayProperty<Double> usage;
    public final ArrayProperty<Double> requiredTicks;
    private BiPredicate<ComponentProcessor, Integer> canProcess = (component, index) -> false;
    private BiConsumer<ComponentProcessor, Integer> process = (component, index) -> {};
    private TriConsumer<ComponentProcessor, List<Integer>, Boolean> failed = (component, failedProcessors, anySuceeded) -> {};
    private final int numProcessors;

    private List<VoltaicRecipe> cachedRecipes = new ArrayList<>();
    private final VoltaicRecipe[] activeRecipies;
    private double storedXp = 0.0;

    private ArrayProperty<Boolean> isActive;
    private ArrayProperty<Boolean> shouldKeepProgress;

    public ComponentProcessor(GenericTile source) {
        this(source, 1);
    }

    public ComponentProcessor(GenericTile source, int totalProcessors) {
        holder(source);
        this.numProcessors = totalProcessors;
        operatingSpeed = holder.property(new SingleProperty<>(PropertyTypes.DOUBLE, "operatingSpeed", 1.0));
        operatingTicks = holder.property(new ArrayProperty<>(PropertyTypes.DOUBLE_ARRAY, "operatingTicks", MathUtils.fillArr(new Double[totalProcessors], 0.0)));
        usage = holder.property(new ArrayProperty<>(PropertyTypes.DOUBLE_ARRAY, "recipeUsage", MathUtils.fillArr(new Double[totalProcessors], 0.0)));
        requiredTicks = holder.property(new ArrayProperty<>(PropertyTypes.DOUBLE_ARRAY, "requiredTicks", MathUtils.fillArr(new Double[totalProcessors], 0.0)));
        isActive = holder.property(new ArrayProperty<>(PropertyTypes.BOOLEAN_ARRAY, "isprocactive", MathUtils.fillArr(new Boolean[totalProcessors], false)));
        shouldKeepProgress = holder.property(new ArrayProperty<>(PropertyTypes.BOOLEAN_ARRAY, "shouldprockeepprogress", MathUtils.fillArr(new Boolean[totalProcessors], false)));
        activeRecipies = new VoltaicRecipe[totalProcessors];
        if (!holder.hasComponent(IComponentType.Inventory)) {
            throw new UnsupportedOperationException("You need to implement an inventory component to use the processor component!");
        }
        if (!holder.hasComponent(IComponentType.Tickable)) {
            throw new UnsupportedOperationException("You need to implement a tickable component to use the processor component!");
        }
        holder.<ComponentTickable>getComponent(IComponentType.Tickable).tickServer(this::tickServer);
    }

    @Override
    public void holder(GenericTile holder) {
        this.holder = holder;
    }

    @Override
    public GenericTile getHolder() {
        return holder;
    }

    private void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);

        for (ItemStack stack : inv.getUpgradeContents()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade) {
            	ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();
            	if(!upgrade.subtype.isEmpty) {
            		for (int i = 0; i < stack.getCount(); i++) {
                        for(int j = 0; j < numProcessors; j++) {
                            upgrade.subtype.applyUpgrade.accept(getHolder(), stack, j);
                        }
                    }
            	}
            }
        }

        boolean suceeded = false;
        List<Integer> failure = new ArrayList<>();

        for(int procNumber = 0; procNumber < numProcessors; procNumber++) {
            if (canProcess.test(this, procNumber)) {
                isActive.setValue(true, procNumber);
                operatingTicks.setValue(operatingTicks.getValue()[procNumber] + operatingSpeed.getValue(), procNumber);
                if (operatingTicks.getValue()[procNumber] >= requiredTicks.getValue()[procNumber]) {
                    if (process != null) {
                        process.accept(this, procNumber);
                        suceeded = true;
                    }
                    operatingTicks.setValue(0.0, procNumber);
                }
                if (holder.hasComponent(IComponentType.Electrodynamic)) {
                    electro.joules(electro.getJoulesStored() - usage.getValue()[procNumber] * operatingSpeed.getValue());
                }
            } else if (isActive(procNumber)) {
                isActive.setValue(false, procNumber);
                if (!shouldKeepProgress.getValue()[procNumber]) {
                    operatingTicks.setValue(0.0, procNumber);
                }

                if (failed != null) {
                    failure.add(procNumber);
                }
            } else {
                operatingTicks.setValue(0.0, procNumber);
            }
        }

        electro.maxJoules(getTotalUsage() * operatingSpeed.getValue() * 10);

        if(!failure.isEmpty()) {
            failed.accept(this, failure, suceeded);
        }


    }

    public ComponentProcessor process(BiConsumer<ComponentProcessor, Integer> process) {
        this.process = process;
        return this;
    }

    public ComponentProcessor failed(TriConsumer<ComponentProcessor, List<Integer>, Boolean> failed) {
        this.failed = failed;
        return this;
    }

    public ComponentProcessor canProcess(BiPredicate<ComponentProcessor, Integer> canProcess) {
        this.canProcess = canProcess;
        return this;
    }

    public ComponentProcessor usageForAll(double usage) {
        for(int index = 0; index <  numProcessors; index++) {
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
        for(double use : usage.getValue()) {
            total += use;
        }
        return total;
    }

    public ComponentProcessor requiredTicks(long requiredTicks, int index) {
        this.requiredTicks.setValue((double) requiredTicks, index);
        return this;
    }

    public ComponentProcessor requiredTicksForAll(long requiredTicks) {
        for(int index = 0; index <  numProcessors; index++) {
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
        FluidUtilities.drainItem(holder, holder.<ComponentFluidHandlerMulti>getComponent(IComponentType.FluidHandler).getInputTanks());
        return this;
    }

    public ComponentProcessor dispenseBucket() {
        FluidUtilities.fillItem(holder, holder.<ComponentFluidHandlerMulti>getComponent(IComponentType.FluidHandler).getOutputTanks());
        return this;
    }

    public ComponentProcessor outputToFluidPipe() {
        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        FluidUtilities.outputToPipe(holder, handler.getOutputTanks(), handler.outputDirections);
        return this;
    }

    public VoltaicRecipe getRecipe(int index) {
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
        for(int i = 0; i < numProcessors; i++) {
            if(isActive(i)) {
                return true;
            }
        }
        return false;
    }

    public int getTotalActive() {
        int count = 0;
        for(int i = 0; i < numProcessors; i++) {
            if(isActive(i)) {
                count++;
            }
        }
        return count;
    }

    public void setShouldKeepProgress(boolean should, int index) {
        shouldKeepProgress.setValue(should, index);
    }

    public boolean canProcessItem2ItemRecipe(int procNumber, IRecipeType<?> typeIn) {
        Item2ItemRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (Item2ItemRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (Item2ItemRecipe) activeRecipies[procNumber];
        }

        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ItemStack output = inv.getOutputContents().get(procNumber);
        ItemStack result = locRecipe.getItemRecipeOutput();
        boolean isEmpty = output.isEmpty();
        if (!isEmpty && !ItemUtils.testItems(output.getItem(), result.getItem())) {
            return false;
        }

        int locCap = isEmpty ? 64 : output.getMaxStackSize();
        if (locCap < output.getCount() + result.getCount()) {
            return false;
        }

        if (locRecipe.hasItemBiproducts()) {
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    public boolean canProcessFluid2ItemRecipe(int procNumber, IRecipeType<?> typeIn) {
        Fluid2ItemRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (Fluid2ItemRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (Fluid2ItemRecipe) activeRecipies[procNumber];
        }
        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ItemStack output = inv.getOutputContents().get(procNumber);
        ItemStack result = locRecipe.getItemRecipeOutput();
        boolean isEmpty = output.isEmpty();

        if (!isEmpty && !ItemUtils.testItems(output.getItem(), result.getItem())) {
            return false;
        }

        int locCap = isEmpty ? 64 : output.getMaxStackSize();
        if (locCap < output.getCount() + result.getCount()) {
            return false;
        }
        if (locRecipe.hasItemBiproducts()) {
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    public boolean canProcessFluid2FluidRecipe(int procNumber, IRecipeType<?> typeIn) {
        Fluid2FluidRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (Fluid2FluidRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (Fluid2FluidRecipe) activeRecipies[procNumber];
        }
        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        int amtAccepted = handler.getOutputTanks()[0].fill(locRecipe.getFluidRecipeOutput(), FluidAction.SIMULATE);
        if (amtAccepted < locRecipe.getFluidRecipeOutput().getAmount()) {
            return false;
        }
        if (locRecipe.hasItemBiproducts()) {
            ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    public boolean canProcessItem2FluidRecipe(int procNumber, IRecipeType<?> typeIn) {
        Item2FluidRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (Item2FluidRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (Item2FluidRecipe) activeRecipies[procNumber];
        }
        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        int amtAccepted = handler.getOutputTanks()[0].fill(locRecipe.getFluidRecipeOutput(), FluidAction.SIMULATE);
        if (amtAccepted < locRecipe.getFluidRecipeOutput().getAmount()) {
            return false;
        }
        if (locRecipe.hasItemBiproducts()) {
            ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    public boolean canProcessFluidItem2FluidRecipe(int procNumber, IRecipeType<?> typeIn) {
        FluidItem2FluidRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (FluidItem2FluidRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (FluidItem2FluidRecipe) activeRecipies[procNumber];
        }
        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        int amtAccepted = handler.getOutputTanks()[0].fill(locRecipe.getFluidRecipeOutput(), FluidAction.SIMULATE);
        if (amtAccepted < locRecipe.getFluidRecipeOutput().getAmount()) {
            return false;
        }
        if (locRecipe.hasItemBiproducts()) {
            ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    public boolean canProcessFluidItem2ItemRecipe(int procNumber, IRecipeType<?> typeIn) {
        FluidItem2ItemRecipe locRecipe;
        if (!checkExistingRecipe(procNumber)) {
            setShouldKeepProgress(false, procNumber);
            operatingTicks.setValue(0.0, procNumber);
            locRecipe = (FluidItem2ItemRecipe) getRecipe(typeIn, procNumber);
            if (locRecipe == null) {
                return false;
            }
        } else {
            setShouldKeepProgress(true, procNumber);
            locRecipe = (FluidItem2ItemRecipe) activeRecipies[procNumber];
        }
        setRecipe(locRecipe, procNumber);

        requiredTicks.setValue((double) locRecipe.getTicks(), procNumber);
        usage.setValue(locRecipe.getUsagePerTick(), procNumber);

        ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);

        if (electro.getJoulesStored() < getUsage(procNumber)) {
            return false;
        }

        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ItemStack output = inv.getOutputContents().get(procNumber);
        ItemStack result = locRecipe.getItemRecipeOutput();
        boolean isEmpty = output.isEmpty();

        if (!isEmpty && !ItemUtils.testItems(output.getItem(), result.getItem())) {
            return false;
        }

        int locCap = isEmpty ? 64 : output.getMaxStackSize();
        if (locCap < output.getCount() + result.getCount()) {
            return false;
        }
        if (locRecipe.hasItemBiproducts()) {
            boolean itemBiRoom = roomInItemBiSlots(inv.getBiprodsForProcessor(procNumber), locRecipe.getFullItemBiStacks());
            if (!itemBiRoom) {
                return false;
            }
        }
        if (locRecipe.hasFluidBiproducts()) {
            ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
            boolean fluidBiRoom = roomInBiproductFluidTanks(handler.getOutputTanks(), locRecipe.getFullFluidBiStacks());
            if (!fluidBiRoom) {
                return false;
            }
        }
        return true;
    }

    /*
     * CONVENTIONS TO NOTE:
     *
     * Biproducts will be output in the order they appear in the recipe JSON
     *
     * The output FluidTanks will contain both the recipe output tank and the biproduct tanks The first tank is ALWAYS the main output tank, and the following tanks will be filled in the order of the fluid biproducts
     *
     *
     *
     *
     * Also, no checks outside of the null recipe check will be performed in these methods All validity checks will take place in the recipe validator methods
     *
     */

    public void processItem2ItemRecipe(int procNumber) {
        if (getRecipe(procNumber) == null) {
            return;
        }
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        Item2ItemRecipe locRecipe = (Item2ItemRecipe) getRecipe(procNumber);
        List<Integer> slotOrientation = locRecipe.getItemArrangment(procNumber);

        if (locRecipe.hasItemBiproducts()) {

            List<ProbableItem> itemBi = locRecipe.getItemBiproducts();
            int index = 0;

            for (int slot : inv.getBiprodSlotsForProcessor(procNumber)) {

                ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) {
                    inv.setItem(slot, itemBi.get(index).roll().copy());
                } else {
                    stack.grow(itemBi.get(index).roll().getCount());
                    inv.setItem(slot, stack);
                }
            }

        }

        if (locRecipe.hasFluidBiproducts()) {
            ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
            List<ProbableFluid> fluidBi = locRecipe.getFluidBiproducts();
            FluidTank[] outTanks = handler.getOutputTanks();
            for (int i = 0; i < fluidBi.size(); i++) {

                outTanks[i].fill(fluidBi.get(i).roll(), FluidAction.EXECUTE);
            }
        }

        int outputSlot = inv.getOutputSlots().get(procNumber);

        if (inv.getOutputContents().get(procNumber).isEmpty()) {
            inv.setItem(outputSlot, locRecipe.getItemRecipeOutput().copy());
        } else {
            ItemStack stack = inv.getOutputContents().get(procNumber);
            stack.grow(locRecipe.getItemRecipeOutput().getCount());
            inv.setItem(outputSlot, stack);

        }
        List<Integer> inputs = inv.getInputSlotsForProcessor(procNumber);
        for (int i = 0; i < inputs.size(); i++) {
            int index = inputs.get(slotOrientation.get(i));
            ItemStack stack = inv.getItem(index);
            stack.shrink(locRecipe.getCountedIngredients().get(i).getStackSize());
            inv.setItem(index, stack);
        }
        dispenseExperience(inv, locRecipe.getXp());
        setChanged();
    }

    public void processFluidItem2FluidRecipe(int procNumber) {
        if (getRecipe(procNumber) == null) {
            return;
        }
        FluidItem2FluidRecipe locRecipe = (FluidItem2FluidRecipe) getRecipe(procNumber);
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        List<Integer> slotOrientation = locRecipe.getItemArrangment(procNumber);
        if (locRecipe.hasItemBiproducts()) {

            List<ProbableItem> itemBi = locRecipe.getItemBiproducts();
            int index = 0;

            for (int slot : inv.getBiprodSlotsForProcessor(procNumber)) {

                ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) {
                    inv.setItem(slot, itemBi.get(index).roll().copy());
                } else {
                    stack.grow(itemBi.get(index).roll().getCount());
                    inv.setItem(slot, stack);
                }
            }

        }

        if (locRecipe.hasFluidBiproducts()) {
            List<ProbableFluid> fluidBi = locRecipe.getFluidBiproducts();
            FluidTank[] outTanks = handler.getOutputTanks();
            for (int i = 0; i < fluidBi.size(); i++) {
                outTanks[i + 1].fill(fluidBi.get(i).roll(), FluidAction.EXECUTE);
            }
        }

        handler.getOutputTanks()[0].fill(locRecipe.getFluidRecipeOutput(), FluidAction.EXECUTE);

        List<Integer> inputs = inv.getInputSlotsForProcessor(procNumber);
        for (int i = 0; i < inputs.size(); i++) {
            int index = inputs.get(slotOrientation.get(i));
            ItemStack stack = inv.getItem(index);
            stack.shrink(locRecipe.getCountedIngredients().get(i).getStackSize());
            inv.setItem(index, stack);
        }

        FluidTank[] tanks = handler.getInputTanks();
        List<FluidIngredient> fluidIngs = locRecipe.getFluidIngredients();
        List<Integer> tankOrientation = locRecipe.getFluidArrangement();
        for (int i = 0; i < handler.tankCount(true); i++) {
            tanks[tankOrientation.get(i)].drain(fluidIngs.get(i).getFluidStack().getAmount(), FluidAction.EXECUTE);
        }
        dispenseExperience(inv, locRecipe.getXp());
        setChanged();
    }

    public void processFluidItem2ItemRecipe(int procNumber) {
        if (getRecipe(procNumber) == null) {
            return;
        }
        FluidItem2ItemRecipe locRecipe = (FluidItem2ItemRecipe) getRecipe(procNumber);
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);
        List<Integer> slotOrientation = locRecipe.getItemArrangment(procNumber);

        if (locRecipe.hasItemBiproducts()) {

            List<ProbableItem> itemBi = locRecipe.getItemBiproducts();
            int index = 0;

            for (int slot : inv.getBiprodSlotsForProcessor(procNumber)) {

                ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) {
                    inv.setItem(slot, itemBi.get(index).roll().copy());
                } else {
                    stack.grow(itemBi.get(index).roll().getCount());
                    inv.setItem(slot, stack);
                }
            }

        }

        if (locRecipe.hasFluidBiproducts()) {
            List<ProbableFluid> fluidBi = locRecipe.getFluidBiproducts();
            FluidTank[] outTanks = handler.getOutputTanks();
            for (int i = 0; i < fluidBi.size(); i++) {
                outTanks[i].fill(fluidBi.get(i).roll(), FluidAction.EXECUTE);
            }
        }

        if (inv.getOutputContents().get(procNumber).isEmpty()) {
            inv.setItem(inv.getOutputSlots().get(procNumber), locRecipe.getItemRecipeOutput().copy());
        } else {
            inv.getOutputContents().get(procNumber).grow(locRecipe.getItemRecipeOutput().getCount());
        }

        List<Integer> inputs = inv.getInputSlotsForProcessor(procNumber);
        for (int i = 0; i < inputs.size(); i++) {
            int index = inputs.get(slotOrientation.get(i));
            ItemStack stack = inv.getItem(index);
            stack.shrink(locRecipe.getCountedIngredients().get(i).getStackSize());
            inv.setItem(index, stack);
        }

        FluidTank[] tanks = handler.getInputTanks();
        List<FluidIngredient> fluidIngs = locRecipe.getFluidIngredients();
        List<Integer> tankOrientation = locRecipe.getFluidArrangement();
        for (int i = 0; i < handler.tankCount(true); i++) {
            tanks[tankOrientation.get(i)].drain(fluidIngs.get(i).getFluidStack().getAmount(), FluidAction.EXECUTE);
        }
        dispenseExperience(inv, locRecipe.getXp());
        setChanged();
    }

    public void processFluid2ItemRecipe(int procNumber) {
        if (getRecipe(procNumber) == null) {
            return;
        }
        Fluid2ItemRecipe locRecipe = (Fluid2ItemRecipe) getRecipe(procNumber);
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);

        if (locRecipe.hasItemBiproducts()) {

            List<ProbableItem> itemBi = locRecipe.getItemBiproducts();
            int index = 0;

            for (int slot : inv.getBiprodSlotsForProcessor(procNumber)) {

                ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) {
                    inv.setItem(slot, itemBi.get(index).roll().copy());
                } else {
                    stack.grow(itemBi.get(index).roll().getCount());
                    inv.setItem(slot, stack);
                }
            }

        }

        if (locRecipe.hasFluidBiproducts()) {
            List<ProbableFluid> fluidBi = locRecipe.getFluidBiproducts();
            FluidTank[] outTanks = handler.getOutputTanks();
            for (int i = 0; i < fluidBi.size(); i++) {
                outTanks[i].fill(fluidBi.get(i).roll(), FluidAction.EXECUTE);
            }
        }

        if (inv.getOutputContents().get(procNumber).isEmpty()) {
            inv.setItem(inv.getOutputSlots().get(procNumber), locRecipe.getItemRecipeOutput().copy());
        } else {
            inv.getOutputContents().get(procNumber).grow(locRecipe.getItemRecipeOutput().getCount());
        }

        FluidTank[] tanks = handler.getInputTanks();
        List<FluidIngredient> fluidIngs = locRecipe.getFluidIngredients();
        List<Integer> tankOrientation = locRecipe.getFluidArrangement();
        for (int i = 0; i < handler.tankCount(true); i++) {
            tanks[tankOrientation.get(i)].drain(fluidIngs.get(i).getFluidStack().getAmount(), FluidAction.EXECUTE);
        }
        dispenseExperience(inv, locRecipe.getXp());
        setChanged();
    }

    public void processFluid2FluidRecipe(int procNumber) {
        if(getRecipe(procNumber) == null) {
            return;
        }

        Fluid2FluidRecipe locRecipe = (Fluid2FluidRecipe) getRecipe(procNumber);
        ComponentInventory inv = holder.getComponent(IComponentType.Inventory);
        ComponentFluidHandlerMulti handler = holder.getComponent(IComponentType.FluidHandler);

        if (locRecipe.hasItemBiproducts()) {

            List<ProbableItem> itemBi = locRecipe.getItemBiproducts();
            int index = 0;

            for (int slot : inv.getBiprodSlotsForProcessor(procNumber)) {

                ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) {
                    inv.setItem(slot, itemBi.get(index).roll().copy());
                } else {
                    stack.grow(itemBi.get(index).roll().getCount());
                    inv.setItem(slot, stack);
                }
            }

        }

        if (locRecipe.hasFluidBiproducts()) {
            List<ProbableFluid> fluidBi = locRecipe.getFluidBiproducts();
            FluidTank[] outTanks = handler.getOutputTanks();
            for (int i = 0; i < fluidBi.size(); i++) {
                outTanks[i + 1].fill(fluidBi.get(i).roll(), FluidAction.EXECUTE);
            }
        }

        handler.getOutputTanks()[0].fill(locRecipe.getFluidRecipeOutput(), FluidAction.EXECUTE);

        FluidTank[] tanks = handler.getInputTanks();
        List<FluidIngredient> fluidIngs = locRecipe.getFluidIngredients();
        List<Integer> tankOrientation = locRecipe.getFluidArrangement();
        for (int i = 0; i < handler.tankCount(true); i++) {
            tanks[tankOrientation.get(i)].drain(fluidIngs.get(i).getFluidStack().getAmount(), FluidAction.EXECUTE);
        }
        dispenseExperience(inv, locRecipe.getXp());
        setChanged();

    }

    public void dispenseExperience(ComponentInventory inv, double experience) {
        storedXp += experience;
        for (ItemStack stack : inv.getUpgradeContents()) {

            if (!stack.isEmpty()) {
                ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();
                if (upgrade.subtype == SubtypeItemUpgrade.experience) {
                	CompoundNBT tag = stack.getOrCreateTag();
					tag.putDouble(NBTUtils.XP, tag.getDouble(NBTUtils.XP) + getStoredXp());
					setStoredXp(0);
                    break;
                }
            }

        }
    }

    public static boolean roomInItemBiSlots(List<ItemStack> slots, ItemStack[] biproducts) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack slotStack = slots.get(i);
            ItemStack biStack = biproducts[Math.min(i, biproducts.length - 1)];
            if (!slotStack.isEmpty()) {
                if ((slotStack.getCount() + biStack.getCount() > slotStack.getMaxStackSize())) {
                    return false;
                }
                if (!ItemUtils.testItems(slotStack.getItem(), biStack.getItem())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean roomInBiproductFluidTanks(FluidTank[] tanks, FluidStack[] stacks) {
        for (int i = 1; i < tanks.length; i++) {
            FluidTank tank = tanks[i];
            FluidStack stack = stacks[Math.min(i, stacks.length - 1)];
            int amtTaken = tank.fill(stack, FluidAction.SIMULATE);
            if (amtTaken < stack.getAmount()) {
                return false;
            }
        }
        return true;
    }

    public boolean checkExistingRecipe(int index) {
        if (activeRecipies[index] != null) {
            return activeRecipies[index].matchesRecipe(this, index);
        }
        return false;
    }

    @Nullable
    public VoltaicRecipe getRecipe(IRecipeType<?> typeIn, int index) {
        if (cachedRecipes.isEmpty()) {
            cachedRecipes = VoltaicRecipe.findRecipesbyType((IRecipeType<VoltaicRecipe>) typeIn, getHolder().getLevel());
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
            	if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade) {
					ItemUpgrade upgrade = (ItemUpgrade) stack.getItem();
					if (upgrade.subtype.isEmpty) {
						for (int i = 0; i < stack.getCount(); i++) {
							if (upgrade.subtype == SubtypeItemUpgrade.basicspeed) {
								operatingSpeed.setValue(Math.min(operatingSpeed.getValue() * 1.5, Math.pow(1.5, 3)));
							} else if (upgrade.subtype == SubtypeItemUpgrade.advancedspeed) {
								operatingSpeed.setValue(Math.min(operatingSpeed.getValue() * 2.25, Math.pow(2.25, 3)));
							}
						}
					}

				}
            }

            if (holder.hasComponent(IComponentType.Electrodynamic)) {
                ComponentElectrodynamic electro = holder.getComponent(IComponentType.Electrodynamic);
                electro.maxJoules(getTotalUsage() * operatingSpeed.getValue() * 10);
            }
        }
    }

}
