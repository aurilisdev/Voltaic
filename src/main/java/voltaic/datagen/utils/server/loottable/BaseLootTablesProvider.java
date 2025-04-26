package voltaic.datagen.utils.server.loottable;

import voltaic.prefab.tile.GenericTile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public abstract class BaseLootTablesProvider extends AbstractLootTableProvider {

    public BaseLootTablesProvider(String modID) {
        super(modID);
    }

    public <T extends GenericTile> void addMachineTable(Block block, RegistryObject<BlockEntityType<T>> tilereg, boolean items, boolean fluids, boolean gases, boolean energy, boolean additional) {
        add(block, machineTable(name(block), block, tilereg.get(), items, fluids, gases, energy, additional));
    }

    /**
     * Adds the block to the loottables silk touch only
     *
     * @param reg The block that will be added
     * @author SeaRobber69
     */
    public void addSilkTouchOnlyTable(RegistryObject<? extends Block> reg) {
        Block block = reg.get();
        add(block, createSilkTouchOnlyTable(name(block), block));
    }

    public void addFortuneAndSilkTouchTable(RegistryObject<? extends Block> reg, Item nonSilk, int minDrop, int maxDrop) {
        addFortuneAndSilkTouchTable(reg.get(), nonSilk, minDrop, maxDrop);
    }

    public void addFortuneAndSilkTouchTable(Block block, Item nonSilk, int minDrop, int maxDrop) {
        add(block, createSilkTouchAndFortuneTable(name(block), block, nonSilk, minDrop, maxDrop));
    }

    public void addSimpleBlock(RegistryObject<? extends Block> reg) {
        addSimpleBlock(reg.get());
    }

    public void addSimpleBlock(Block block) {

        add(block, createSimpleBlockTable(name(block), block));
    }

    public String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }


}
