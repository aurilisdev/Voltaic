package voltaic.prefab.screen.component.types;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.utilities.RenderingUtils;
import net.minecraftforge.fluids.FluidStack;

public class ScreenComponentCondensedFluid extends ScreenComponentGeneric {

    private final Supplier<SingleProperty<FluidStack>> fluidPropertySupplier;

    public ScreenComponentCondensedFluid(Supplier<SingleProperty<FluidStack>> fluidStackSupplier, int x, int y) {
        super(IconType.FLUID_DARK, x, y);
        this.fluidPropertySupplier = fluidStackSupplier;
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        super.renderBackground(poseStack, xAxis, yAxis, guiWidth, guiHeight);

        SingleProperty<FluidStack> fluidProperty = fluidPropertySupplier.get();

        if (fluidProperty == null || fluidProperty.getValue().isEmpty()) {
            return;
        }

        IconType fluidFull = IconType.FLUID_BLUE;
        
        RenderingUtils.bindTexture(fluidFull.getLocation());

        blit(poseStack, guiWidth + x + 1, guiHeight + y + 1, fluidFull.textureU(), fluidFull.textureV(), fluidFull.textureWidth(), fluidFull.textureHeight(), fluidFull.imageWidth(), fluidFull.imageHeight());
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

}
