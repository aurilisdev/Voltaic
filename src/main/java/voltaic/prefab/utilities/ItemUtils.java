package voltaic.prefab.utilities;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemUtils {

	public static boolean testItems(Item comparator, Item... itemsToCompare) {
		ItemStack stack = new ItemStack(comparator);
		for (Item item : itemsToCompare) {
			if (stack.getItem() == item) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an Ingredient from the input tag
	 * 
	 * @param location The mod location e.g "forge", "minecraft"
	 * @param tag      The tag in question e.g. "ingots/gold", "planks"
	 * @return An Ingredient based on the tag
	 */
	@Nullable
	public static Ingredient getIngredientFromTag(String location, String tag) {
		return Ingredient.of(ItemTags.createOptional(new ResourceLocation(location, tag)));
	}

	public static Item fromBlock(Block block) {
		return new ItemStack(block).getItem();
	}

	public static boolean isIngredientMember(Ingredient ing, Item item) {
		for (ItemStack stack : ing.getItems()) {
			if (testItems(item, stack.getItem())) {
				return true;
			}
		}
		return false;
	}

	public static void removeEnchantment(ItemStack item, Enchantment enchantment) {
		item.removeTagKey("Enchantments");
		item.removeTagKey("StoredEnchantments");

		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item).entrySet().stream().filter((p_217012_0_) -> {
			return p_217012_0_.getKey().isCurse();
		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		EnchantmentHelper.setEnchantments(map, item);
	}

	public static ActionResult<ItemStack> startUsingInstantly(World pLevel, PlayerEntity pPlayer, Hand pHand) {
		pPlayer.startUsingItem(pHand);
		return ActionResult.consume(pPlayer.getItemInHand(pHand));
	}

}
