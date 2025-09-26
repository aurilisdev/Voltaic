package voltaic.prefab.screen.component.types;

import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.utilities.RenderingUtils;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.ResourceLocation;

public class ScreenComponentHorizontalSlider extends ScreenComponentGeneric {
    private int sliderXOffset = 0;
    private boolean active = false;

    private boolean isHeld = false;

    private Consumer<Integer> sliderDragConsumer;
    private Consumer<Integer> sliderClickConsumer;

    public ScreenComponentHorizontalSlider(int x, int y, int width) {
        super(x, y, Math.max(width, 30), 14);
    }

    public ScreenComponentHorizontalSlider setDragConsumer(Consumer<Integer> responder) {
        sliderDragConsumer = responder;
        return this;
    }

    public ScreenComponentHorizontalSlider setClickConsumer(Consumer<Integer> responder) {
        sliderClickConsumer = responder;
        return this;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isValidClick(button)) {
            this.onMouseDrag(mouseX, mouseY, dragX, dragY);
            return true;
        }
        return false;
    }

    @Override
    public void onMouseDrag(double mouseX, double mouseY, double dragX, double dragY) {
        isHeld = true;
        if (sliderDragConsumer != null) {
            sliderDragConsumer.accept((int) (mouseX - this.gui.getGuiWidth()));
        }
        super.onMouseDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isHeld && isPointInSlider(this.x, this.y, mouseX - this.gui.getGuiWidth(), mouseY - this.gui.getGuiHeight(), this.width, this.height) && sliderClickConsumer != null) {
            sliderClickConsumer.accept((int) (mouseX - this.gui.getGuiWidth()));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isHeld = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean isPointInRegion(int x, int y, double xAxis, double yAxis, int width, int height) {
        return xAxis >= x + sliderXOffset + 2 && xAxis <= (x + 2 + sliderXOffset + 15) && yAxis >= y && yAxis <= (y + height - 1);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        ITexture bg = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_BACKGROUND;
        
        RenderingUtils.bindTexture(bg.getLocation());

        blit(matrixStack, guiWidth + x - 1, guiHeight + y, 1, height, bg.textureU(), bg.textureV(), 1, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        int permutations = (int) ((width - 2) / 28.0D);

        int remainder = width - permutations * 28 - 2;

        for (int i = 0; i < permutations; i++) {

        	blit(matrixStack, guiWidth + x + i * 28, guiHeight + y, 28, height, bg.textureU() + 1, bg.textureV(), 28, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        }

        blit(matrixStack, guiWidth + x + 28 * permutations, guiHeight + y, remainder, height, bg.textureU() + 1, bg.textureV(), remainder, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        blit(matrixStack, guiWidth + x + width - 2, guiHeight + y, 1, height, bg.textureU() + 29, bg.textureV(), 1, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        ITexture slider;

        if (active) {

            slider = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_ACTIVE;

        } else {

            slider = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_INACTIVE;

        }
        
        RenderingUtils.bindTexture(slider.getLocation());

        blit(matrixStack, guiWidth + x + sliderXOffset , guiHeight + y + 1, slider.textureWidth(), slider.textureHeight(), slider.textureU(), slider.textureV(), slider.textureWidth(), slider.textureHeight(), slider.imageWidth(), slider.imageHeight());

    }

    public void updateActive(boolean active) {
        this.active = active;
    }

    public void setSliderXOffset(int offset) {
        sliderXOffset = Math.min(offset, width - 2 - 15);
    }

    protected boolean isPointInSlider(int x, int y, double xAxis, double yAxis, int width, int height) {
        return xAxis >= x && xAxis <= (x + width - 1) && yAxis >= y && yAxis <= (y + height - 1);
    }

    public boolean isSliderActive() {
        return active;
    }

    public boolean isSliderHeld() {
        return isHeld;
    }

    public static enum HorizontalSliderTextures implements ITexture {
        SLIDER_BACKGROUND(30, 14, 0, 0, 30, 14, Voltaic.rl("textures/screen/component/horizontalslider/horizontal_slider_bg.png")),
        SLIDER_ACTIVE(15, 12, 0, 0, 15, 12, Voltaic.rl("textures/screen/component/horizontalslider/horizontal_slider_active.png")),
        SLIDER_INACTIVE(15, 12, 0, 0, 15, 12, Voltaic.rl("textures/screen/component/horizontalslider/horizontal_slider_inactive.png"));

        private final int textureWidth;
        private final int textureHeight;
        private final int textureU;
        private final int textureV;
        private final int imageWidth;
        private final int imageHeight;
        private final ResourceLocation loc;

        private HorizontalSliderTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, ResourceLocation loc) {
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
            return this.loc;
        }

        @Override
        public int imageHeight() {
            return this.imageHeight;
        }

        @Override
        public int imageWidth() {
            return this.imageWidth;
        }

        @Override
        public int textureHeight() {
            return this.textureHeight;
        }

        @Override
        public int textureU() {
            return this.textureU;
        }

        @Override
        public int textureV() {
            return this.textureV;
        }

        @Override
        public int textureWidth() {
            return this.textureWidth;
        }

    }
}
