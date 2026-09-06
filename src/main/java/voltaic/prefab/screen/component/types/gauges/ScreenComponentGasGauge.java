package voltaic.prefab.screen.component.types.gauges;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import voltaic.Voltaic;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.gas.GasAction;
import voltaic.api.gas.GasStack;
import voltaic.api.gas.IGasHandlerItem;
import voltaic.api.gas.PropertyGasTank;
import voltaic.api.gas.utils.IGasTank;
import voltaic.api.screen.ITexture;
import voltaic.client.VoltaicClientRegister;
import voltaic.common.packet.types.server.PacketUpdateCarriedItemServer;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicDataComponentTypes;
import voltaic.registers.VoltaicSounds;

public class ScreenComponentGasGauge extends ScreenComponentGeneric {

    public static final ResourceLocation TEXTURE = Voltaic.rl("textures/screen/component/gas.png");

    public final Supplier<IGasTank> gasTank;

    public ScreenComponentGasGauge(Supplier<IGasTank> gasStack, int x, int y) {
	super(GasGaugeTextures.BACKGROUND_DEFAULT, x, y);
	gasTank = gasStack;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
	super.renderBackground(graphics, xAxis, yAxis, guiWidth, guiHeight);

	IGasTank tank = gasTank.get();

	if (tank != null) {

	    renderMercuryTexture(graphics, guiWidth + xLocation + 1, guiHeight + yLocation + 1,
		    (float) tank.getGasAmount() / (float) tank.getCapacity());

	}

	GasGaugeTextures texture = GasGaugeTextures.LEVEL_DEFAULT;

	graphics.blit(texture.getLocation(), guiWidth + xLocation, guiHeight + yLocation + 1, texture.textureU(),
		texture.textureV(), texture.textureWidth(), texture.textureHeight(), texture.imageWidth(),
		texture.imageHeight());

    }

    @Override
    public void renderForeground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
	if (isPointInRegion(xLocation, yLocation, xAxis, yAxis, super.texture.textureWidth(),
		super.texture.textureHeight())) {

	    IGasTank tank = gasTank.get();

	    List<FormattedCharSequence> tooltips = new ArrayList<>();

	    if (tank == null)
		return;

	    GasStack gas = tank.getGas();

	    if (gas.isEmpty()) {

		tooltips.add(VoltaicTextUtils
			.ratio(Component.literal("0"), ChatFormatter.formatFluidMilibuckets(tank.getCapacity()))
			.withStyle(ChatFormatting.GRAY).getVisualOrderText());

	    } else {

		tooltips.add(gas.getGas().getDescription().getVisualOrderText());
		tooltips.add(VoltaicTextUtils
			.ratio(ChatFormatter.formatFluidMilibuckets(tank.getGasAmount()),
				ChatFormatter.formatFluidMilibuckets(tank.getCapacity()))
			.withStyle(ChatFormatting.GRAY).getVisualOrderText());
		tooltips.add(ChatFormatter.getChatDisplayShort(gas.getTemperature(), DisplayUnits.TEMPERATURE_KELVIN)
			.withStyle(ChatFormatting.GRAY).getVisualOrderText());
		tooltips.add(ChatFormatter.getChatDisplayShort(gas.getPressure(), DisplayUnits.PRESSURE_ATM)
			.withStyle(ChatFormatting.GRAY).getVisualOrderText());

	    }

	    graphics.renderTooltip(requireScreen().getFontRenderer(), tooltips, xAxis, yAxis);

	}
    }

    public static void renderMercuryTexture(GuiGraphics graphics, int x, int y, float progress) {

	TextureAtlasSprite mercury = VoltaicClientRegister.getSprite(VoltaicClientRegister.TEXTURE_MERCURY);

	Matrix4f matrix = graphics.pose().last().pose();

	RenderingUtils.bindTexture(mercury.atlasLocation());

	int height = (int) (progress * 47);

	int x1 = x;
	int x2 = x1 + 12;

	int y1 = y + 47 - height;
	int y2 = y1 + height;

	float minU = mercury.getU0();
	float maxU = mercury.getU1();

	float minV = mercury.getV0();
	float maxV = mercury.getV1();

	float deltaV = maxV - minV;

	minV = maxV - deltaV * progress;

	RenderSystem.setShader(GameRenderer::getPositionTexShader);
	BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
		DefaultVertexFormat.POSITION_TEX);
	bufferbuilder.addVertex(matrix, x1, y2, 0).setUv(minU, maxV).addVertex(matrix, x2, y2, 0).setUv(maxU, maxV)
		.addVertex(matrix, x2, y1, 0).setUv(maxU, minV).addVertex(matrix, x1, y1, 0).setUv(minU, minV);
	BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
	if (isActiveAndVisible() && isValidClick(button) && isInClickRegion(mouseX, mouseY)) {

	    onMouseClick(mouseX, mouseY);

	    return true;
	}
	return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
	if (isValidClick(button)) {
	    onMouseRelease(mouseX, mouseY);
	    return true;
	}
	return false;
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY) {
	PropertyGasTank tank = (PropertyGasTank) gasTank.get();
	if (tank == null)
	    return;

	GenericScreen<?> screen = (GenericScreen<?>) requireScreen();
	GenericContainerBlockEntity<?> menu = (GenericContainerBlockEntity<?>) screen.getMenu();
	GenericTile owner = (GenericTile) menu.getSafeHost().orElse(null);
	if (owner == null)
	    return;

	ItemStack stack = menu.getCarried();
	if (stack.isEmpty() || stack.getOrDefault(VoltaicDataComponentTypes.HASCLICKEDONFLUIDGAUGE, false))
	    return;

	IGasHandlerItem handler = stack.getCapability(VoltaicCapabilities.CAPABILITY_GASHANDLER_ITEM);
	if (handler == null)
	    return;

	GasStack gas = tank.getGas().copy();
	int taken = handler.fill(gas, GasAction.EXECUTE);

	if (taken > 0) {
	    tank.drain(taken, GasAction.EXECUTE);
	    playSound();

	    stack = handler.getContainer();
	    menu.setCarried(stack);
	    sendCarriedItemUpdate(stack, owner);
	    stack.set(VoltaicDataComponentTypes.HASCLICKEDONFLUIDGAUGE, true);
	    return;
	}

	for (int i = 0; i < handler.getTanks(); i++) {
	    gas = handler.getGasInTank(i);
	    taken = tank.fill(gas, GasAction.EXECUTE);

	    if (taken <= 0) {
		continue;
	    }

	    handler.drain(taken, GasAction.EXECUTE);
	    playSound();

	    stack = handler.getContainer();
	    menu.setCarried(stack);
	    sendCarriedItemUpdate(stack, owner);
	    stack.set(VoltaicDataComponentTypes.HASCLICKEDONFLUIDGAUGE, true);
	    return;
	}
    }

    private static void playSound() {
	Minecraft.getInstance().getSoundManager()
		.play(SimpleSoundInstance.forUI(VoltaicSounds.SOUND_PRESSURERELEASE.get(), 1.0F));
    }

    private static void sendCarriedItemUpdate(ItemStack stack, GenericTile owner) {
	Player player = Minecraft.getInstance().player;
	if (player == null)
	    return;

	PacketDistributor
		.sendToServer(new PacketUpdateCarriedItemServer(stack.copy(), owner.getBlockPos(), player.getUUID()));
    }

    public enum GasGaugeTextures implements ITexture {
	BACKGROUND_DEFAULT(14, 49, 0, 0, 256, 256, TEXTURE),
	LEVEL_DEFAULT(14, 49, 14, 0, 256, 256, TEXTURE);

	private final int textureWidth;
	private final int textureHeight;
	private final int textureU;
	private final int textureV;
	private final int imageWidth;
	private final int imageHeight;
	private final ResourceLocation loc;

	GasGaugeTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth,
		int imageHeight, ResourceLocation loc) {
	    this.textureWidth = textureWidth;
	    this.textureHeight = textureHeight;
	    this.textureU = textureU;
	    this.textureV = textureV;
	    this.imageWidth = imageWidth;
	    this.imageHeight = imageHeight;
	    this.loc = loc;
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
