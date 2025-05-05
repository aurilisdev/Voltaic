package voltaic.datagen.utils.server.loottable;

import voltaic.Voltaic;
import voltaic.prefab.tile.components.type.ComponentInventory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.AlternativesLootEntry;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.DynamicLootEntry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractLootTableProvider extends LootTableProvider {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;
	private final String modID;

	public AbstractLootTableProvider(DataGenerator dataGeneratorIn, String modID) {
		super(dataGeneratorIn);
		this.generator = dataGeneratorIn;
		this.modID = modID;
	}

	public LootTable.Builder machineTable(String name, Block block, TileEntityType<?> type, boolean items, boolean fluids, boolean gases, boolean energy, boolean additional) {
		CopyNbt.Builder function = CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY);

		if (items) {
			function = function.copy("Items", "BlockEntityTag", CopyNbt.Action.REPLACE);
			function = function.copy(ComponentInventory.SAVE_KEY + "_size", "BlockEntityTag", CopyNbt.Action.REPLACE);
		}

		if (fluids) {
			function = function.copy("fluid", "BlockEntityTag", CopyNbt.Action.REPLACE);
		}

		if (gases) {
			// function = function
		}

		if (energy) {
			function = function.copy("joules", "BlockEntityTag.joules", CopyNbt.Action.REPLACE);
		}

		if (additional) {
			function = function.copy("additional", "BlockEntityTag.additional", CopyNbt.Action.REPLACE);
		}

		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantRange.exactly(1)).add(
				//
				ItemLootEntry.lootTableItem(block).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)).apply(function).apply(SetContents.setContents().withEntry(DynamicLootEntry.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
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
				.setRolls(ConstantRange.exactly(1))
				//
				.add(
						//
						AlternativesLootEntry.alternatives(
								//
								ItemLootEntry.lootTableItem(block).when(MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))),
								//
								ItemLootEntry.lootTableItem(lootItem)
										//
										.apply(SetCount.setCount(RandomValueRange.between(min, max)))
										//
										.apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
										//
										.apply(ExplosionDecay.explosionDecay())
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
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(block).when(MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1)))))

		);
		return LootTable.lootTable().withPool(builder);
	}

	protected LootTable.Builder createSimpleBlockTable(String name, Block block) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(block));
		return LootTable.lootTable().withPool(builder);
	}

	@Override
	public void run(DirectoryCache cache) {
		addTables();

		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());
		}

		Path outputFolder = generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				
				IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
				
			} catch (IOException e) {
				Voltaic.LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}
	
	protected abstract void addTables();

	@Override
	public String getName() {
		return modID + " LootTables";
	}

}
