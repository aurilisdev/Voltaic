package voltaic.prefab.screen.component.types;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import voltaic.Voltaic;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;

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
        if (!isHeld && isPointInSlider(this.xLocation, this.yLocation, mouseX - this.gui.getGuiWidth(), mouseY - this.gui.getGuiHeight(), this.width, this.height) && sliderClickConsumer != null) {
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
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        ITexture bg = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_BACKGROUND;

        graphics.blit(bg.getLocation(), guiWidth + xLocation - 1, guiHeight + yLocation, 1, height, bg.textureU(), bg.textureV(), 1, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        int permutations = (int) ((width - 2) / 28.0D);

        int remainder = width - permutations * 28 - 2;

        for (int i = 0; i < permutations; i++) {

            graphics.blit(bg.getLocation(), guiWidth + xLocation + i * 28, guiHeight + yLocation, 28, height, bg.textureU() + 1, bg.textureV(), 28, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        }

        graphics.blit(bg.getLocation(), guiWidth + xLocation + 28 * permutations, guiHeight + yLocation, remainder, height, bg.textureU() + 1, bg.textureV(), remainder, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        graphics.blit(bg.getLocation(), guiWidth + xLocation + width - 2, guiHeight + yLocation, 1, height, bg.textureU() + 29, bg.textureV(), 1, bg.textureHeight(), bg.imageWidth(), bg.imageHeight());

        ITexture slider;

        if (active) {

            slider = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_ACTIVE;

        } else {

            slider = ScreenComponentHorizontalSlider.HorizontalSliderTextures.SLIDER_INACTIVE;

        }

        graphics.blit(slider.getLocation(), guiWidth + xLocation + sliderXOffset , guiHeight + yLocation + 1, slider.textureWidth(), slider.textureHeight(), slider.textureU(), slider.textureV(), slider.textureWidth(), slider.textureHeight(), slider.imageWidth(), slider.imageHeight());

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
