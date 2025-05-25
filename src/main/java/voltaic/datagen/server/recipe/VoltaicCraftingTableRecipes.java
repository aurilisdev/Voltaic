package voltaic.datagen.server.recipe;

import voltaic.Voltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.datagen.utils.server.recipe.AbstractRecipeGenerator;
import voltaic.datagen.utils.server.recipe.CustomShapedCraftingRecipe;
import voltaic.datagen.utils.server.recipe.CustomShapelessCraftingRecipe;
import voltaic.registers.VoltaicItems;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

public class VoltaicCraftingTableRecipes extends AbstractRecipeGenerator {

    public static final String ELECTRODYNAMICS_ID = "electrodynamics";

    @Override
    public void addRecipes(Consumer<FinishedRecipe> output) {

        addGear(output);


        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedcapacity), 1)
                //
                .addPattern("PBP")
                //
                .addPattern("BWB")
                //
                .addPattern("CBC")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('B', VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basiccapacity))
                //
                .addKey('W', Tags.Items.INGOTS_COPPER)
                //
                .addKey('C', Tags.Items.GEMS_DIAMOND)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_advanced_capacity_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedspeed), 1)
                //
                .addPattern("PGP")
                //
                .addPattern("BWB")
                //
                .addPattern("CGC")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('G', Items.CHAIN)
                //
                .addKey('B', VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basicspeed))
                //
                .addKey('W', Tags.Items.INGOTS_COPPER)
                //
                .addKey('C', Tags.Items.GEMS_DIAMOND)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_advanced_speed_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basiccapacity), 1)
                //
                .addPattern("PBP")
                //
                .addPattern("BWB")
                //
                .addPattern("CBC")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('B', Tags.Items.DUSTS_REDSTONE)
                //
                .addKey('W', Tags.Items.INGOTS_COPPER)
                //
                .addKey('C', Tags.Items.INGOTS_GOLD)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_basic_capacity_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basicspeed), 1)
                //
                .addPattern("PGP")
                //
                .addPattern("WWW")
                //
                .addPattern("CGC")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('G', Items.CHAIN)
                //
                .addKey('W', Items.SUGAR)
                //
                .addKey('C', Tags.Items.INGOTS_GOLD)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_basic_speed_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.experience), 1)
                //
                .addPattern("PBP")
                //
                .addPattern("BWB")
                //
                .addPattern("PBP")
                //
                .addKey('P', Tags.Items.INGOTS_GOLD)
                //
                .addKey('B', Items.GLASS_BOTTLE)
                //
                .addKey('W', Tags.Items.INGOTS_COPPER)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_experience_noelectro", output);
        
        ItemStack fortuneBook = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(fortuneBook, new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 1));
        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.fortune), 1)
                //
                .addPattern("PCP")
                //
                .addPattern("CBC")
                //
                .addPattern("PCP")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.GEMS_EMERALD)
                //
                .addKey('B', PartialNBTIngredient.of(fortuneBook.getItem(), fortuneBook.getTag()))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_fortune_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.improvedsolarcell), 1)
                //
                .addPattern("PPP")
                //
                .addPattern("BCB")
                //
                .addPattern("BSB")
                //
                .addKey('P', Tags.Items.GLASS_PANES)
                //
                .addKey('B', Tags.Items.DUSTS_REDSTONE)
                //
                .addKey('C', Tags.Items.INGOTS_GOLD)
                //
                .addKey('S', Tags.Items.INGOTS_IRON)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_improved_solar_cell_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput), 1)
                //
                .addPattern("C")
                //
                .addPattern("P")
                //
                .addPattern("A")
                //
                .addKey('A', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.INGOTS_GOLD)
                //
                .addKey('P', Items.STICKY_PISTON)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_input_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput), 1)
                //
                .addPattern("C")
                //
                .addPattern("P")
                //
                .addPattern("A")
                //
                .addKey('A', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.INGOTS_GOLD)
                //
                .addKey('P', Items.PISTON)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_output_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemvoid), 1)
                //
                .addPattern("C")
                //
                .addPattern("B")
                //
                .addPattern("P")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Items.CACTUS)
                //
                .addKey('B', Tags.Items.INGOTS_GOLD)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_void_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.range), 1)
                //
                .addPattern("PWP")
                //
                .addPattern("WBW")
                //
                .addPattern("PWP")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('W', Tags.Items.INGOTS_COPPER)
                //
                .addKey('B', Tags.Items.INGOTS_GOLD)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_range_noelectro", output);

        ItemStack silkTouchBook = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(silkTouchBook, new EnchantmentInstance(Enchantments.SILK_TOUCH, 1));
        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.silktouch), 1)
                //
                .addPattern("PCP")
                //
                .addPattern("CBC")
                //
                .addPattern("PCP")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.GEMS_AMETHYST)
                //
                .addKey('B', PartialNBTIngredient.of(silkTouchBook.getItem(), silkTouchBook.getTag()))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_silk_touch_noelectro", output);

        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.stator), 1)
                //
                .addPattern("PCP")
                //
                .addPattern("CRC")
                //
                .addPattern("PCP")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.INGOTS_COPPER)
                //
                .addKey('R', Tags.Items.DUSTS_REDSTONE)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_stator_noelectro", output);

        ItemStack unbreakingBook = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantedBookItem.addEnchantment(unbreakingBook, new EnchantmentInstance(Enchantments.UNBREAKING, 1));
        CustomShapedCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.unbreaking), 1)
                //
                .addPattern("PCP")
                //
                .addPattern("CBC")
                //
                .addPattern("PCP")
                //
                .addKey('P', Tags.Items.INGOTS_IRON)
                //
                .addKey('C', Tags.Items.GEMS_DIAMOND)
                //
                .addKey('B', PartialNBTIngredient.of(unbreakingBook.getItem(), unbreakingBook.getTag()))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_unbreaking_noelectro", output);

        CustomShapelessCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput), 1)
                //
                .addIngredient(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_output_reset", output);

        CustomShapelessCraftingRecipe.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput), 1)
                //
                .addIngredient(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_input_reset", output);

        /*

        CustomShapelessCraftingRecipe.start(VoltaicItems.ITEM_ANTIDOTE.get(), 3)
                //
                .addIngredient(Items.GLASS_BOTTLE)
                //
                .addIngredient(Items.GLASS_BOTTLE)
                //
                .addIngredient(Items.GLASS_BOTTLE)
                //
                .addIngredient(ItemTags.FISHES)
                //
                .complete(Voltaic.ID, "antidote", output);

        CustomShapelessCraftingRecipe.start(VoltaicItems.ITEM_IODINETABLET.get(), 3)
                //
                .addIngredient(Tags.Items.GEMS_AMETHYST)
                //
                .addIngredient(Tags.Items.GEMS_AMETHYST)
                //
                .addIngredient(Tags.Items.GEMS_AMETHYST)
                //
                .addIngredient(ItemTags.FISHES)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "iodinetablet", output);

         */

    }

    private static void addGear(Consumer<FinishedRecipe> output) {

        CustomShapelessCraftingRecipe.start(VoltaicItems.GUIDEBOOK.get(), 1)
                //
                .addIngredient(Items.BOOK)
                //
                .addIngredient(Tags.Items.INGOTS_COPPER)
                //
                .complete(Voltaic.ID, "guidebook", output);


        CustomShapedCraftingRecipe.start(VoltaicItems.ITEM_WRENCH.get(), 1)
                //
                .addPattern(" S ")
                //
                .addPattern(" SS")
                //
                .addPattern("S  ")
                //
                .addKey('S', Tags.Items.INGOTS_IRON)
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "wrench_noelectro", output);

    }


}
