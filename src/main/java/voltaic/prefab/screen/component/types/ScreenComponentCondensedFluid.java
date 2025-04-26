package voltaic.prefab.screen.component.types;

import java.util.function.Supplier;

import voltaic.common.packet.NetworkHandler;
import voltaic.common.packet.types.server.PacketUpdateCarriedItemServer;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.tile.GenericTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ScreenComponentCondensedFluid extends ScreenComponentGeneric {

    private final Supplier<SingleProperty<FluidStack>> fluidPropertySupplier;

    public ScreenComponentCondensedFluid(Supplier<SingleProperty<FluidStack>> fluidStackSupplier, int x, int y) {
        super(IconType.FLUID_DARK, x, y);
        this.fluidPropertySupplier = fluidStackSupplier;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        super.renderBackground(graphics, xAxis, yAxis, guiWidth, guiHeight);

        SingleProperty<FluidStack> fluidProperty = fluidPropertySupplier.get();

        if (fluidProperty == null || fluidProperty.getValue().isEmpty()) {
            return;
        }

        IconType fluidFull = IconType.FLUID_BLUE;

        graphics.blit(fluidFull.getLocation(), guiWidth + xLocation + 1, guiHeight + yLocation + 1, fluidFull.textureU(), fluidFull.textureV(), fluidFull.textureWidth(), fluidFull.textureHeight(), fluidFull.imageWidth(), fluidFull.imageHeight());
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

        SingleProperty<FluidStack> fluidProperty = fluidPropertySupplier.get();

        if (fluidProperty == null || fluidProperty.getValue().isEmpty()) {
            return;
        }

        FluidStack fluidStack = fluidProperty.getValue();

        GenericScreen<?> screen = (GenericScreen<?>) gui;

        GenericTile owner = (GenericTile) ((GenericContainerBlockEntity<?>) screen.getMenu()).getSafeHost();

        if (owner == null) {
            return;
        }

        ItemStack stack = screen.getMenu().getCarried();

        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);

        if (handler == null) {
            return;
        }

        int taken = handler.fill(fluidStack, FluidAction.EXECUTE);

        if (taken <= 0) {
            return;
        }

        fluidStack.shrink(taken);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_FILL, 1.0F));

        stack = handler.getContainer();
        
        NetworkHandler.CHANNEL.sendToServer(new PacketUpdateCarriedItemServer(stack.copy(), owner.getBlockPos(), Minecraft.getInstance().player.getUUID()));

    }

}
