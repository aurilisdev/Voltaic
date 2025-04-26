package voltaic.common.fluid;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import voltaic.client.misc.SWBFClientExtensions;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

public class SimpleWaterBasedFluidType extends FluidType {

    public static final Color DEFAULT_COLOR_TINT = new Color(63, 118, 228, 255);

    public final ResourceLocation texture;
    public final Color color;
    private final Fluid fluid;

    public SimpleWaterBasedFluidType(Fluid fluid, String modId, String id, String texture, Color color) {
        super(FluidType.Properties.create().descriptionId("fluid." + modId + "." + id).fallDistanceModifier(0F).canExtinguish(true).canConvertToSource(true).supportsBoating(true).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH).canHydrate(true));
        this.texture = new ResourceLocation(modId, "block/fluid/" + texture);
        this.color = color;
        this.fluid = fluid;
    }

    public SimpleWaterBasedFluidType(Fluid fluid, String modId, String fluidName, String texture) {
        this(fluid, modId, fluidName, texture, DEFAULT_COLOR_TINT);
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
        return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
    }
    
    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    	consumer.accept(new SWBFClientExtensions((SimpleWaterBasedFluidType) fluid.getFluidType()));
    }

}
