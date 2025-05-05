package voltaic.prefab.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.Voltaic;
import voltaic.api.screen.IScreenWrapper;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.prefab.inventory.container.GenericContainer;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;
import voltaic.prefab.screen.component.utils.AbstractScreenComponent;
import voltaic.prefab.screen.component.utils.SlotTextureProvider;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericScreen<T extends GenericContainer> extends ContainerScreen<T> implements IScreenWrapper {

	protected ResourceLocation defaultResource = Voltaic.rl("textures/screen/component/base.png");
	private List<AbstractScreenComponent> components = new ArrayList<>();
	public List<ScreenComponentSlot> slots = new ArrayList<>();
	private List<ScreenComponentEditBox> editBoxes = new ArrayList<>();
	protected int playerInvOffset = 0;

	// Ability to manipulate labels
	public ScreenComponentSimpleLabel guiTitle;
	public ScreenComponentSimpleLabel playerInvLabel;

	public GenericScreen(T container, PlayerInventory inv, ITextComponent title) {
		super(container, inv, title);
		initializeComponents();
	}

	protected void initializeComponents() {
		for (Slot slot : menu.slots) {
			ScreenComponentSlot component = createScreenSlot(slot);
			addComponent(component);
			slots.add(component);
		}
		addComponent(guiTitle = new ScreenComponentSimpleLabel(this.titleLabelX, this.titleLabelY, 10, Color.TEXT_GRAY, this.title));
		addComponent(playerInvLabel = new ScreenComponentSimpleLabel(this.inventoryLabelX, this.inventoryLabelY, 10, Color.TEXT_GRAY, this.inventory.getDisplayName()));
	}

	protected ScreenComponentSlot createScreenSlot(Slot slot) {
		if (slot instanceof SlotTextureProvider) {
			SlotTextureProvider provider = (SlotTextureProvider) slot;
			ISlotTexture texture = provider.getSlotType();
			return new ScreenComponentSlot(slot, texture, provider.getIconType(), slot.x + texture.xOffset(), slot.y + texture.yOffset());
		}
		return new ScreenComponentSlot(slot, SlotType.NORMAL, IconType.NONE, slot.x + SlotType.NORMAL.xOffset(), slot.y + SlotType.NORMAL.yOffset());
	}

	@Override
	public void tick() {
		super.tick();
		for (ScreenComponentEditBox box : editBoxes) {
			box.tick();
		}
	}

	@Override
	protected void init() {
		super.init();
		guiTitle.x = titleLabelX;
		guiTitle.y = titleLabelY;
		playerInvLabel.x = inventoryLabelX;
		playerInvLabel.y = inventoryLabelY;
		for (AbstractScreenComponent component : components) {
			addButton(component);
		}
	}

	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack poseStack, int mouseX, int mouseY) {
		// super.renderLabels(poseStack, mouseX, mouseY);
		int guiWidth = (int) getGuiWidth();
		int guiHeight = (int) getGuiHeight();
		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;
		for (AbstractScreenComponent component : components) {
			component.renderForeground(poseStack, xAxis, yAxis, guiWidth, guiHeight);
		}
	}

	@Override
	protected void renderBg(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
		// RenderingUtils.bindTexture(defaultResource);
		int guiWidth = (int) getGuiWidth();
		int guiHeight = (int) getGuiHeight();

		int y = guiHeight;
		
		RenderingUtils.bindTexture(defaultResource);

		blit(poseStack, guiWidth, y, 0, 0, 176, 4, 176, 18);

		y += 4;

		int wholeHeight = (imageHeight - 8) / 10;
		int remainder = (imageHeight - 8) % 10;

		for(int i = 0; i < wholeHeight; i++){
			blit(poseStack, guiWidth, y, 0, 4, 176, 10, 176, 18);
			y += 10;
		}

		blit(poseStack, guiWidth, y, 0, 4, 176, remainder, 176, 18);
		y += remainder;

		blit(poseStack, guiWidth, y, 0, 14, 176, 4, 176, 18);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		for (AbstractScreenComponent component : components) {
			component.preOnMouseClick(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public FontRenderer getFontRenderer() {
		return Minecraft.getInstance().font;
	}

	@Override
	public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
		List<String> strings = new ArrayList<>();
		List<ScreenComponentEditBox> boxes = new ArrayList<>(editBoxes);
		for (ScreenComponentEditBox box : boxes) {
			strings.add(box.getValue());
		}
		super.resize(pMinecraft, pWidth, pHeight);
		for (int i = 0; i < boxes.size(); i++) {
			boxes.get(i).setValue(strings.get(i));
		}
	}

	@Override
	public double getGuiWidth() {
		return (width - imageWidth) / 2.0;
	}

	@Override
	public double getGuiHeight() {
		return (height - imageHeight) / 2.0;
	}

	public double getXAxis(double mouseX) {
		return mouseX - getGuiWidth();
	}

	public double getYAxis(double mouseY) {
		return mouseY - getGuiHeight();
	}

	public AbstractScreenComponent addComponent(AbstractScreenComponent component) {
		components.add(component);
		component.setScreen(this);
		return component;
	}

	public List<AbstractScreenComponent> getComponents() {
		return components;
	}

	public void addEditBox(ScreenComponentEditBox box) {
		editBoxes.add(box);
		addComponent(box);
	}

	@Override
	public void displayTooltips(MatrixStack stack, List<? extends IReorderingProcessor> tooltips, int mouseX, int mouseY) {
		renderTooltip(stack, tooltips, mouseX, mouseY);
	}

	@Override
	public void displayTooltips(MatrixStack stack, List<? extends IReorderingProcessor> lines, int x, int y, Font font) {
		renderTooltip(stack, lines, x, y);
	}

}