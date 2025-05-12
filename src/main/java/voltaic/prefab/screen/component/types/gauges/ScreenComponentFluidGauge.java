package voltaic.prefab.screen.component.types.gauges;

import java.util.ArrayList;
import java.util.List;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.fluid.PropertyFluidTank;
import voltaic.api.screen.component.FluidTankSupplier;
import voltaic.common.packet.NetworkHandler;
import voltaic.common.packet.types.server.PacketUpdateCarriedItemServer;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@OnlyIn(Dist.CLIENT)
public class ScreenComponentFluidGauge extends AbstractScreenComponentGauge {
	public FluidTankSupplier fluidInfoHandler;

	public ScreenComponentFluidGauge(FluidTankSupplier fluidInfoHandler, int x, int y) {
		super(x, y);
		this.fluidInfoHandler = fluidInfoHandler;
	}

	@Override
	protected int getScaledLevel() {
		IFluidTank tank = fluidInfoHandler.getTank();
		if (tank != null) {
			if (tank.getFluidAmount() > 0 && tank.getCapacity() > 0) {
				return tank.getFluidAmount() * (GaugeTextures.BACKGROUND_DEFAULT.textureHeight() - 2) / tank.getCapacity();
			}
		}

		return 0;
	}

	@Override
	protected void applyColor() {
		IFluidTank tank = fluidInfoHandler.getTank();
		if (tank != null) {
			FluidStack fluidStack = tank.getFluid();
			if (!fluidStack.isEmpty()) {
				FluidAttributes extensions = fluidStack.getFluid().getAttributes();
				RenderingUtils.setShaderColor(new Color(extensions.getColor(fluidStack)));
			}
		}
	}

	@Override
	protected ResourceLocation getTexture() {
		IFluidTank tank = fluidInfoHandler.getTank();
		if (tank != null) {
			FluidStack fluidStack = tank.getFluid();
			FluidAttributes extensions = fluidStack.getFluid().getAttributes();
			return extensions.getStillTexture();
		}
		return texture.getLocation();
	}

	@Override
	protected List<? extends IReorderingProcessor> getTooltips() {
		List<IReorderingProcessor> tooltips = new ArrayList<>();
		IFluidTank tank = fluidInfoHandler.getTank();
		if (tank != null) {
			FluidStack fluidStack = tank.getFluid();
			if (fluidStack.getAmount() > 0) {
				tooltips.add(new TranslationTextComponent(fluidStack.getTranslationKey()).getVisualOrderText());
				tooltips.add(VoltaicTextUtils.ratio(ChatFormatter.formatFluidMilibuckets(tank.getFluidAmount()), ChatFormatter.formatFluidMilibuckets(tank.getCapacity())).withStyle(TextFormatting.GRAY).getVisualOrderText());
			} else {
				tooltips.add(VoltaicTextUtils.ratio(new StringTextComponent("0"), ChatFormatter.formatFluidMilibuckets(tank.getCapacity())).withStyle(TextFormatting.GRAY).getVisualOrderText());
			}
		}
		return tooltips;
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
		PropertyFluidTank tank = null;
		
		if(fluidInfoHandler.getTank() instanceof PropertyFluidTank) {
			tank = (PropertyFluidTank) fluidInfoHandler.getTank();
		}

		if (tank == null) {
			return;
		}

		GenericScreen<?> screen = (GenericScreen<?>) gui;

		GenericTile owner = (GenericTile) ((GenericContainerBlockEntity<?>) screen.getMenu()).getSafeHost();

		if (owner == null) {
			return;
		}

		ItemStack stack = Minecraft.getInstance().player.inventory.getCarried();

		IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(CapabilityUtils.EMPTY_FLUID_ITEM);

		if(handler == CapabilityUtils.EMPTY_FLUID_ITEM) {
			return;
		}

		FluidStack drainedSourceFluid = tank.getFluid().copy();

		int taken = handler.fill(drainedSourceFluid, FluidAction.EXECUTE);

		//drain this fluid gauge if the amount taken was greater than zero
		if (taken > 0) {

			tank.drain(taken, FluidAction.EXECUTE);

			Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.BUCKET_FILL, 1.0F));

			stack = handler.getContainer();
			
			NetworkHandler.CHANNEL.sendToServer(new PacketUpdateCarriedItemServer(stack.copy(), ((GenericContainerBlockEntity<?>) screen.getMenu()).getSafeHost().getBlockPos(), Minecraft.getInstance().player.getUUID()));

			return;

		}
		//we didn't drain the gauge, now we try to fill it

		for(int i = 0; i < handler.getTanks(); i++){
			drainedSourceFluid = handler.getFluidInTank(i);
			taken = tank.fill(drainedSourceFluid, FluidAction.EXECUTE);
			if(taken <= 0) {
				continue;
			}
			handler.drain(taken, FluidAction.EXECUTE);

			Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.BUCKET_EMPTY, 1.0F));

			stack = handler.getContainer();
			
			NetworkHandler.CHANNEL.sendToServer(new PacketUpdateCarriedItemServer(stack.copy(), ((GenericContainerBlockEntity<?>) screen.getMenu()).getSafeHost().getBlockPos(), Minecraft.getInstance().player.getUUID()));

			return;
		}
		
	}	

}