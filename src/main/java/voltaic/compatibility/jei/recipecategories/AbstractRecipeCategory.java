package voltaic.compatibility.jei.recipecategories;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.api.screen.ITexture;
import voltaic.compatibility.jei.utils.RecipeType;
import voltaic.compatibility.jei.utils.gui.ScreenObject;
import voltaic.compatibility.jei.utils.gui.types.ArrowAnimatedObject;
import voltaic.compatibility.jei.utils.gui.types.BackgroundObject;
import voltaic.compatibility.jei.utils.gui.types.ItemSlotObject;
import voltaic.compatibility.jei.utils.gui.types.fluidgauge.AbstractFluidGaugeObject;
import voltaic.compatibility.jei.utils.label.AbstractLabelWrapper;
import voltaic.prefab.utilities.math.MathUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractRecipeCategory<T> implements IRecipeCategory<T> {

	protected int animationTime;

	protected ITextComponent title;

	protected IDrawable background;
	protected IDrawable icon;

	protected RecipeType<T> recipeType;

	public AbstractLabelWrapper[] labels = new AbstractLabelWrapper[0];

	protected ArrayList<AnimatedWrapper> animatedDrawables = new ArrayList<>();
	protected ArrayList<StaticWrapper> staticDrawables = new ArrayList<>();

	protected SlotDataWrapper[] inputSlotWrappers = new SlotDataWrapper[0];
	protected SlotDataWrapper[] outputSlotWrappers = new SlotDataWrapper[0];
	protected AbstractFluidGaugeObject[] fluidInputWrappers = new AbstractFluidGaugeObject[0];
	protected AbstractFluidGaugeObject[] fluidOutputWrappers = new AbstractFluidGaugeObject[0];

	public AbstractRecipeCategory(IGuiHelper guiHelper, ITextComponent title, ItemStack inputMachine, BackgroundObject wrapper, RecipeType<T> recipeType, int animationTime) {

		this.title = title;

		this.recipeType = recipeType;

		icon = guiHelper.createDrawableIngredient(inputMachine);

		ITexture texture = wrapper.getTexture();
		background = guiHelper.drawableBuilder(texture.getLocation(), wrapper.getX(), wrapper.getY(), wrapper.getWidth(), wrapper.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build();

		this.animationTime = animationTime;
	}

	@Override
	public ResourceLocation getUid() {
		return recipeType.getUid();
	}

	@Override
	public ITextComponent getTitleAsTextComponent() {
		return title;
	}

	@Override
	public Class<? extends T> getRecipeClass() {
		return recipeType.getRecipeClass();
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(T recipe, MatrixStack poseStack, double mouseX, double mouseY) {

		drawnBackground(recipe, poseStack);

		drawPre(poseStack, recipe);

		drawStatic(poseStack);
		drawAnimated(poseStack);

		drawPost(poseStack, recipe);

		preLabels(poseStack, recipe);

		addLabels(poseStack, recipe);

		postLabels(poseStack, recipe);
	}

	public void drawnBackground(T recipe, MatrixStack poseStack) {
		// background.draw(poseStack);
	}

	@Override
	public void setRecipe(IRecipeLayout builder, T recipe, IIngredients focuses) {
		int index = 0;

		index = setItemInputs(index, builder, focuses);
		index = setFluidInputs(index, getFluidInputs(recipe), builder, focuses);
		index = setItemOutputs(index, getItemOutputs(recipe).size(), builder, focuses);
		index = setFluidOutputs(index, getFluidOutputs(recipe), builder, focuses);
	}

	@Override
	public void setIngredients(T recipe, IIngredients ingredients) {
		List<List<ItemStack>> inputs = getItemInputs(recipe);
		if (!inputs.isEmpty()) {
			ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		}
		List<ItemStack> outputs = getItemOutputs(recipe);
		if (!outputs.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.ITEM, outputs);
		}
		List<List<FluidStack>> fluidInputs = getFluidInputs(recipe);
		if (!fluidInputs.isEmpty()) {
			ingredients.setInputLists(VanillaTypes.FLUID, fluidInputs);
		}
		List<FluidStack> fluidOutputs = getFluidOutputs(recipe);
		if (!fluidOutputs.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.FLUID, fluidOutputs);
		}

	}

	public int getAnimationTime() {
		return animationTime;
	}

	public void setLabels(AbstractLabelWrapper... labels) {
		this.labels = labels;
	}

	public void setInputSlots(IGuiHelper guiHelper, ItemSlotObject... inputSlots) {
		inputSlotWrappers = new SlotDataWrapper[inputSlots.length];
		ItemSlotObject slot;
		for (int i = 0; i < inputSlots.length; i++) {
			slot = inputSlots[i];
			inputSlotWrappers[i] = new SlotDataWrapper(slot.getItemXStart(), slot.getItemYStart(), slot.isInput());
			ITexture texture = slot.getTexture();
			staticDrawables.add(new StaticWrapper(slot.getX(), slot.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), slot.getWidth(), slot.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
			if (slot.getIcon() != null) {
				ScreenObject icon = slot.getIcon();
				texture = icon.getTexture();
				staticDrawables.add(new StaticWrapper(icon.getX(), icon.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), icon.getWidth(), icon.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
			}
		}
	}

	public void setOutputSlots(IGuiHelper guiHelper, ItemSlotObject... outputSlots) {
		outputSlotWrappers = new SlotDataWrapper[outputSlots.length];
		ItemSlotObject slot;
		for (int i = 0; i < outputSlots.length; i++) {
			slot = outputSlots[i];
			outputSlotWrappers[i] = new SlotDataWrapper(slot.getItemXStart(), slot.getItemYStart(), slot.isInput());
			ITexture texture = slot.getTexture();
			staticDrawables.add(new StaticWrapper(slot.getX(), slot.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), slot.getWidth(), slot.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
			if (slot.getIcon() != null) {
				ScreenObject icon = slot.getIcon();
				texture = icon.getTexture();
				staticDrawables.add(new StaticWrapper(icon.getX(), icon.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), icon.getWidth(), icon.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
			}
		}
	}

	public void setFluidInputs(IGuiHelper guiHelper, AbstractFluidGaugeObject... gauges) {
		fluidInputWrappers = gauges;
		for (AbstractFluidGaugeObject gauge : fluidInputWrappers) {
			ITexture texture = gauge.getTexture();
			staticDrawables.add(new StaticWrapper(gauge.getX(), gauge.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), gauge.getWidth(), gauge.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
		}
	}

	public void setFluidOutputs(IGuiHelper guiHelper, AbstractFluidGaugeObject... gauges) {
		fluidOutputWrappers = gauges;
		for (AbstractFluidGaugeObject gauge : fluidOutputWrappers) {
			ITexture texture = gauge.getTexture();
			staticDrawables.add(new StaticWrapper(gauge.getX(), gauge.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), gauge.getWidth(), gauge.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));
		}
	}

	public void setScreenObjects(IGuiHelper guiHelper, ScreenObject... objects) {

		for (ScreenObject object : objects) {

			ITexture texture = object.getTexture();
			staticDrawables.add(new StaticWrapper(object.getX(), object.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), object.getWidth(), object.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));

		}

	}

	public void setAnimatedArrows(IGuiHelper guiHelper, ArrowAnimatedObject... arrows) {

		ScreenObject staticArrow;

		for (ArrowAnimatedObject arrow : arrows) {

			ITexture texture = arrow.getTexture();
			animatedDrawables.add(new AnimatedWrapper(arrow.getX(), arrow.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), arrow.getWidth(), arrow.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).buildAnimated(getAnimationTime(), arrow.startDirection(), false)));

			staticArrow = arrow.getOffArrow();

			texture = staticArrow.getTexture();
			staticDrawables.add(new StaticWrapper(staticArrow.getX(), staticArrow.getY(), guiHelper.drawableBuilder(texture.getLocation(), texture.textureU(), texture.textureV(), staticArrow.getWidth(), staticArrow.getHeight()).setTextureSize(texture.imageWidth(), texture.imageHeight()).build()));

		}

	}

	public int setItemInputs(int index, IRecipeLayout builder, IIngredients ingredients) {
		SlotDataWrapper wrapper;

		IGuiItemStackGroup group = builder.getItemStacks();
		
		for (int i = 0; i < inputSlotWrappers.length; i++) {
			wrapper = inputSlotWrappers[i];

			group.init(index, wrapper.input, wrapper.x - 1, wrapper.y - 1);
			index++;
		}
		
		return index;
	}

	public int setItemOutputs(int index, int outputSize, IRecipeLayout builder, IIngredients ingredients) {
		SlotDataWrapper wrapper;

		IGuiItemStackGroup group = builder.getItemStacks();

		for (int i = 0; i < outputSlotWrappers.length; i++) {
			wrapper = outputSlotWrappers[i];
			if (i < outputSize) {

				group.init(index, wrapper.input, wrapper.x - 1, wrapper.y - 1);
				index++;
			}
		}

		group.set(ingredients);
		
		return index;
	}

	public int setFluidInputs(int index, List<List<FluidStack>> inputs, IRecipeLayout builder, IIngredients ingredients) {
		AbstractFluidGaugeObject wrapper;

		IGuiFluidStackGroup group = builder.getFluidStacks();

		FluidStack stack;

		int maxGaugeCap = 0;

		for (List<FluidStack> stacks : inputs) {
			stack = stacks.get(0);
			int gaugeCap = (int) Math.pow(10, MathUtils.nearestPowerOf10(stack.getAmount(), true));
			if (gaugeCap > maxGaugeCap) {
				maxGaugeCap = gaugeCap;
			}
		}

		for (int i = 0; i < fluidInputWrappers.length; i++) {
			wrapper = fluidInputWrappers[i];
			stack = inputs.get(i).get(0);

			int amt = stack.getAmount();

			// int gaugeCap = (int) Math.pow(10, MathUtils.nearestPowerOf10(amt, true));

			int height = (int) Math.ceil((float) amt / (float) maxGaugeCap * wrapper.getFluidTextHeight());

			group.init(index, true, wrapper.getFluidXPos(), wrapper.getFluidYPos() - height, wrapper.getFluidTextWidth(), height, stack.getAmount(), true, null);
			index++;
		}
		return index;
	}

	public int setFluidOutputs(int index, List<FluidStack> outputs, IRecipeLayout builder, IIngredients ingredients) {
		AbstractFluidGaugeObject wrapper;

		IGuiFluidStackGroup group = builder.getFluidStacks();

		FluidStack stack;

		int maxGaugeCap = 0;

		for (FluidStack s : outputs) {
			int gaugeCap = (int) Math.pow(10, MathUtils.nearestPowerOf10(s.getAmount(), true));
			if (gaugeCap > maxGaugeCap) {
				maxGaugeCap = gaugeCap;
			}
		}

		for (int i = 0; i < fluidOutputWrappers.length; i++) {
			wrapper = fluidOutputWrappers[i];
			stack = outputs.get(i);

			int amt = stack.getAmount();

			// int gaugeCap = (int) Math.pow(10, MathUtils.nearestPowerOf10(amt, true));

			int height = (int) Math.ceil((float) amt / (float) maxGaugeCap * wrapper.getFluidTextHeight());
			group.init(index, false, wrapper.getFluidXPos(), wrapper.getFluidYPos() - height, wrapper.getFluidTextWidth(), height, stack.getAmount(), true, null);
			index++;
		}
		return index;
	}

	public void drawStatic(MatrixStack poseStack) {
		for (StaticWrapper wrapper : staticDrawables) {
			wrapper.stat.draw(poseStack, wrapper.x, wrapper.y);
		}
	}

	public void drawAnimated(MatrixStack poseStack) {
		for (AnimatedWrapper wrapper : animatedDrawables) {
			wrapper.anim.draw(poseStack, wrapper.x, wrapper.y);
		}
	}

	public void drawPre(MatrixStack poseStack, T recipe) {

	}

	public void drawPost(MatrixStack poseStack, T recipe) {

	}

	public void addLabels(MatrixStack poseStack, T recipe) {
		FontRenderer font = Minecraft.getInstance().font;
		for (AbstractLabelWrapper wrap : labels) {
			ITextComponent text = wrap.getComponent(this, recipe);
			if (wrap.xIsEnd()) {
				font.draw(poseStack, text, wrap.getXPos() - font.width(text.getVisualOrderText()), wrap.getYPos(), wrap.getColor().color());
			} else {
				font.draw(poseStack, text, wrap.getXPos(), wrap.getYPos(), wrap.getColor().color());
			}
		}
	}

	public void preLabels(MatrixStack poseStack, T recipe) {

	}

	public void postLabels(MatrixStack poseStack, T recipe) {

	}

	public List<List<ItemStack>> getItemInputs(T recipe) {
		return new ArrayList<>();
	}

	public List<ItemStack> getItemOutputs(T recipe) {
		return new ArrayList<>();
	}

	public List<List<FluidStack>> getFluidInputs(T recipe) {
		return new ArrayList<>();
	}

	public List<FluidStack> getFluidOutputs(T recipeo) {
		return new ArrayList<>();
	}

	private static class SlotDataWrapper {

		private final int x;
		private final int y;
		private final boolean input;

		private SlotDataWrapper(int x, int y, boolean input) {
			this.x = x;
			this.y = y;
			this.input = input;
		}

	}

	private static class StaticWrapper {

		private final int x;
		private final int y;
		private final IDrawableStatic stat;

		private StaticWrapper(int x, int y, IDrawableStatic stat) {
			this.x = x;
			this.y = y;
			this.stat = stat;
		}

	}

	private static class AnimatedWrapper {

		private final int x;
		private final int y;
		private final IDrawableAnimated anim;

		private AnimatedWrapper(int x, int y, IDrawableAnimated anim) {

			this.x = x;
			this.y = y;
			this.anim = anim;

		}

	}

}
