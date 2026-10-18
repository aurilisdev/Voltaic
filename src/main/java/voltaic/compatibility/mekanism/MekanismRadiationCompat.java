package voltaic.compatibility.mekanism;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.radiation.capability.IRadiationShielding;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.ItemCapability;
import voltaic.Voltaic;
import voltaic.registers.VoltaicDataComponentTypes;

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
	if (RadiationShielding.getHazmatDurability(stack) > 0) {
	    shielding = armorShielding;
	}
	return RadiationShielding.consumeEnergy(stack, shielding, damageChance);
    }

    // Keep Mekanism's API classes unloaded when the optional mod is absent.
    private static final class RadiationShielding {

	private static final int DEFAULT_HAZMAT_DURABILITY = 26000 * 5;
	private static final ItemCapability<IRadiationShielding, Void> CAPABILITY = ItemCapability.createVoid(
		ResourceLocation.fromNamespaceAndPath(Voltaic.MEKANISM_ID, "radiation_shielding"),
		IRadiationShielding.class);
	private static final ItemCapability<IStrictEnergyHandler, Void> ENERGY_CAPABILITY = ItemCapability.createVoid(
		ResourceLocation.fromNamespaceAndPath(Voltaic.MEKANISM_ID, "strict_energy_handler"),
		IStrictEnergyHandler.class);

	private static double getRadiationShielding(ItemStack stack) {
	    IRadiationShielding shielding = stack.getCapability(CAPABILITY);
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

	    IStrictEnergyHandler energy = stack.getCapability(ENERGY_CAPABILITY);
	    if (!(energy instanceof IMekanismStrictEnergyHandler handler))
		return 0.0;

	    IEnergyContainer container = handler.getEnergyContainer(0, null);
	    if (container == null || container.isEmpty())
		return 0.0;

	    double expectedDamage = Math.min(1.0, damageChance) * (int) Math.ceil(damageChance);
	    double pendingEnergy = stack.getOrDefault(VoltaicDataComponentTypes.MEKASUIT_RADIATION_ENERGY_REMAINDER,
		    0.0);
	    if (!Double.isFinite(pendingEnergy) || pendingEnergy < 0.0 || pendingEnergy >= 1.0) {
		pendingEnergy = 0.0;
	    }
	    double exactEnergyCost = expectedDamage / hazmatDurability * container.getMaxEnergy() + pendingEnergy;
	    long energyCost = (long) exactEnergyCost;
	    double remainder = energyCost == Long.MAX_VALUE ? 0.0 : exactEnergyCost - energyCost;
	    if (energyCost <= 0) {
		stack.set(VoltaicDataComponentTypes.MEKASUIT_RADIATION_ENERGY_REMAINDER, remainder);
		return shielding;
	    }
	    long extracted = container.extract(energyCost, Action.EXECUTE, AutomationType.MANUAL);
	    stack.set(VoltaicDataComponentTypes.MEKASUIT_RADIATION_ENERGY_REMAINDER,
		    extracted < energyCost ? 0.0 : remainder);
	    return shielding * (extracted / (double) energyCost);
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

	    return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("nuclearscience", hazmat))
		    .map(armor -> armor.getDefaultInstance().getMaxDamage()).filter(durability -> durability > 0)
		    .orElse(DEFAULT_HAZMAT_DURABILITY);
	}
    }
}
