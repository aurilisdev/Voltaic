package voltaic.prefab.screen.component.button;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;

/**
 * A modification of the vanilla button to integrate it with the Electrodynamics system of doing GUI components as the
 * Button class has several annoying issues
 * 
 * @author skip999
 *
 * @param <T>
 */
public class ScreenComponentButton<T extends ScreenComponentButton<?>> extends ScreenComponentGeneric {

    public boolean isPressed = false;

    public final boolean isVanillaRender;
    @Nullable
    public OnPress onPress = null;

    @Nullable
    public Supplier<IFormattableTextComponent> label = null;

    public SoundEvent pressSound = SoundEvents.UI_BUTTON_CLICK;

    public ScreenComponentButton(ITexture texture, int x, int y) {
        super(texture, x, y);
        isVanillaRender = false;
    }

    public ScreenComponentButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        isVanillaRender = true;
        this.width = width;
        this.height = height;
    }

    public T setLabel(IFormattableTextComponent label) {
        return setLabel(() -> label);
    }

    public T setLabel(Supplier<IFormattableTextComponent> label) {
        this.label = label;
        return (T) this;
    }

    public T setOnPress(OnPress onPress) {
        this.onPress = onPress;
        return (T) this;
    }

    @Override
    public T onTooltip(OnTooltip onTooltip) {
        this.onTooltip = onTooltip;
        return (T) this;
    }

    public T setPressSound(SoundEvent sound) {
        pressSound = sound;
        return (T) this;
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (isVanillaRender && isVisible()) {
            
        	Minecraft minecraft = Minecraft.getInstance();
            
        	ITexture buttonTexture = isHovered() ? VanillaButtonTextures.BUTTON_ON : VanillaButtonTextures.BUTTON_OFF;
            
        	RenderingUtils.setShaderColor(color);
        	
            RenderingUtils.bindTexture(buttonTexture.getLocation());
            
            ScreenComponentEditBox.drawExpandedBox(poseStack, x + guiWidth, y + guiHeight, width, height);
            
            if(icon != null) {
                int xOffset = (width - icon.imageWidth()) / 2;
                int yOffset = (height - icon.imageHeight()) / 2;
                RenderingUtils.bindTexture(icon.getLocation());
                blit(poseStack, guiWidth + x + xOffset, guiHeight + y + yOffset, icon.textureU(), icon.textureV(), icon.textureWidth(), icon.textureHeight(), icon.imageWidth(), icon.imageHeight());
            }
            FontRenderer font = minecraft.font;
            if (label != null) {
                drawCenteredString(poseStack, font, label.get(), this.x + guiWidth + this.width / 2, this.y + guiHeight + (this.height - 8) / 2, color.color());
            }
            RenderingUtils.resetShaderColor();
        } else {
            super.renderBackground(poseStack, xAxis, yAxis, guiWidth, guiHeight);
        }
    }

    public int getVanillaYImage(boolean isMouseOver) {
        if (!isVisible()) {
            return 0;
        }
        if (isMouseOver) {
            return 2;
        }

        return 1;
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
        if (onPress != null) {
            onPress();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isActiveAndVisible()) {
            return false;
        }
        if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
        }
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        this.onPress();
        return true;
    }

    public void onPress() {
        onPress.onPress(this);
        playDownSound(Minecraft.getInstance().getSoundManager());
    }

    public boolean isValidMouseClick(int button) {
        return button == 0;
    }

    public void playDownSound(SoundHandler soundManager) {
		soundManager.play(SimpleSound.forUI(pressSound, 1.0F));
	}

    public static interface OnPress {

        public void onPress(ScreenComponentButton<?> button);

    }
    
    public static enum VanillaButtonTextures implements ITexture {
		BUTTON_OFF(18, 18, 0, 0, 18, 18, "vanilla_button_off"), BUTTON_ON(18, 18, 0, 0, 18, 18, "vanilla_button_on");
    	
    	private final int textureWidth;
		private final int textureHeight;
		private final int textureU;
		private final int textureV;
		private final int imageWidth;
		private final int imageHeight;
		private final ResourceLocation loc;

		VanillaButtonTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, String name) {
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			this.textureU = textureU;
			this.textureV = textureV;
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			loc = Voltaic.rl("textures/screen/component/button/" + name + ".png");
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
