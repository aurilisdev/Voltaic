package voltaic.datagen.utils.server.loottable;

import voltaic.prefab.tile.GenericTile;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public abstract class BaseLootTablesProvider extends AbstractLootTableProvider {

    public BaseLootTablesProvider(DataGenerator dataGeneratorIn, String modID) {
        super(dataGeneratorIn, modID);
    }

    public <T extends GenericTile> void addMachineTable(Block block, RegistryObject<BlockEntityType<T>> tilereg, boolean items, boolean fluids, boolean gases, boolean energy, boolean additional) {
    	lootTables.put(block, machineTable(name(block), block, tilereg.get(), items, fluids, gases, energy, additional));
    }

    /**
     * Adds the block to the loottables silk touch only
     *
     * @param reg The block that will be added
     * @author SeaRobber69
     */
    public void addSilkTouchOnlyTable(RegistryObject<? extends Block> reg) {
        Block block = reg.get();
        lootTables.put(block, createSilkTouchOnlyTable(name(block), block));
    }

    public void addFortuneAndSilkTouchTable(RegistryObject<? extends Block> reg, Item nonSilk, int minDrop, int maxDrop) {
        addFortuneAndSilkTouchTable(reg.get(), nonSilk, minDrop, maxDrop);
    }

    public void addFortuneAndSilkTouchTable(Block block, Item nonSilk, int minDrop, int maxDrop) {
    	lootTables.put(block, createSilkTouchAndFortuneTable(name(block), block, nonSilk, minDrop, maxDrop));
    }

    public void addSimpleBlock(RegistryObject<? extends Block> reg) {
        addSimpleBlock(reg.get());
    }

    public void addSimpleBlock(Block block) {

    	lootTables.put(block, createSimpleBlockTable(name(block), block));
    }

    public String name(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }


}
