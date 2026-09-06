package voltaic.common.recipe.recipeutils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import voltaic.registers.VoltaicIngredients;

public class EnchantmentIngredient implements ICustomIngredient {

    public static final MapCodec<EnchantmentIngredient> CODEC = RecordCodecBuilder.mapCodec(
	    builder -> builder.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(instance -> instance.ingredient),
		    TagKey.codec(Registries.ENCHANTMENT).listOf().fieldOf("enchantments")
			    .forGetter(instance -> instance.enchantments),
		    Codec.BOOL.fieldOf("isStrict").forGetter(instance -> instance.isStrict)

	    ).apply(builder, EnchantmentIngredient::new)

    );

    private final Ingredient ingredient;
    private final List<TagKey<Enchantment>> enchantments;

    private final boolean isStrict;

    public EnchantmentIngredient(Ingredient base, List<TagKey<Enchantment>> enchantments, boolean isStrict) {
	ingredient = base;
	this.enchantments = enchantments;
	this.isStrict = isStrict;
    }

    @Override
    public boolean test(ItemStack stack) {
	if (!ingredient.test(stack))
	    return false;

	if (isStrict)
	    return matchesExactly(stack.getTagEnchantments())
		    || matchesExactly(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));

	ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

	for (TagKey<Enchantment> tag : enchantments) {
	    if (EnchantmentHelper.hasTag(stack, tag)
		    || stored.keySet().stream().anyMatch(enchantment -> enchantment.is(tag)))
		return true;
	}

	return false;
    }

    private boolean matchesExactly(ItemEnchantments current) {
	if (current.isEmpty() || current.keySet().size() != enchantments.size())
	    return false;

	return current.keySet().stream().allMatch(enchantment -> enchantments.stream().anyMatch(enchantment::is));
    }

    @Override
    public Stream<ItemStack> getItems() {
	return Stream.of(ingredient.getItems());
    }

    @Override
    public boolean isSimple() {
	return false;
    }

    @Override
    public IngredientType<?> getType() {
	return VoltaicIngredients.ENCHANTMENT_INGREDIENT_TYPE.get();
    }

    @Override
    public String toString() {
	return "items: " + Arrays.toString(ingredient.getItems()) + ", enchants: "
		+ StringUtils.join(enchantments.iterator(), ", ") + ", is strict: " + isStrict;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
	if (obj instanceof EnchantmentIngredient ing)
	    return ing.isStrict == isStrict && ing.ingredient.equals(ingredient)
		    && ing.enchantments.equals(enchantments);
	return false;
    }
}
