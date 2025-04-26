package voltaic.prefab.utilities;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;

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
		return Ingredient.of(ItemTags.create(new ResourceLocation(location, tag)));
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
		ListTag listtag = item.getOrCreateTag().getList("Enchantments", 10);
		Iterator<Tag> iterator = listtag.iterator();
		CompoundTag itTag;
		while(iterator.hasNext()) {
			itTag = (CompoundTag) iterator.next();
			if(itTag.contains("id") && new ResourceLocation(itTag.getString("id")).equals(EnchantmentHelper.getEnchantmentId(enchantment))) {
				iterator.remove();
			}
		}
	}

}
