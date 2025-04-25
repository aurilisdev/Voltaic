package voltaic.datagen.utils.server.loottable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import voltaic.prefab.tile.components.type.ComponentInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class AbstractLootTableProvider extends VanillaBlockLoot {

	private final String modID;

	public AbstractLootTableProvider(HolderLookup.Provider provider, String modID) {
		this.modID = modID;
	}

	public LootTable.Builder machineTable(String name, Block block, BlockEntityType<?> type, boolean items, boolean fluids, boolean gases, boolean energy, boolean additional) {
		CopyNbtFunction.Builder function = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY);

		if (items) {
			function = function.copy("Items", "BlockEntityTag", CopyNbtFunction.MergeStrategy.REPLACE);
			function = function.copy(ComponentInventory.SAVE_KEY + "_size", "BlockEntityTag", CopyNbtFunction.MergeStrategy.REPLACE);
		}

		if (fluids) {
			function = function.copy("fluid", "BlockEntityTag", CopyNbtFunction.MergeStrategy.REPLACE);
		}

		if (gases) {
			// function = function
		}

		if (energy) {
			function = function.copy("joules", "BlockEntityTag.joules", CopyNbtFunction.MergeStrategy.REPLACE);
		}

		if (additional) {
			function = function.copy("additional", "BlockEntityTag.additional", CopyNbtFunction.MergeStrategy.REPLACE);
		}

		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1)).add(
				//
				LootItem.lootTableItem(block).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(function).apply(SetContainerContents.setContents(type).withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
		//
		);
		return LootTable.lootTable().withPool(builder);
	}

	/**
	 * Creates a silk touch and fortune loottable for a block
	 *
	 * @param name     Name of the block
	 * @param block    The block that will be added
	 * @param lootItem The alternative item that is dropped when silk is not used
	 * @param min      The minimum amount dropped
	 * @param max      The maximum amount dropped
	 * @author SeaRobber69
	 */
	protected LootTable.Builder createSilkTouchAndFortuneTable(String name, Block block, Item lootItem, float min, float max) {
		LootPool.Builder builder = LootPool.lootPool()
				//
				.name(name)
				//
				.setRolls(ConstantValue.exactly(1))
				//
				.add(
						//
						AlternativesEntry.alternatives(
								//
								LootItem.lootTableItem(block).when(HAS_SILK_TOUCH),
								//
								LootItem.lootTableItem(lootItem)
										//
										.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
										//
										.apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
										//
										.apply(ApplyExplosionDecay.explosionDecay())
						//
						)
				//
				);
		return LootTable.lootTable().withPool(builder);
	}

	/**
	 * Creates a silk touch only loottable for a block
	 *
	 * @param name  Name of the block
	 * @param block The block that will be added
	 * @author SeaRobber69
	 */
	protected LootTable.Builder createSilkTouchOnlyTable(String name, Block block) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block).when(HAS_SILK_TOUCH)

		);
		return LootTable.lootTable().withPool(builder);
	}

	protected LootTable.Builder createSimpleBlockTable(String name, Block block) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block));
		return LootTable.lootTable().withPool(builder);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {

		return BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().location().getNamespace().equals(modID) && !getExcludedBlocks().contains(e.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	public abstract List<Block> getExcludedBlocks();

}
