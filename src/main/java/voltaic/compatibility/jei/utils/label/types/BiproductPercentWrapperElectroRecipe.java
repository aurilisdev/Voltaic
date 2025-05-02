package voltaic.compatibility.jei.utils.label.types;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.common.recipe.VoltaicRecipe;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableItem;
import voltaic.compatibility.jei.recipecategories.AbstractRecipeCategory;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class BiproductPercentWrapperElectroRecipe extends AbstractLabelWrapper {

	private final BiproductType type;
	private final int index;

	public BiproductPercentWrapperElectroRecipe(int xPos, int yPos, BiproductType type, int index) {
		super(Color.JEI_TEXT_GRAY, yPos, xPos, false);
		this.type = type;
		this.index = index;
	}

	@Override
	public Component getComponent(AbstractRecipeCategory<?> category, Object recipe) {

		switch(type){
			case ITEM:
				VoltaicRecipe electro = (VoltaicRecipe) recipe;
				if (electro.hasItemBiproducts() && index < electro.getItemBiproducts().size()) {
					ProbableItem item = electro.getItemBiproducts().get(index);
					return ChatFormatter.getChatDisplayShort(item.getChance() * 100, DisplayUnits.PERCENTAGE);
				}
				break;
			case FLUID:
				electro = (VoltaicRecipe) recipe;
				if (electro.hasFluidBiproducts() && index < electro.getFluidBiproducts().size()) {
					ProbableFluid item = electro.getFluidBiproducts().get(index);
					return ChatFormatter.getChatDisplayShort(item.getChance() * 100, DisplayUnits.PERCENTAGE);
				}
				break;
		}
		return TextComponent.EMPTY;
	}

	public static enum BiproductType {
		ITEM, FLUID;
	}
}
