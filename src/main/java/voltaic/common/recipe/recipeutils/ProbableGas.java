package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import voltaic.Voltaic;
import voltaic.api.codec.StreamCodec;
import voltaic.api.gas.GasStack;
import voltaic.registers.VoltaicRegistries;

public class ProbableGas {

    public static final Codec<ProbableGas> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //
            VoltaicRegistries.gasRegistry().getCodec().fieldOf("gas").forGetter(instance0 -> instance0.gas.getGas()),
            //
            Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.gas.getAmount()),
            //

            Codec.INT.fieldOf("temp").forGetter(instance0 -> instance0.gas.getTemperature()),
            //
            Codec.INT.fieldOf("pressure").forGetter(instance0 -> instance0.gas.getPressure()),
            //
            Codec.DOUBLE.fieldOf("chance").forGetter(instance0 -> instance0.chance))
            //
            .apply(instance, (gas, amt, temp, pres, chance) -> new ProbableGas(new GasStack(gas, amt, temp, pres), chance))
    //
    );

    public static final Codec<List<ProbableGas>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<FriendlyByteBuf, ProbableGas> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(FriendlyByteBuf buf, ProbableGas gas) {
            GasStack.STREAM_CODEC.encode(buf, gas.gas);
            buf.writeDouble(gas.chance);
        }

        @Override
        public ProbableGas decode(FriendlyByteBuf buf) {
            return new ProbableGas(GasStack.STREAM_CODEC.decode(buf), buf.readDouble());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, List<ProbableGas>> LIST_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public List<ProbableGas> decode(FriendlyByteBuf buf) {
            int count = buf.readInt();
            List<ProbableGas> fluids = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                fluids.add(STREAM_CODEC.decode(buf));
            }
            return fluids;
        }

        @Override
        public void encode(FriendlyByteBuf buf, List<ProbableGas> probable) {
            buf.writeInt(probable.size());
            for (ProbableGas gas : probable) {
                STREAM_CODEC.encode(buf, gas);
            }
        }
    };

    public static final List<ProbableGas> NONE = new ArrayList<>();

    private GasStack gas;
    // 0: 0% chance
    // 1: 100% chance
    private double chance;

    public ProbableGas(GasStack stack, double chance) {
        gas = stack;
        setChance(chance);
    }

    public GasStack getFullStack() {
        return gas;
    }

    private void setChance(double chance) {
        this.chance = chance > 1 ? 1 : chance < 0 ? 0 : chance;
    }

    public double getChance() {
        return chance;
    }

    public GasStack roll() {
        double random = Voltaic.RANDOM.nextDouble();
        if (random > 1 - chance) {
            int amount = chance >= 1 ? gas.getAmount() : (int) (gas.getAmount() * random);
            return new GasStack(gas.getGas(), amount, gas.getTemperature(), gas.getPressure());
        }
        return GasStack.EMPTY;
    }

}
