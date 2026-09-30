package voltaic.prefab.tile.components.utils;

import java.util.Collections;
import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

/**
 * Populates configured resource validators from direct values and registry
 * tags.
 */
public class RegistryValidatorUtils {

    public static <T> void populate(Set<T> validators, T[] values, TagKey<T>[] tags, Registry<T> registry) {
	Collections.addAll(validators, values);
	for (TagKey<T> tag : tags) {
	    registry.getTag(tag).orElseThrow().forEach(holder -> validators.add(holder.value()));
	}
    }
}
