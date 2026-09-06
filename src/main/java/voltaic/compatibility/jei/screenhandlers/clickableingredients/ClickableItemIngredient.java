package voltaic.compatibility.jei.screenhandlers.clickableingredients;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

public class ClickableItemIngredient extends AbstractClickableIngredient<ItemStack> {

    private final ItemStack ingredient;
    private final ITypedIngredient<ItemStack> typedIngredient;

    public ClickableItemIngredient(Rect2i rect, ItemStack stack) {
	super(rect);
	ingredient = stack;
	typedIngredient = new ItemIngredientType(stack);
    }

    @Override
    public IIngredientType<ItemStack> getIngredientType() {
	return VanillaTypes.ITEM_STACK;
    }

    @Override
    public ItemStack getIngredient() {
	return ingredient;
    }

    @Override
    @Deprecated
    public ITypedIngredient<ItemStack> getTypedIngredient() {
	return typedIngredient;
    }

    private record ItemIngredientType(ItemStack ingredient) implements ITypedIngredient<ItemStack> {

	@Override
	public IIngredientType<ItemStack> getType() {
	    return VanillaTypes.ITEM_STACK;
	}

	@Override
	public ItemStack getIngredient() {
	    return ingredient;
	}

    }

}