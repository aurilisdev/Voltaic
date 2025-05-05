package voltaic.prefab.screen.component.types;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.api.screen.component.ISlotTexture;
import voltaic.api.screen.component.TextSupplier;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.utils.IUpgradeSlot;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.VoltaicTextUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenComponentSlot extends ScreenComponentGeneric {

	private final ISlotTexture slotType;
	private final ITexture iconType;
	private TextSupplier tooltip;

	private final Slot slot;

	public ScreenComponentSlot(Slot slot, ISlotTexture slotType, ITexture iconType, int x, int y) {
		super(slotType, x, y);
		this.slotType = slotType;
		this.iconType = iconType;
		this.slot = slot;
		onTooltip((poseStack, component, xAxis, yAxis) -> {
			if(tooltip == null){
				return;
			}
			gui.displayTooltip(poseStack, tooltip.getText().getVisualOrderText(), xAxis, yAxis);
		});
	}

	public ScreenComponentSlot(Slot slot, int x, int y) {
		this(slot, SlotType.NORMAL, IconType.NONE, x, y);
	}

	public ScreenComponentSlot(Slot slot, ISlotTexture slotType, ITexture iconType, int x, int y, TextSupplier tooltip) {
		this(slot, slotType, iconType, x, y);
		this.tooltip = tooltip;
	}

	public void tooltip(TextSupplier tooltip) {
		this.tooltip = tooltip;
	}

	public ScreenComponentSlot setHoverText(Slot slot) {
		tooltip = () -> slot.getItem().isEmpty() ? VoltaicTextUtils.empty() : slot.getItem().getHoverName().copy();
		return this;
	}

	@Override
	public void renderBackground(MatrixStack poseStack, final int xAxis, final int yAxis, final int guiWidth, final int guiHeight) {
		super.renderBackground(poseStack, xAxis, yAxis, guiWidth, guiHeight);
		if (iconType == IconType.NONE) {
			return;
		}
		int slotXOffset = (slotType.imageWidth() - iconType.imageWidth()) / 2;
		int slotYOffset = (slotType.imageHeight() - iconType.imageHeight()) / 2;
		RenderingUtils.bindTexture(iconType.getLocation());
		blit(poseStack, guiWidth + x + slotXOffset, guiHeight + y + slotYOffset, iconType.textureU(), iconType.textureV(), iconType.textureWidth(), iconType.textureHeight(), iconType.imageWidth(), iconType.imageHeight());
	}

	@Override
	public void renderForeground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if (!slot.isActive()) {
			return;
		}
		super.renderForeground(poseStack, xAxis, yAxis, guiWidth, guiHeight);
		if (isHoveredOrFocused()) {

			if (Screen.hasControlDown() && slot instanceof IUpgradeSlot) {
				
				IUpgradeSlot upgrade = (IUpgradeSlot) slot;

				List<IReorderingProcessor> tooltips = new ArrayList<>();
				tooltips.add(VoltaicTextUtils.tooltip("validupgrades").getVisualOrderText());
				for (SubtypeItemUpgrade item : upgrade.getUpgrades()) {
					tooltips.add(item.name.withStyle(TextFormatting.GRAY).getVisualOrderText());
				}
				gui.displayTooltips(poseStack, tooltips, xAxis, yAxis);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return slot.isActive();
	}

	public enum SlotType implements ISlotTexture {

		NORMAL(18, 18, 0, 0, 18, 18, "slot_regular"), //
		BIG(26, 26, 0, 0, 26, 26, "slot_big", -5, -5);

		private final int textureWidth;
		private final int textureHeight;
		private final int textureU;
		private final int textureV;
		private final int imageWidth;
		private final int imageHeight;
		private final ResourceLocation loc;

		private final int xOffset;
		private final int yOffset;

		SlotType(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, String name) {
			this(textureWidth, textureHeight, textureU, textureV, imageWidth, imageHeight, name, -1, -1);
		}

		SlotType(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, String name, int xOffset, int yOffset) {
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.textureU = textureU;
			this.textureV = textureV;
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			loc = Voltaic.rl("textures/screen/component/slot/" + name + ".png");

			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		@Override
		public ResourceLocation getLocation() {
			return loc;
		}

		@Override
		public int imageHeight() {
			return imageHeight;
		}

		@Override
		public int imageWidth() {
			return imageWidth;
		}

		@Override
		public int textureHeight() {
			return textureHeight;
		}

		@Override
		public int textureU() {
			return textureU;
		}

		@Override
		public int textureV() {
			return textureV;
		}

		@Override
		public int textureWidth() {
			return textureWidth;
		}

		@Override
		public int xOffset() {
			return xOffset;
		}

		@Override
		public int yOffset() {
			return yOffset;
		}

	}

	public enum IconType implements ITexture {

		NONE(0, 0, 0, 0, 0, 0, null), //
		ENERGY_DARK(10, 10, 0, 0, 10, 10, "electricity_dark"), //
		FLUID_DARK(14, 14, 0, 0, 14, 14, "fluid_dark"), //
		GAS_DARK(16, 16, 0, 0, 16, 16, "gas_dark"), //
		UPGRADE_DARK(12, 12, 0, 0, 12, 12, "upgrade_dark"), //
		DRILL_HEAD_DARK(12, 12, 0, 0, 12, 12, "drill_head_dark"), //
		TRASH_CAN_DARK(10, 10, 0, 0, 10, 10, "trash_can_dark"), //
		FIBERGLASS_SHEET_DARK(16, 16, 0, 0, 16, 16, "fiberglasssheet_dark"), //

		ENERGY_GREEN(14, 14, 0, 0, 14, 14, "electricity_green"), //
		ENCHANTMENT(16, 16, 0, 0, 16, 16, "enchantment"), //
		FLUID_BLUE(16, 16, 0, 0, 16, 16, "fluid_blue"), //
		MINING_LOCATION(18, 18, 0, 0, 18, 18, "mining_location"), //
		QUARRY_COMPONENTS(18, 18, 0, 0, 18, 18, "quarry_components"), //
		TEMPERATURE(14, 14, 0, 0, 14, 14, "temperature"), //
		THERMOMETER(16, 16, 0, 0, 16, 16, "thermometer"), //
		PRESSURE_GAUGE(10, 10, 0, 0, 10, 10, "pressuredial"), //
		INVENTORY_IO(16, 16, 0, 0, 16, 16, "inventoryio"), //
		SONAR_PROFILE(16, 16, 0, 0, 16, 16, "sonarpattern"), //
		CUBE_OUTLINE(16,16, 0, 0, 16, 16, "cubeoutline") //
		; //

		private final int textureWidth;
		private final int textureHeight;
		private final int textureU;
		private final int textureV;
		private final int imageWidth;
		private final int imageHeight;
		private final ResourceLocation loc;

		IconType(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, String name) {
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.textureU = textureU;
			this.textureV = textureV;
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			loc = Voltaic.rl("textures/screen/component/icon/" + name + ".png");
		}

		@Override
		public ResourceLocation getLocation() {
			return loc;
		}

		@Override
		public int imageHeight() {
			return imageHeight;
		}

		@Override
		public int imageWidth() {
			return imageWidth;
		}

		@Override
		public int textureHeight() {
			return textureHeight;
		}

		@Override
		public int textureU() {
			return textureU;
		}

		@Override
		public int textureV() {
			return textureV;
		}

		@Override
		public int textureWidth() {
			return textureWidth;
		}

	}

}