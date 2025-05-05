package voltaic.datagen.utils.server.loottable;

import voltaic.prefab.tile.GenericTile;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BaseLootTablesProvider extends AbstractLootTableProvider {

    public BaseLootTablesProvider(DataGenerator dataGeneratorIn, String modID) {
        super(dataGeneratorIn, modID);
    }

    public <T extends GenericTile> void addMachineTable(Block block, RegistryObject<TileEntityType<T>> tilereg, boolean items, boolean fluids, boolean gases, boolean energy, boolean additional) {
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

    public <T extends Block, A extends Item> void addFortuneAndSilkTouchTable(RegistryObject<T> reg, A nonSilk, int minDrop, int maxDrop) {
        addFortuneAndSilkTouchTable(reg.get(), nonSilk, minDrop, maxDrop);
    }

    public <T extends Block, A extends Item> void addFortuneAndSilkTouchTable(T block, A nonSilk, int minDrop, int maxDrop) {
    	lootTables.put(block, createSilkTouchAndFortuneTable(name(block), block, nonSilk, minDrop, maxDrop));
    }

    public void addSimpleBlock(RegistryObject<? extends Block> reg) {
        addSimpleBlock(reg.get());
    }

    public <T extends Block> void addSimpleBlock(T block) {

    	lootTables.put(block, createSimpleBlockTable(name(block), block));
    }

    public <T extends Block> String name(T block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }


}
