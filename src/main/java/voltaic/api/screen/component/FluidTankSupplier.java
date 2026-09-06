package voltaic.api.screen.component;

import javax.annotation.Nullable;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.IFluidTank;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface FluidTankSupplier {
    @Nullable
    IFluidTank getTank();
}