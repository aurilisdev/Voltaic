package voltaic.compatibility.mekanism;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.api.radiation.capability.IRadiationShielding;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.ModList;
import voltaic.Voltaic;

public final class MekanismRadiationCompat {

    private static final boolean MEKANISM_LOADED = ModList.get().isLoaded(Voltaic.MEKANISM_ID);

    private MekanismRadiationCompat() {
    }

    public static double getRadiationShielding(ItemStack stack) {
	if (!MEKANISM_LOADED || stack.isEmpty()) {
	    return 0.0;
	}
	return RadiationShielding.getRadiationShielding(stack);
    }

    public static double applyRadiationShielding(ItemStack stack, float damageChance, double armorShielding) {
	double shielding = getRadiationShielding(stack);
	if (shielding <= 0.0) {
	    return 0.0;
	}
	if (MekanismHazmatIntegrity.isHazmat(stack)) {
	    return MekanismHazmatIntegrity.applyRadiationShielding(stack, damageChance, armorShielding);
	}
	if (RadiationShielding.getHazmatDurability(stack) > 0) {
	    shielding = armorShielding;
	}
	return RadiationShielding.consumeEnergy(stack, shielding, damageChance);
    }

    // Keep Mekanism's API classes unloaded when the optional mod is absent.
    private static final class RadiationShielding {

	private static final int DEFAULT_HAZMAT_DURABILITY = 26000 * 5;
	private static final String ENERGY_REMAINDER_KEY = "voltaic:mekasuit_radiation_energy_remainder";
	private static final Capability<IRadiationShielding> CAPABILITY = CapabilityManager
		.get(new CapabilityToken<>() {
		});
	private static final Capability<IStrictEnergyHandler> ENERGY_CAPABILITY = CapabilityManager
		.get(new CapabilityToken<>() {
		});

	private static double getRadiationShielding(ItemStack stack) {
	    IRadiationShielding shielding = stack.getCapability(CAPABILITY).orElse(null);
	    if (shielding == null) {
		return 0.0;
	    }
	    double resistance = shielding.getRadiationShielding();
	    if (!Double.isFinite(resistance)) {
		return 0.0;
	    }
	    return Math.max(0.0, Math.min(1.0, resistance));
	}

	private static double consumeEnergy(ItemStack stack, double shielding, float damageChance) {
	    int hazmatDurability = getHazmatDurability(stack);
	    if (hazmatDurability == 0)
		return shielding;

	    IStrictEnergyHandler energy = stack.getCapability(ENERGY_CAPABILITY).orElse(null);
	    if (!(energy instanceof IMekanismStrictEnergyHandler handler))
		return 0.0;

	    IEnergyContainer container = handler.getEnergyContainer(0, null);
	    if (container == null || container.isEmpty())
		return 0.0;

	    double expectedDamage = Math.min(1.0, damageChance) * (int) Math.ceil(damageChance);
	    double pendingEnergy = stack.hasTag() ? stack.getTag().getDouble(ENERGY_REMAINDER_KEY) : 0.0;
	    if (!Double.isFinite(pendingEnergy) || pendingEnergy < 0.0 || pendingEnergy >= 1.0) {
		pendingEnergy = 0.0;
	    }
	    // Match the original whole joule billing and retain fractional costs in NBT.
	    double exactEnergyCost = expectedDamage / hazmatDurability * container.getMaxEnergy().doubleValue()
		    + pendingEnergy;
	    long energyCost = (long) exactEnergyCost;
	    double remainder = energyCost == Long.MAX_VALUE ? 0.0 : exactEnergyCost - energyCost;
	    if (energyCost <= 0) {
		stack.getOrCreateTag().putDouble(ENERGY_REMAINDER_KEY, remainder);
		return shielding;
	    }
	    double extracted = container.extract(FloatingLong.create(energyCost), Action.EXECUTE, AutomationType.MANUAL)
		    .doubleValue();
	    stack.getOrCreateTag().putDouble(ENERGY_REMAINDER_KEY, extracted < energyCost ? 0.0 : remainder);
	    return shielding * (extracted / energyCost);
	}

	private static int getHazmatDurability(ItemStack stack) {
	    ResourceLocation item = BuiltInRegistries.ITEM.getKey(stack.getItem());
	    if (!Voltaic.MEKANISM_ID.equals(item.getNamespace())) {
		return 0;
	    }
	    String hazmat = switch (item.getPath()) {
	    case "mekasuit_helmet" -> "reinforcedhazmathelmet";
	    case "mekasuit_bodyarmor" -> "reinforcedhazmatplate";
	    case "mekasuit_pants" -> "reinforcedhazmatlegs";
	    case "mekasuit_boots" -> "reinforcedhazmatboots";
	    default -> "";
	    };
	    if (hazmat.isEmpty())
		return 0;

	    return BuiltInRegistries.ITEM.getOptional(new ResourceLocation("nuclearscience", hazmat))
		    .map(armor -> armor.getDefaultInstance().getMaxDamage()).filter(durability -> durability > 0)
		    .orElse(DEFAULT_HAZMAT_DURABILITY);
	}
    }
}
