package voltaic.compatibility.mekanism;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import voltaic.registers.VoltaicDataComponentTypes;

/**
 * Custom integrity for Mekanism's ordinary hazmat, which has no native
 * durability. This value affects Voltaic radiation shielding only.
 */
public final class MekanismHazmatIntegrity {

    private static final int FALLBACK_DURABILITY = 26000 * 5;

    public static boolean isHazmat(ItemStack stack) {
	return !referenceArmor(stack).isEmpty();
    }

    public static double getIntegrity(ItemStack stack) {
	double integrity = stack.getOrDefault(VoltaicDataComponentTypes.MEKANISM_HAZMAT_INTEGRITY, 1.0);
	// Missing data means new equipment. Invalid stored values fail closed.
	if (!Double.isFinite(integrity)) {
	    return 0.0;
	}
	return Math.max(0.0, Math.min(1.0, integrity));
    }

    public static double applyRadiationShielding(ItemStack stack, float damageChance, double armorShielding) {
	double integrity = getIntegrity(stack);
	if (integrity <= 0.0) {
	    return 0.0;
	}

	// Bill fractional expected wear rather than randomly discarding small doses.
	if (damageChance > 0.0f) {
	    double expectedWear = Math.min(1.0, damageChance) * Math.ceil(damageChance);
	    double remaining = Math.max(0.0, integrity - expectedWear / getReferenceDurability(stack));
	    stack.set(VoltaicDataComponentTypes.MEKANISM_HAZMAT_INTEGRITY, remaining);
	}
	return armorShielding;
    }

    private static int getReferenceDurability(ItemStack stack) {
	String reference = referenceArmor(stack);
	if (reference.isEmpty()) {
	    return FALLBACK_DURABILITY;
	}
	return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("nuclearscience", reference))
		.map(item -> item.getDefaultInstance().getMaxDamage()).filter(durability -> durability > 0)
		.orElse(FALLBACK_DURABILITY);
    }

    private static String referenceArmor(ItemStack stack) {
	if (stack.isEmpty()) {
	    return "";
	}
	ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
	if (!"mekanism".equals(id.getNamespace())) {
	    return "";
	}
	return switch (id.getPath()) {
	case "hazmat_mask" -> "reinforcedhazmathelmet";
	case "hazmat_gown" -> "reinforcedhazmatplate";
	case "hazmat_pants" -> "reinforcedhazmatlegs";
	case "hazmat_boots" -> "reinforcedhazmatboots";
	default -> "";
	};
    }
}
