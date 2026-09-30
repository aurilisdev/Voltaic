package voltaic.common.recipe.recipeutils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import voltaic.api.gas.GasStack;
import voltaic.registers.VoltaicGases;

public class ProbableGas extends AbstractProbable<GasStack> {

    public static final Codec<ProbableGas> CODEC = RecordCodecBuilder.create(instance -> instance
	    .group(VoltaicGases.GAS_REGISTRY.byNameCodec().fieldOf("gas")
		    .forGetter(instance0 -> instance0.stack.getGas()),
		    Codec.INT.fieldOf("amount").forGetter(instance0 -> instance0.stack.getAmount()),
		    Codec.INT.fieldOf("temp").forGetter(instance0 -> instance0.stack.getTemperature()),
		    Codec.INT.fieldOf("pressure").forGetter(instance0 -> instance0.stack.getPressure()),
		    Codec.DOUBLE.fieldOf("chance").forGetter(ProbableGas::getChance))
	    .apply(instance,
		    (gas, amt, temp, pres, chance) -> new ProbableGas(new GasStack(gas, amt, temp, pres), chance)));

    public static final Codec<List<ProbableGas>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, ProbableGas> STREAM_CODEC = new StreamCodec<>() {

	@Override
	public void encode(RegistryFriendlyByteBuf buf, ProbableGas gas) {
	    GasStack.STREAM_CODEC.encode(buf, gas.getFullStack());
	    buf.writeDouble(gas.getChance());
	}

	@Override
	public ProbableGas decode(RegistryFriendlyByteBuf buf) {
	    return new ProbableGas(GasStack.STREAM_CODEC.decode(buf), buf.readDouble());
	}
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ProbableGas>> LIST_STREAM_CODEC = new StreamCodec<>() {
	@Override
	public List<ProbableGas> decode(RegistryFriendlyByteBuf buf) {
	    int count = buf.readInt();
	    List<ProbableGas> fluids = new ArrayList<>();
	    for (int i = 0; i < count; i++) {
		fluids.add(STREAM_CODEC.decode(buf));
	    }
	    return fluids;
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf, List<ProbableGas> probable) {
	    buf.writeInt(probable.size());
	    for (ProbableGas gas : probable) {
		STREAM_CODEC.encode(buf, gas);
	    }
	}
    };

    public static final List<ProbableGas> NONE = List.of();

    public ProbableGas(GasStack stack, double chance) {
	super(stack, chance);
    }

    public GasStack roll() {
	double random = nextRoll();
	if (passesChance(random)) {
	    int amount = isGuaranteed() ? stack.getAmount() : (int) (stack.getAmount() * random);
	    return new GasStack(stack.getGas(), amount, stack.getTemperature(), stack.getPressure());
	}
	return GasStack.EMPTY;
    }

}
