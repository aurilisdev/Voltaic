package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import voltaic.Voltaic;
import voltaic.api.codec.StreamCodec;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

public class ProbableFluid {

    public static final Codec<ProbableFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //
            FluidStack.CODEC.fieldOf("fluid").forGetter(instance0 -> instance0.fluid),
            //
            Codec.DOUBLE.fieldOf("chance").forGetter(instance0 -> instance0.chance)

    )
            //
            .apply(instance, (fluid, chance) -> new ProbableFluid(fluid, chance))

    //
    );

    public static final Codec<List<ProbableFluid>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<PacketBuffer, ProbableFluid> STREAM_CODEC = new StreamCodec<PacketBuffer, ProbableFluid>() {
        @Override
        public ProbableFluid decode(PacketBuffer buf) {
            return new ProbableFluid(StreamCodec.FLUID_STACK.decode(buf), buf.readDouble());
        }

        @Override
        public void encode(PacketBuffer buf, ProbableFluid fluid) {
            StreamCodec.FLUID_STACK.encode(buf, fluid.fluid);
            buf.writeDouble(fluid.chance);
        }
    };

    public static final StreamCodec<PacketBuffer, List<ProbableFluid>> LIST_STREAM_CODEC = new StreamCodec<PacketBuffer, List<ProbableFluid>>() {

        @Override
        public void encode(PacketBuffer buf, List<ProbableFluid> probable) {
            buf.writeInt(probable.size());
            for (ProbableFluid fluid : probable) {
                STREAM_CODEC.encode(buf, fluid);
            }
        }

        @Override
        public List<ProbableFluid> decode(PacketBuffer buf) {
            int count = buf.readInt();
            List<ProbableFluid> fluids = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fluids.add(STREAM_CODEC.decode(buf));
            }
            return fluids;
        }
    };

    public static final List<ProbableFluid> NONE = new ArrayList<>();

    private FluidStack fluid;
    // 0: 0% chance
    // 1: 100% chance
    private double chance;

    public ProbableFluid(FluidStack stack, double chance) {
        fluid = stack;
        setChance(chance);
    }

    public FluidStack getFullStack() {
        return fluid;
    }

    private void setChance(double chance) {
        this.chance = chance > 1 ? 1 : chance < 0 ? 0 : chance;
    }

    public double getChance() {
        return chance;
    }

    public FluidStack roll() {
        double random = Voltaic.RANDOM.nextDouble();
        if (random > 1 - chance) {
            double amount = chance >= 1 ? fluid.getAmount() : fluid.getAmount() * random;
            int fluidAmount = (int) Math.ceil(amount);
            return new FluidStack(fluid.getFluid(), fluidAmount);
        }
        return FluidStack.EMPTY;
    }

}
