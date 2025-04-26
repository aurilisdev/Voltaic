package voltaic.registers;

import voltaic.Voltaic;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.EnchantmentIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class VoltaicIngredients {
    
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, Voltaic.ID);
    
    /* INGREDIENT TYPES */
    
    public static final DeferredHolder<IngredientType<?>, IngredientType<CountableIngredient>> COUNTABLE_INGREDIENT_TYPE = INGREDIENT_TYPES.register("countableingredient", () -> new IngredientType<>(CountableIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<FluidIngredient>> FLUID_INGREDIENT_TYPE = INGREDIENT_TYPES.register("fluidingredient", () -> new IngredientType<>(FluidIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<GasIngredient>> GAS_INGREDIENT_TYPE = INGREDIENT_TYPES.register("gasingredient", () -> new IngredientType<>(GasIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<EnchantmentIngredient>> ENCHANTMENT_INGREDIENT_TYPE = INGREDIENT_TYPES.register("enchantmentingredient", () -> new IngredientType<>(EnchantmentIngredient.CODEC));

}
