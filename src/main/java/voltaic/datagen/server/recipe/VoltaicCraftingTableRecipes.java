package voltaic.datagen.server.recipe;

import com.google.common.collect.Lists;
import net.minecraft.world.item.crafting.Ingredient;
import voltaic.Voltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.common.recipe.recipeutils.EnchantmentIngredient;
import voltaic.common.tags.VoltaicTags;
import voltaic.datagen.utils.server.recipe.AbstractRecipeGenerator;
import voltaic.datagen.utils.server.recipe.ShapedCraftingRecipeBuilder;
import voltaic.datagen.utils.server.recipe.ShapelessCraftingRecipeBuilder;
import voltaic.registers.VoltaicItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public class VoltaicCraftingTableRecipes extends AbstractRecipeGenerator {

    public static final String ELECTRODYNAMICS_ID = "electrodynamics";

    @Override
    public void addRecipes(RecipeOutput output) {

        addGear(output);

        

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedcapacity), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedspeed), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basiccapacity), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basicspeed), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.experience), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.fortune), 1)
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
                .addKey('B', new EnchantmentIngredient(Ingredient.of(Items.ENCHANTED_BOOK), Lists.newArrayList(Tags.Enchantments.INCREASE_BLOCK_DROPS), false))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_fortune_noelectro", output);

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.improvedsolarcell), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemvoid), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.range), 1)
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


        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.silktouch), 1)
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
                .addKey('B', new EnchantmentIngredient(Ingredient.of(Items.ENCHANTED_BOOK), Lists.newArrayList(VoltaicTags.Enchantments.SILK_TOUCH), false))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_silk_touch_noelectro", output);

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.stator), 1)
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

        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.unbreaking), 1)
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
                .addKey('B', new EnchantmentIngredient(Ingredient.of(Items.ENCHANTED_BOOK), Lists.newArrayList(VoltaicTags.Enchantments.UNBREAKING), false))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_unbreaking_noelectro", output);

        ShapelessCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput), 1)
                //
                .addIngredient(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_output_reset", output);

        ShapelessCraftingRecipeBuilder.start(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput), 1)
                //
                .addIngredient(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput))
                //
                .addConditions(new NotCondition(new ModLoadedCondition(ELECTRODYNAMICS_ID)))
                //
                .complete(Voltaic.ID, "upgrade_item_input_reset", output);
    /*

        ShapelessCraftingRecipeBuilder.start(VoltaicItems.ITEM_ANTIDOTE.get(), 3)
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

        ShapelessCraftingRecipeBuilder.start(VoltaicItems.ITEM_IODINETABLET.get(), 3)
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

    private static void addGear(RecipeOutput output) {

        ShapelessCraftingRecipeBuilder.start(VoltaicItems.GUIDEBOOK.get(), 1)
                //
                .addIngredient(Items.BOOK)
                //
                .addIngredient(Tags.Items.INGOTS_COPPER)
                //
                .complete(Voltaic.ID, "guidebook", output);


        ShapedCraftingRecipeBuilder.start(VoltaicItems.ITEM_WRENCH.get(), 1)
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
