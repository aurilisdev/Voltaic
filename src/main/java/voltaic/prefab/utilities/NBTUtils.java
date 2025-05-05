package voltaic.prefab.utilities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;


public class NBTUtils {

	public static final String TIMER = "timer";
	public static final String MODE = "mode";
	public static final String XP = "xp";
	public static final String USED = "used";
	public static final String ON = "on";
	public static final String SMART = "smart";
	public static final String SIZE = "size";
	public static final String DIRECTION = "dir";
	public static final String LOCATION = "loc";
	public static final String PLATES = "plates";
	public static final String SUCESS = "sucess";
	public static final String PLAYING_SOUND = "false";
	public static final String DIMENSION = "dimension";
	public static final String TEMPERATURE = "temperature";

	public static final String FORTUNE_ENCHANT = "fortuneenchant";
	public static final String SILK_TOUCH_ENCHANT = "silktouchenchant";
	public static final String SPEED_ENCHANT = "speedenchant";

	public static List<Direction> readDirectionList(ItemStack item) {
		List<Direction> dirs = new ArrayList<>();
		CompoundNBT tag = item.getTag();
		int size = tag.getInt(SIZE + DIRECTION);
		for (int i = 0; i < size; i++) {
			dirs.add(Direction.valueOf(tag.getString(DIRECTION + i).toUpperCase()));
		}
		return dirs;
	}

	public static void writeDirectionList(List<Direction> dirs, ItemStack item) {
		int size = dirs.size();
		CompoundNBT tag = item.getTag();
		tag.putInt(SIZE + DIRECTION, size);
		for (int i = 0; i < size; i++) {
			tag.putString(DIRECTION + i, dirs.get(i).getName());
		}
	}

	public static void clearDirectionList(ItemStack item) {
		CompoundNBT tag = item.getTag();
		int size = tag.getInt(SIZE + DIRECTION);
		for (int i = 0; i < size; i++) {
			tag.remove(DIRECTION + i);
		}
		tag.remove(SIZE + DIRECTION);
	}

	public static CompoundNBT writeDimensionToTag(RegistryKey<World> level) {
		CompoundNBT tag = new CompoundNBT();
		tag.putString(DIMENSION, level.location().toString());
		return tag;
	}

	public static RegistryKey<World> readDimensionFromTag(CompoundNBT tag) {
		return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString(DIMENSION)));
	}

}
