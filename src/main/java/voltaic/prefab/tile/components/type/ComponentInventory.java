package voltaic.prefab.tile.components.type;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import voltaic.api.inventory.IndexedSidedInvWrapper;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.CapabilityInputType;
import voltaic.prefab.tile.components.IComponent;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ComponentInventory implements IComponent, WorldlyContainer {

    protected static final int[] SLOTS_EMPTY = new int[] {};
    public static final String SAVE_KEY = "itemproperty";

    protected GenericTile holder = null;

    private final ListProperty<ItemStack> items;

    protected TriPredicate<Integer, ItemStack, ComponentInventory> itemValidTest = (x, y, i) -> true;

    protected HashSet<Player> viewing = new HashSet<>();

    public HashSet<Integer>[] relativeDirectionToSlotsMap = new HashSet[6]; // Down Up North South West East

    protected int inventorySize;

    protected Function<Direction, Collection<Integer>> getSlotsFunction;

    private final IItemHandlerModifiable[] sidedOptionals = IndexedSidedInvWrapper.create(this, Direction.values());

    private static int[][] createArr() {
        int[][] arr = new int[6][];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = new int[] {};
        }

        return arr;
    }

    private int[][] slotsForFace = createArr(); // Down Up North South West East

    /*
     * IMPORTANT DEFINITIONS:
     * 
     * SLOT ORDER: 1. Item Input Slots 2. Item Output Slot 3. Item Biproduct Slots 4. Bucket Input Slots 5. Bucket Output
     * Slots 6. Upgrade Slots
     * 
     */

    private int inputs = 0;
    private int outputs = 0;
    private int upgrades = 0;
    private int biproducts = 0;
    private int bucketInputs = 0;
    private int bucketOutputs = 0;
    private int gasInputs = 0;
    private int gasOutputs = 0;

    private int inputsPerProc = 0;
    private int outputsPerProc = 0;
    private int biprodsPerProc = 0;

    private BiConsumer<ComponentInventory, Integer> onChanged = (componentInventory, slot) -> {
        if (holder != null) {
            holder.onInventoryChange(componentInventory, slot);
        }
    };

    protected SubtypeItemUpgrade[] validUpgrades = SubtypeItemUpgrade.values();

    public ComponentInventory(GenericTile holder) {
        this(holder, InventoryBuilder.EMPTY);
    }

    public ComponentInventory(GenericTile holder, InventoryBuilder builder) {
        holder(holder);

        if (!holder.getBlockState().hasProperty(VoltaicBlockStates.FACING)) {
            throw new UnsupportedOperationException("The tile " + holder + " must have the FACING direction property!");
        }

        if (builder.builderSize > 0) {
            inventorySize = builder.builderSize;
        } else {

            inputs = builder.builderInputs;
            outputs = builder.builderOutputs;
            upgrades = builder.builderUpgrades;
            biproducts = builder.builderBiproducts;
            bucketInputs = builder.builderBucketInputs;
            bucketOutputs = builder.builderBucketOutputs;
            gasInputs = builder.builderGasInputs;
            gasOutputs = builder.builderGasOutputs;

            inventorySize = inputs + outputs + upgrades + biproducts + bucketInputs + bucketOutputs + gasInputs + gasOutputs + upgrades;

            inputsPerProc = builder.builderInputsPerProc;
            outputsPerProc = builder.builderOutputsPerProc;
            biprodsPerProc = builder.builderBiprodsPerProc;

        }

        List<ItemStack> items = new ArrayList<>(inventorySize);

        for(int i = 0; i < inventorySize; i++) {
            items.add(ItemStack.EMPTY);
        }

        this.items = holder.property(new ListProperty<>(PropertyTypes.ITEM_STACK_LIST, "machineinventory", items));

    }

    @Override
    public void holder(GenericTile holder) {
        this.holder = holder;
    }

    @Override
    public GenericTile getHolder() {
        return holder;
    }

    public ComponentInventory onChanged(BiConsumer<ComponentInventory, Integer> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    public ComponentInventory getSlots(Function<Direction, Collection<Integer>> getSlotsFunction) {
        this.getSlotsFunction = getSlotsFunction;
        return this;
    }

    public ComponentInventory setSlotsByDirection(BlockEntityUtils.MachineDirection face, Integer... slot) {
        Direction faceDir = face.mappedDir;
        if (relativeDirectionToSlotsMap[faceDir.ordinal()] == null) {
            relativeDirectionToSlotsMap[faceDir.ordinal()] = new HashSet<>();
        }
        for (Integer sl : slot) {
            relativeDirectionToSlotsMap[faceDir.ordinal()].add(sl);
        }
        return this;
    }

    public ComponentInventory setDirectionsBySlot(Integer slot, BlockEntityUtils.MachineDirection... faces) {
        for (BlockEntityUtils.MachineDirection face : faces) {
            setSlotsByDirection(face, slot);
        }
        return this;
    }

    public ComponentInventory setSlotsForAllDirections(Integer... slots) {
        for (BlockEntityUtils.MachineDirection faceDirection : BlockEntityUtils.MachineDirection.values()) {
            setSlotsByDirection(faceDirection, slots);
        }
        return this;
    }

    public ComponentInventory implementMachineInputsAndOutputs() {
        ComponentInventory inv = this;

        for (int i : getInputSlots()) {
            inv = inv.setSlotsByDirection(BlockEntityUtils.MachineDirection.RIGHT, i).setSlotsByDirection(BlockEntityUtils.MachineDirection.TOP, i);
        }

        for (int i : getOutputSlots()) {
            inv = inv.setSlotsByDirection(BlockEntityUtils.MachineDirection.LEFT, i).setSlotsByDirection(BlockEntityUtils.MachineDirection.BOTTOM, i);
        }

        for (int i : getBiproductSlots()) {
            inv = inv.setSlotsByDirection(BlockEntityUtils.MachineDirection.LEFT, i).setSlotsByDirection(BlockEntityUtils.MachineDirection.BOTTOM, i);
        }

        return inv;

    }

    public ComponentInventory valid(TriPredicate<Integer, ItemStack, ComponentInventory> itemValidPredicate) {
        itemValidTest = itemValidPredicate;
        return this;
    }

    @Override
    public void startOpen(Player player) {
        viewing.add(player);
    }

    @Override
    public void stopOpen(Player player) {
        viewing.remove(player);
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side, CapabilityInputType inputType) {
    	if (side == null) {
            return LazyOptional.empty();
        }
        return LazyOptional.of(() -> sidedOptionals[side.ordinal()]).cast();
    }

    @Override
    public void refresh() {

        defineOptionals(holder.getFacing());

    }

    @Override
    public void refreshIfUpdate(BlockState oldState, BlockState newState) {
        if (oldState.hasProperty(VoltaicBlockStates.FACING) && newState.hasProperty(VoltaicBlockStates.FACING) && oldState.getValue(VoltaicBlockStates.FACING) != newState.getValue(VoltaicBlockStates.FACING)) {
            defineOptionals(newState.getValue(VoltaicBlockStates.FACING));
        }
    }

    private void defineOptionals(Direction facing) {

        holder.invalidateCaps();

        slotsForFace = new int[6][];

        Direction relative;

        for (Direction dir : Direction.values()) {

            relative = BlockEntityUtils.getRelativeSide(facing, dir);

            HashSet<Integer> slots = relativeDirectionToSlotsMap[dir.ordinal()];

            if (slots == null) {

                slotsForFace[relative.ordinal()] = SLOTS_EMPTY;

            } else {

                int[] arr = new int[slots.size()];

                int i = 0;

                for (Integer integer : slots) {
                    arr[i] = integer;
                    i++;
                }

                slotsForFace[relative.ordinal()] = arr;

            }

        }

    }

    @Override
    public int getContainerSize() {
        return inventorySize;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : items.getValue()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.getValue().get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        if (index < 0 || index >= items.getValue().size() || count <= 0 || items.getValue().get(index).isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack indexItem = items.getValue().get(index);
        ItemStack taken = indexItem.split(count);

        items.setValue(indexItem, index);

        //items.forceDirty();

        setChanged(index);

        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items.getValue(), index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        if (index < 0 || index >= items.getValue().size() || ItemStack.matches(items.getValue().get(index), stack)) {
            return;
        }

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        items.setValue(stack, index);

        //items.forceDirty();

        setChanged(index);
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = holder.getBlockPos();
        return holder.getLevel().getBlockEntity(pos) == holder && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        items.wipeList();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (getSlotsFunction != null) {
            return getSlotsFunction.apply(side).stream().mapToInt(i -> i).toArray();
        }

        return side == null ? SLOTS_EMPTY : slotsForFace[side.ordinal()];
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return itemValidTest.test(index, stack, this);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        ArrayList<Integer> test = new ArrayList<>();
        for (int i : getSlotsForFace(direction)) {
            test.add(i);
        }
        return test.contains(index) && canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        ArrayList<Integer> test = new ArrayList<>();
        for (int i : getSlotsForFace(direction)) {
            test.add(i);
        }
        return test.contains(index);
    }

    public NonNullList<ItemStack> getItems() {
    	NonNullList<ItemStack> its = NonNullList.create();
    	its.addAll(items.getValue());
        return its;
    }

    public HashSet<Player> getViewing() {
        return viewing;
    }

    @Override
    public IComponentType getType() {
        return IComponentType.Inventory;
    }

    @Override
    public void remove() {
        // Not required
    }

    @Override
    // this is only called through someone instance checking of this class....
    public void setChanged() {
        setChanged(-1);
    }

    public void setChanged(int slot) {
        if (onChanged != null) {
            onChanged.accept(this, slot);
        }
    }

    public int inputs() {
        return inputs;
    }

    public int outputs() {
        return outputs;
    }

    public int upgrades() {
        return upgrades;
    }

    public ComponentInventory validUpgrades(SubtypeItemUpgrade... upgrades) {
        validUpgrades = upgrades;
        return this;
    }

    public SubtypeItemUpgrade[] validUpgrades() {
        return validUpgrades;
    }

    public int biproducts() {
        return biproducts;
    }

    public int bucketInputs() {
        return bucketInputs;
    }

    public int bucketOutputs() {
        return bucketOutputs;
    }

    public int gasInputs() {
        return gasInputs;
    }

    public int gasOutputs() {
        return gasOutputs;
    }

    /*
     * Utility methods so you don't have to think as much
     */

    public int getInputStartIndex() {
        return 0;
    }

    public int getOutputStartIndex() {
        return inputs;
    }

    public int getItemBiproductStartIndex() {
        return getOutputStartIndex() + outputs;
    }

    public int getInputBucketStartIndex() {
        return getItemBiproductStartIndex() + biproducts;
    }

    public int getOutputBucketStartIndex() {
        return getInputBucketStartIndex() + bucketInputs;
    }

    public int getInputGasStartIndex() {
        return getOutputBucketStartIndex() + bucketOutputs;
    }

    public int getOutputGasStartIndex() {
        return getInputGasStartIndex() + gasInputs;
    }

    public int getUpgradeSlotStartIndex() {
        return getOutputGasStartIndex() + gasOutputs;
    }

    public List<ItemStack> getInputContents() {
        return items.getValue().subList(getInputStartIndex(), getOutputStartIndex());
    }

    public List<ItemStack> getOutputContents() {
        return items.getValue().subList(getOutputStartIndex(), getItemBiproductStartIndex());
    }

    public List<ItemStack> getItemBiContents() {
        return items.getValue().subList(getItemBiproductStartIndex(), getInputBucketStartIndex());
    }

    public List<ItemStack> getInputBucketContents() {
        return items.getValue().subList(getInputBucketStartIndex(), getOutputBucketStartIndex());
    }

    public List<ItemStack> getOutputBucketContents() {
        return items.getValue().subList(getOutputBucketStartIndex(), getUpgradeSlotStartIndex());
    }

    public List<ItemStack> getInputGasContents() {
        return items.getValue().subList(getInputGasStartIndex(), getOutputGasStartIndex());
    }

    public List<ItemStack> getOutputGasContents() {
        return items.getValue().subList(getOutputGasStartIndex(), getUpgradeSlotStartIndex());
    }

    public List<ItemStack> getUpgradeContents() {
        return items.getValue().subList(getUpgradeSlotStartIndex(), items.getValue().size());
    }

    // processor number is indexed at zero
    public List<ItemStack> getInputsForProcessor(int processor) {
        return getInputContents().subList(inputsPerProc * processor, inputsPerProc * (processor + 1));
    }

    // processor number is indexed at zero
    public List<ItemStack> getOutputsForProcessor(int processor) {
        return getOutputContents().subList(outputsPerProc * processor, outputsPerProc * (processor + 1));
    }

    // processor number is indexed at zero
    public List<ItemStack> getBiprodsForProcessor(int processor) {
        return getItemBiContents().subList(biprodsPerProc * processor, biprodsPerProc * (processor + 1));
    }

    public List<Integer> getInputSlots() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < inputs; i++) {
            list.add(getInputStartIndex() + i);
        }
        return list;
    }

    public List<Integer> getOutputSlots() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < outputs; i++) {
            list.add(getOutputStartIndex() + i);
        }
        return list;
    }

    public List<Integer> getBiproductSlots() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < biproducts; i++) {
            list.add(getItemBiproductStartIndex() + i);
        }
        return list;
    }

    // processor number is indexed at zero
    public List<Integer> getInputSlotsForProcessor(int processor) {
        return getInputSlots().subList(inputsPerProc * processor, inputsPerProc * (processor + 1));
    }

    // processor number is indexed at zero
    public List<Integer> getOutputSlotsForProcessor(int processor) {
        return getOutputSlots().subList(outputsPerProc * processor, outputsPerProc * (processor + 1));
    }

    // processor number is indexed at zero
    public List<Integer> getBiprodSlotsForProcessor(int processor) {
        return getBiproductSlots().subList(biprodsPerProc * processor, biprodsPerProc * (processor + 1));
    }

    public boolean areOutputsEmpty() {
        boolean output = false;
        boolean biproduct = false;
        for (ItemStack stack : getOutputContents()) {
            if (stack.isEmpty()) {
                output = true;
                break;
            }
        }
        if (!getItemBiContents().isEmpty()) {
            for (ItemStack stack : getItemBiContents()) {
                if (stack.isEmpty()) {
                    biproduct = true;
                    break;
                }
            }
        } else {
            biproduct = true;
        }
        return output && biproduct;
    }

    public boolean hasItemsInOutput() {
        for (ItemStack stack : getOutputContents()) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        for (ItemStack stack : getItemBiContents()) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean areInputsEmpty() {
        for (ItemStack stack : getInputContents()) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return false;
    }

    public boolean hasInputRoom() {
        for (ItemStack stack : getInputContents()) {
            if (stack.getMaxStackSize() > stack.getCount()) {
                return true;
            }
        }
        return false;
    }

    public boolean isUpgradeValid(SubtypeItemUpgrade upgrade) {
        for (SubtypeItemUpgrade subtype : validUpgrades) {
            if (subtype == upgrade) {
                return true;
            }
        }
        return false;
    }

    public static class InventoryBuilder {

        private static final InventoryBuilder EMPTY = new InventoryBuilder();

        private int builderSize = 0;

        private int builderInputs = 0;
        private int builderOutputs = 0;
        private int builderBiproducts = 0;
        private int builderBucketInputs = 0;
        private int builderBucketOutputs = 0;
        private int builderUpgrades = 0;
        private int builderGasInputs = 0;
        private int builderGasOutputs = 0;

        private int builderInputsPerProc = 0;
        private int builderOutputsPerProc = 0;
        private int builderBiprodsPerProc = 0;

        private InventoryBuilder() {

        }

        public InventoryBuilder inputs(int inputs) {
            this.builderInputs = inputs;
            return this;
        }

        public InventoryBuilder outputs(int outputs) {
            this.builderOutputs = outputs;
            return this;
        }

        public InventoryBuilder biproducts(int biproducts) {
            this.builderBiproducts = biproducts;
            return this;
        }

        public InventoryBuilder bucketInputs(int bucketInputs) {
            this.builderBucketInputs = bucketInputs;
            return this;
        }

        public InventoryBuilder bucketOutputs(int bucketOutputs) {
            this.builderBucketOutputs = bucketOutputs;
            return this;
        }

        public InventoryBuilder gasInputs(int gasInputs) {
            this.builderGasInputs = gasInputs;
            return this;
        }

        public InventoryBuilder gasOutputs(int gasOutputs) {
            this.builderGasOutputs = gasOutputs;
            return this;
        }

        public InventoryBuilder upgrades(int upgrades) {
            this.builderUpgrades = upgrades;
            return this;
        }

        /**
         * Specialized method for machines that use ComponentProcessors. It removed the need to individually set input, output,
         * and biproduct slots.
         * 
         * @param procCount      How many ComponentProcessors the machine has
         * @param inputsPerProc  How many inputs are assigned to a processor
         * @param outputsPerProc How many outputs are assigned to a processor
         * @param biprodsPerProc how many biproducts are assigned to a processor
         * @return The mutated inventory builder
         */
        public InventoryBuilder processors(int procCount, int inputsPerProc, int outputsPerProc, int biprodsPerProc) {

            this.builderInputsPerProc = inputsPerProc;
            this.builderOutputsPerProc = outputsPerProc;
            this.builderBiprodsPerProc = biprodsPerProc;

            this.builderInputs = procCount * inputsPerProc;
            this.builderOutputs = procCount * outputsPerProc;
            this.builderBiproducts = procCount * biprodsPerProc;

            return this;
        }

        /**
         * This method should not be used in tandem with other individual mutator methods and is designed for inventories that
         * have no specified slot types
         * 
         * @param size The desired size of the inventory
         * @return The mutated builder
         */
        public InventoryBuilder forceSize(int size) {
            this.builderSize = size;
            return this;
        }

        public static InventoryBuilder newInv() {
            return new InventoryBuilder();
        }

    }

}
